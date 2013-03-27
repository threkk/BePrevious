var application_root = __dirname;
var express = require("express");
var path = require("path");
var async = require('async');
var fs = require('fs');
var app = express();
log4js = require('log4js');

var client = require('./client');
var io = require('./io');
var logger = log4js.getLogger("app");

fs.mkdir('logs', 0777, function(err) {
	if (!err) {
		logger.debug("created log directory");
	} else {
		if (err && err.code!=='EEXIST') {
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

var http = require('http');
var server = http.createServer(app);

// Config
app.configure(function () {
	app.engine('hulk', require('hulk-hogan').__express);
	
    app.set('views', __dirname + '/views');
    app.set('view engine', 'hulk');
	
    app.use(express.bodyParser());
    app.use(express.methodOverride());    
    app.use(app.router);
    app.use(express.static(path.join(application_root, "public")));
    app.use(express.errorHandler({
        dumpExceptions: true,
        showStack: true
    }));
});



var root_routes = {
	get: function(req,res) {
	    res.render('demo.hulk',{naam: req.query.naam});
	}
}

client.on('update', function(message) {
	logger.debug(JSON.stringify(message));
});

// map all api routes
app.map(root_routes, '/');

// Launch server
server.listen(8080);
logger.info("Application started");