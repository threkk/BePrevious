var express = require('express');
var http = require('http');
var path = require('path');

var logger = require('./modules/logging').getLogger();
var DBLogger = require('./modules/logging').getLogger('database');
var serverLogger = require('./modules/logging').getLogger('server');
var app = express();

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
			logger.debug('%s %s', key, route);
			app[key](route, a[key]);
			break;
		}
	}
};

app.initDB = function() {
	var mongoose = require('mongoose');
	mongoose.set('debug', function(collectionName, method, query, doc) {
		DBLogger.debug("query: db.%s.%s(%s)", collectionName, method, JSON.stringify(query));
	});
	mongoose.connection.on('error', function(err) {
		DBLogger.error('Failed to connect to mongoDB');
	});
	mongoose.connect('mongodb://localhost/boxlab');
};

app.start = function(port) {
	http.createServer(app).listen(port, function() {
		logger.debug('Boxlab master server running on port ' + port);
	});

	var apiRoutes = require('./routes/api').routes;
	var deviceRoutes = require('./routes/devices').routes;
	var entryRoutes = require('./routes/exerciseentries').routes;
	var messageRoutes = require('./routes/messages').routes;
	var routes = {
		'/api' : {
			'' : apiRoutes,
			'/:identification' : {
				'/devices' : deviceRoutes,
				'/entries' : entryRoutes,
				'/messages' : messageRoutes
			}
		}
	};
	app.map(routes, '/boxlab');
};

// development only
if ('development' == app.get('env')) {
	app.use(express.errorHandler());
	app.use(function(req, res, next) {
		serverLogger.trace('%s %s', req.method, req.url);
		next();
	});
}

app.use(express.favicon());
app.use(express.bodyParser());
app.use(express.methodOverride());
app.use(app.router);
app.use(express.static(path.join(__dirname, 'public')));
app.use(express.cookieParser());
app.use(express.cookieSession({
	secret : 'boxlab-best-lab',
	cookie : {
		maxAge : 60 * 60 * 1000
	}
}));

app.initDB();
app.start(process.env.PORT || 8083);