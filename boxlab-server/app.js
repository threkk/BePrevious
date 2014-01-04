var express = require("express");
var http = require('http');
var path = require("path");
var fs = require('fs');
var async = require('async');
var app = express();
var hbs = require('hbs');
var io = require('socket.io');

var handler = require('./modules/zwave/commandhandler').handler;

var scheduler = require('./modules/scheduler').scheduler;
var synchronizer = require('./modules/synchronizer');
var writer = require('./modules/writer').writer;
var logger = require('./modules/logging').getLogger('app');
var identification = require('./modules/identification');

/**
 * Helper for mapping routes to the app
 */
app.map = function(a, route) {
	route = route || '';
	for ( var key in a) {
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

/**
 * Helper for starting the boxlab server
 */
app.start = function() {
	var server = http.createServer(app);

	bindWebsockets(server);
	bindWriter();
	scheduleSynchronizer();

	app.map(require('./routes/client').routes, '/client/');
	app.map(require('./routes/dashboard').routes, '/');

	var port = app.get('port');
	server.listen(port, function() {
		logger.debug('Boxlab server started, listening on port ' + port);
		logger.debug('Boxlab device identification: ' + identification.getIdentity());
	});
}

function bindWebsockets(server) {
	var sockets = io.listen(server, {
		log : false
	});
	var socket = sockets.of('/client');

	var deviceManager = require('./modules/zwave/devicemanager').deviceManager;
	deviceManager.on('device_added', function(device) {
		socket.emit('device_added', device);
	});

	deviceManager.on('device_removed', function(device) {
		socket.emit('device_removed', device);
	});
}

function bindWriter() {
	handler.on('state_changed', function(message) {
		writer.write(message, function(err, status) {
			logger.debug('wrote to file: ' + JSON.stringify(message));
		});
	});
}

function scheduleSynchronizer() {
	scheduler.schedule('00 00 03 * * 1-7', function() {
		synchronizer.synchronize(function(err) {
			if (err) {
				logger.info('synchronization unsuccesfull:' + JSON.stringify(err));
			} else {
				logger.info('synchronization succesfull');
			}
		});
	}, function(err) {
		if (err) {
			logger.error('failed to schedule synchronizer');
		}
	});
}

if ('development' == app.get('env')) {
	app.use(express.logger('dev'));
	app.use(express.errorHandler());
}

// Config
app.registerPartial('header', '/views/header.hbs');
app.set('port', process.env.PORT || 8080);
app.set('views', __dirname + '/views');
app.set('view engine', 'hbs');
app.use(express.favicon());
app.use(express.bodyParser());
app.use(express.methodOverride());
app.use(app.router);
app.use(express.static(path.join(__dirname, 'public')));
app.start();