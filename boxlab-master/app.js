/**
 * Module dependencies.
 */

var express = require('express');
var http = require('http');
var path = require('path');

var logger = require('./modules/logging').getLogger();
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

app.start = function(port) {
	http.createServer(app).listen(port, function() {
		logger.debug('Boxlab master server running on port ' + port);
	});

	var apiRoutes = require('./routes/api').routes;
	var deviceRoutes = require('./routes/devices').routes;
	var messageRoutes = require('./routes/messages').routes;
	var routes = {
		'/api' : {
			'' : apiRoutes,
			'/:identification' : {
				'/devices' : deviceRoutes, 
				'/messages': messageRoutes
			}
		}
	};
	app.map(routes, '/boxlab');

	require('mongoose').connect('mongodb://localhost/boxlab');
};

// development only
if ('development' == app.get('env')) {
	app.use(express.errorHandler());
}

app.use(express.favicon());
app.use(express.logger('dev'));
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

app.start(process.env.PORT || 8083);