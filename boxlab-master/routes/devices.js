var async = require('async');
var moment = require('moment');

var logger = require('../modules/logging').getLogger('api');
var deviceService = require('../modules/service/deviceservice');

function parseDate(input) {
	var intValue = parseInt(input, 10);
	if (intValue) {
		input = intValue;
	}

	var parsedDate = moment(input);
	if (parsedDate.isValid()) {
		return parsedDate;
	} else {
		return null;
	}
}

function getDevices(req, res) {
	var identifier = req.params.identifier;
	var nodeId = req.query.nodeid;

	function handleReponse(err, results) {
		if (err) {
			return res.send(500, err);
		}
		res.send(results);
	}

	if (nodeId) {
		deviceService.getDevice(identifier, nodeId, handleResponse);
	} else {
		deviceService.getDevices(identifier, handleReponse);
	}
}

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
	var state = req.body.state;

	var stateData = {
		identification : req.params.identification,
		nodeId : req.body.nodeId,
		timestamp : req.body.timestamp,
		kWh : state.kWh,
		W : state.W,
		temperature : state.temperature,
		luminescence : state.luminescence,
		value : state.value
	};

	deviceService.saveState(stateData, function(err) {
		if (err) {
			res.send(500, err);
		} else {
			res.end();
		}
	});
}

function getDeviceState(req, res) {
	var query = {
		from : (parseDate(req.query.from) || moment().startOf('month'))
				.valueOf(),
		to : (parseDate(req.query.to) || moment().endOf('month')).valueOf()
	};

	deviceService.get(req.params.identification, query, function handleResult(
			err, results) {
		res.send(404, results);
	});
}

module.exports.routes = {
	'/state' : {
		post : postDeviceState,
		get : getDeviceState
	},
	post : postDevices,
	get : getDevices
};