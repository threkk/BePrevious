var async = require('async');

var logger = require('../logging').getLogger('client');
var client = require('./client');
var writer = require('../writer').writer;

module.exports.updateBinaryCommand = function(deviceManager, callback) {
	async.forEach(deviceManager.devices, updateBinary, function(err) {
		callback(err);
	});
}

function updateBinary(device, callback) {
	var nodeid = device.data.id;

	var parser;
	var command;
	if (device.data.type == 'switch') {
		parser = parseSwitch;
		command = 'devices[' + nodeid + '].instances[0].commandClasses[0x25]';
	} else if (device.data.type == 'sensor') {
		parser = parseSensor;
		command = 'devices[' + nodeid + '].instances[0].commandClasses[0x30]';
	} else {
		return callback();
	}

	client.runCommand(command, function handleResponse(err, json) {
		if (err) {
			return callback(err);
		}

		if (!json) {
			// device has no binary information
			return callback();
		}

		var stateUpdate = parser(json);
		device.updateState(stateUpdate.state,
				function(err, dirty, currentState) {
					if (err) {
						logger.error("failed to update device binary state: "
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
							logger.debug('wrote to file: '
									+ JSON.stringify(message));
						});
					}

					callback();
				});
	});
}

function parseSwitch(update) {
	return {
		timestamp : update.data.level.updateTime,
		state : {
			value : (update.data.level.value == 255) ? 1 : 0
		}
	}
}

function parseSensor(update) {
	return {
		timestamp : update.data.level.updateTime
		state : {
			value:update.data.level.value ? 1 : 0
		}
	};
}