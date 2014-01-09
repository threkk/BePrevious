var async = require('async');

var logger = require('../logging').getLogger('client');
var client = require('./client');
var writer = require('../writer').writer;

module.exports.updateBatteryCommand = function(deviceManager, callback) {
	async.forEach(deviceManager.devices, updateBattery, function(err) {
		callback(err);
	});
}

function updateBattery(device, callback) {
	if (!device.data.hasBattery) {
		// dont need to update the battery stats if the device has no battery
		return callback();
	}

	var nodeid = device.data.id;
	var command = 'devices[' + nodeid + '].instances[0].commandClasses[128]';

	client.runCommand(command, function handleResponse(err, json) {
		if (err) {
			return callback(err);
		}

		if (!json) {
			// device has no battery information
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

	if (json.name == 'Battery') {
		if (json.data && json.data.last) {
			result.timestamp = json.data.last.updateTime;
			result.state.batteryLevel = json.data.last.value;
		}
	}

	return result;
}