var async = require('async');

var deviceService = require('../modules/deviceservice').service;
var logger = require('../modules/logging').getLogger('api');

function postDevices(req, res) {
	var identifier = req.params.identifier;
	var devices = req.body.devices;

	if (!devices || !devices.length) {
		// malformed request
		return res.send(400, {
			error : 'missing devices array in request'
		});
	}

	async.each(devices, function(device, callback) {
		device.identifier = identifier;
		deviceService.save(device, callback);
	}, function(err) {
		if (err) {
			return res.send(500, err);
		}
		return res.end();
	});
}

function postDeviceState(req, res) {
	var identifier = req.params.identifier;
	var 
}

module.exports.routes = {
	'/state' : {
		post : postDeviceState
	},
	post : postDevices
};