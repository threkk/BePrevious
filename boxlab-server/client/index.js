var EventEmitter = require('events').EventEmitter;
var emitter = new EventEmitter();
var configuration = require('../configuration.js');
var restify = require('restify');
var logger = log4js.getLogger("client");

var DeviceManager = require('./devicemanager.js').DeviceManager;
var CommandManager = require('./commandmanager.js').CommandManager;

var apiDataPath = '/ZWaveAPI/Data/';
var apiCommandPath = '/ZWaveAPI/Run/';

function Client() {
    this.restClient = restify.createJsonClient({
        url: configuration.host,
        version: '*'
    });

    this.deviceManager = new DeviceManager(this);
    this.commandManager = new CommandManager(this);
    
    this.deviceManager.update();
}

Client.prototype = {
    runCommand: function (command, callback) {
        this.restClient.get(apiCommandPath + command, function (err, req, res, json) {
         	logger.debug('command executed: ' + command);
            callback && callback(err, json);
        });
    },

    getApiData: function (timestamp, callback) {
        this.restClient.get(apiDataPath + timestamp, function (err, req, res, json) {
            callback(err, json);
        });
    }
};

Client.prototype.__proto__ = EventEmitter.prototype;

var client = new Client();

function startInclude(req, res) {
	var duration = getDuration(req);
	client.deviceManager.startInclusionMode(duration, function(err) {
		handleResponse(err,res);
	});
}

function stopInclude(req, res) {
	client.deviceManager.stopInclusionMode(function(err) {
		handleResponse(err,res);
	});
}

function startExclude(req, res) {
	var duration = getDuration(req);
	client.deviceManager.startExclusionMode(duration, function(err) {
		handleResponse(err,res);
	});
}

function stopExclude(req, res) {
	client.deviceManager.stopExclusionMode(function(err) {
		handleResponse(err,res);
	});
}

function getController(req, res) {
	res.jsonp(client.deviceManager.controller);
}

function getDevices(req, res) {
	res.jsonp(client.deviceManager.devices);
}

function getDuration(req) {
	var duration = (req.query.duration || req.body.duration);
	var min = configuration.minDuration;
	var max = configuration.maxDuration;
	
	return Math.max(min, Math.min(max, (parseInt(duration) || min)))
}

function handleResponse(err, res) {
	if (err) {
		throw new Error();
	} else {
		res.jsonp({message: 'succes'});
	}
}


var routes = {
	'controller' : {
		get: getController
	}, 
	'devices': {
		get: getDevices
	},
    'devices/inclusion/': {
        'start': {
            get: startInclude,
            post: startInclude
        },
        'stop': {
            get: stopInclude,
            post: stopInclude
        }
    },
    'devices/exclusion/': {
        'start': {
            get: startExclude,
            post: startExclude
        },
        'stop': {
            get: stopExclude,
            post: stopExclude
        }
    }
};

module.exports = {
    client: client,
    routes: routes
};