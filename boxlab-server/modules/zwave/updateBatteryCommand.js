var async = require('async');
var logger = require('../logging').getLogger('client');
var client = require('./client').client;

module.exports.updateBatteryCommand = function (deviceManager, callback) {
	if (require('./devicemanager').deviceManager==deviceManager) {
		console.log('device manager');
	} else if (require('../../app')==deviceManager) {
		console.log('app!');
	}
    async.forEach(deviceManager.devices, updateBattery, function (err) {
        callback(err);
    });
}

function updateBattery(device, callback) {
	if (!device.data.hasBattery) {
		//dont need to update the battery stats if the device has no battery
		return callback();
	}
	
    var nodeid = device.data.id;
    var command = 'devices[' + nodeid + '].instances[0].commandClasses[128]';

    client.runCommand(command, function handleResponse(err, json) {
        if (err) {
            return callback(err);
        }

        if (!json) {
            //device has no battery information
            return callback();
        }

        device.updateState(parseResult(json), callback);
    });
}

function parseResult(json) {
    var result = {};
    if (json.name == 'Battery') {
    	if (json.data && json.data.last) {
    		result.batteryLevel = json.data.last.value;
    	}
    }
    return result;
}