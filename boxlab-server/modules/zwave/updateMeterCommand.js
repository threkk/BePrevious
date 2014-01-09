var async = require('async');
var logger = require('../logging').getLogger('client');
var writer = require('../writer').writer;
var client = require('./client');

module.exports.updateMeterCommand = function(deviceManager, callback) {
	async.forEach(deviceManager.devices, updateMeter, function(err) {
		callback(err);
	});
}

function updateMeter(device, callback) {
	var nodeid = device.data.id;
	var command = 'devices[' + nodeid + '].instances[0].commandClasses[50]';

	client.runCommand(command, function handleResponse(err, json) {
		if (err) {
			return callback(err);
		}

		if (!json) {
			// device has no meter information
			return callback();
		}

		updateDevice(device, parseResult(json), callback);
	});
}

function updateDevice(device, stateUpdate, callback) {
	device.updateState(stateUpdate.state, function(err, dirty, currentState) {
		if (err) {
			logger.error("failed to update device meter state: "
					+ JSON.stringify(err));
			return callback(err);
		}

		if (dirty) {
			var message = {
				nodeId : device.data.id,
				timestamp : stateUpdate.timestamp,
				state : currentState
			};
			writer.write(message, function(err, status) {
				logger.debug('wrote to file: ' + JSON.stringify(message));
			});
		}

		callback();
	});
}

function parseResult(json) {
	var result = {
		state : {}
	};

	for ( var key in json.data) {
		var instance = json.data[key];
		if (instance && instance.sensorTypeString) {
			var sensorType = instance.sensorTypeString.value;
			var scale = instance.scaleString && instance.scaleString.value;
			var updateTime = instance.val.updateTime;
			var value = instance.val.value;

			if (!scale) {
				continue;
			}

			result.timestamp = updateTime;

			if (scale == "W") {
				result.state.W = value;
			} else if (scale == "kWh") {
				result.state.kWh = value;
			}
		}
	}
	return result;
}