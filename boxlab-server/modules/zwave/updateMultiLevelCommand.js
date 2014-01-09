var async = require('async');
var logger = require('../logging').getLogger('client');
var localDB = require('../localdatabase').localDB;
var writer = require('../writer').writer;
var client = require('./client');

module.exports.updateMultiLevelCommand = function(deviceManager, callback) {
	async.forEach(deviceManager.devices, updateMultiLevel, function(err) {
		callback(err);
	});
}

function updateMultiLevel(device, callback) {
	var nodeId = device.data.id;
	var command = 'devices[' + nodeId + '].instances[0].commandClasses[49]';

	client.runCommand(command, function handleResponse(err, json) {
		if (err) {
			return callback(err);
		}

		if (!json) {
			// device has no multilevel information
			return callback();
		}

		updateDevice(device, parseResult(nodeId, json), callback);
	});
}

function updateDevice(device, stateUpdate, callback) {
	device.updateState(stateUpdate.state, function(err, dirty, currentState) {
		if (err) {
			logger.error("failed to update multilevel state: " + JSON.stringify(err));
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

function parseResult(nodeId, json) {
	var result = {
		state : {}
	};

	for ( var key in json.data) {
		var instance = json.data[key];
		if (instance && instance.sensorTypeString) {
			var sensorType = instance.sensorTypeString.value;
			var updateTime = instance.val.updateTime;
			var value = instance.val.value;

			var name = normalizeSensorName(sensorType);

			// adjust the read value with the stored temperature offset
			if (name == 'temperature') {
				value = Math.round(value);
				value = value + localDB.getTempOffset(nodeId);
			}

			result.timestamp = updateTime;
			result.state[name] = value;
		}
	}
	return result;
}

function normalizeSensorName(name) {
	// decapitalize the first character
	var result = name.charAt(0).toLowerCase() + name.slice(1);

	return result;
}