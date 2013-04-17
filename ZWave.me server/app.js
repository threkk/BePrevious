var application_root = __dirname;
var express = require("express");
var path = require("path");
var async = require('async');
var fs = require('fs');
var app = express();
var io = require('socket.io');

log4js = require('log4js');
var logger = log4js.getLogger("app");
var cronJob = require('cron').CronJob;
var client = require('./client').client;
var writer = require('./io').writer;
var ftp = require('./io').ftp;

fs.mkdir('logs', 0777, function (err) {
    if (!err) {
        logger.debug("created log directory");
    } else {
        if (err && err.code !== 'EEXIST') {
            throw new Error('Failed to create logging directory');
        }
    }
    log4js.configure('log4js.json', {
        reloadSecs: 300
    });
});

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

var http = require('http');
var server = http.createServer(app),
	io = io.listen(server);

function registerPartialSync(hbs, partialName, filename) {
    var encoding = 'utf8'
    var absolutePath = path.join(__dirname, filename);
    var content = fs.readFileSync(absolutePath, encoding);

    hbs.registerPartial(partialName, content);
}

// Config
app.configure(function () {
    var hbs = require('hbs');

    app.engine('hbs', hbs.__express);

    app.set('views', __dirname + '/views');
    app.set('view engine', 'hbs');

    registerPartialSync(hbs, 'header', 'views/header.hbs');

    app.use(express.bodyParser());
    app.use(express.methodOverride());
    app.use(app.router);
    app.use(express.static(path.join(application_root, "public")));
    app.use(express.errorHandler({
        dumpExceptions: true,
        showStack: true
    }));
});

app.map(require('./client').routes, '/client/');
app.map(require('./dashboard').routes, '/');

var compressJob = new cronJob({
    cronTime: '00 00 02 * * *',
    onTick: function () {
        writer.compressFiles();
    },
    start: false
});
compressJob.start();
var ftpJob = new cronJob({
    cronTime: '00 00 03 * * *',
    onTick: function () {
      ftp.send();  
    },
    start: false
});
ftpJob.start();

//bind socket io to the client
var clientsocket = io.of('/client').on('connection', function (socket) {
    socket.on('start_inclusion', function(data) {
       client.startInclusionMode(data.duration);
    });
    socket.on('start_exclusion', function(data) {
       client.startExclusionMode(data.duration);
    });
    socket.on('stop_inclusion', function(data) {
       client.stopInclusionMode();
    });
    socket.on('stop_exclusion', function(data) {
       client.stopExclusionMode();
    });
});

//start client
client.on('update', function (message) {
    writer.write(message, function (err, status) {
        logger.debug('wrote to file: ' + JSON.stringify(message));
    });
    clientsocket.emit('update', message);
});

client.on('device_added', function(device){
	clientsocket.emit('device_added', device);
});
client.on('device_removed', function(device){
	clientsocket.emit('device_removed', device);
});

// Launch server
server.listen(8080);
logger.info("Application started");