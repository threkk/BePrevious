var configuration = require('../configuration');
var deviceManager = require('../modules/zwave/devicemanager').deviceManager;

function getController(req, res) {
	res.jsonp(deviceManager.controller);
}

function getDevices(req, res) {
	res.jsonp(deviceManager.devices);
}

function startInclude(req, res) {
	deviceManager.changeMode('include', getDuration(req), function(err){
		handleResponse(err, res);
	});
}

function stopInclude(req, res) {
	deviceManager.changeMode('include', 0, function(err){
		handleResponse(err, res);
	});
}

function startExclude(req, res) {
	deviceManager.changeMode('exclude', getDuration(req), function(err){
		handleResponse(err, res);
	});
}

function stopExclude(req, res) {
	deviceManager.changeMode('exclude', 0, function(err){
		handleResponse(err, res);
	});
}

function getDuration(req) {
	var duration = (req.query.duration || req.body.duration);
	var min = configuration.minDuration;
	var max = configuration.maxDuration;
	
	return Math.max(min, Math.min(max, (parseInt(duration) || min)))
}

function handleResponse(err, res) {
	if (err) {
		throw new Error(err);
	} else {
		res.jsonp({message: 'succes'});
	}
}

module.exports.routes = {
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