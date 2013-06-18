log4js = require('log4js');
var logger = log4js.getLogger("app");

var application_root = __dirname;
var express = require("express");
var path = require("path");
var fs = require('fs');
var async = require('async');
var app = express();
var hbs = require('hbs');
var io = require('socket.io');
var Hashids = require("hashids"),
    hashids = new Hashids("__BOXLAB__", 12);

var localDB = require('./io').localDB;
var writer = require('./io').writer;
var ftp = require('./io').ftp;
var cronJob = require('cron').CronJob;
var client = require('./client').client;



/**
 * Helper for mapping routes to the app
 */
app.map = function (a, route) {
    route = route || '';
    for (var key in a) {
        switch (typeof a[key]) {
            // { '/path': { ... }}
        case 'object':
            app.map(a[key], route + key);
            break;
            // get: function(){ ... }
        case 'function':
            // log the routes in test
            if (process.env.NODE_ENV != 'test')
                logger.debug('%s %s', key, route);
            app[key](route, a[key]);
            break;
        }
    }
};
/**
 * Helper for registering partial templates for the templating engine
 */
app.registerPartial = function(partialName, filename) {
    var encoding = 'utf8'
    var absolutePath = path.join(__dirname, filename);
    var content = fs.readFileSync(absolutePath, encoding);

    hbs.registerPartial(partialName, content);
}

var http = require('http');
var server = http.createServer(app),
    io = io.listen(server, {
        log: false
    });

// Config
app.configure(function () {
    app.engine('hbs', hbs.__express);

    app.set('views', __dirname + '/views');
    app.set('view engine', 'hbs');

    app.registerPartial('header', 'views/header.hbs');
    app.use(express.bodyParser());
    app.use(express.methodOverride());
    app.use(app.router);
    app.use(express.static(path.join(application_root, "public")));
    app.use(express.errorHandler({
        dumpExceptions: true,
        showStack: true
    }));
    
	async.series([configureLogger, configureIdentity], function(err, results) {
		if (err) {
			console.log('error on startup: ' + JSON.stringify(err));
			setTimeout(function(){process.exit(1)},0);
			return;
		}
		app.map(require('./client').routes, '/client/');
		app.map(require('./dashboard').routes, '/');
		
		// Launch server
		server.listen(8080);
		logger.debug("Application started");
	});
});

function configureLogger(callback) {
	fs.mkdir('logs', 0777, function (err) {
		if (err && err.code !== 'EEXIST') {
            console.log('ERROR: Failed to create logging directory');
			return callback('Failed to create logging directory');
        }

	    log4js.configure('log4js.json', {
	        reloadSecs: 300
	    });
	    
	    callback(null);
	});
}

function configureIdentity(callback) {
	var identity = localDB.getIdentity();
	if (!identity) {
		//create a hash based on the current time
		identity = hashids.encrypt(new Date().getTime());
		localDB.setIdentity(identity);
	}
	logger.debug('Boxlab identity: ' + identity);
	
	callback(null);
}

var ftpJob = new cronJob({
    cronTime: '00 00 03 * * *',
    onTick: function () {
        writer.compressFiles(function (err) {
            if (err) {
                return logger.error('failed to compress files: ' + JSON.stringify(err));
            }
            ftp.send();
        });
    },
    start: false
});
ftpJob.start();

//bind socket io to the client
var clientsocket = io.of('/client');

//start client
client.on('commandupdate', function (message) {
    writer.write(message, function (err, status) {
        logger.debug('wrote to file: ' + JSON.stringify(message));
    });
    clientsocket.emit('update', message);
});

client.on('device_added', function (device) {
    clientsocket.emit('device_added', device);
});
client.on('device_removed', function (device) {
    clientsocket.emit('device_removed', device);
});