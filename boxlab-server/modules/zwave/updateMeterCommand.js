var async = require('async');
var logger = require('../logging').getLogger('client');
var client = require('./client').client;

module.exports.updateMeterCommand = function (deviceManager, callback) {
    async.forEach(deviceManager.devices, updateMeter, function (err) {
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
            //device has no meter information
            return callback();
        }

        device.updateState(parseResult(json), callback);
    });
}

function parseResult(json) {
    var result = {};
    for (var key in json.data) {
        var instance = json.data[key];
        if (instance && instance.sensorTypeString) {
            var sensorType = instance.sensorTypeString.value;
			var scale = instance.scaleString && instance.scaleString.value;
            var value = instance.val.value;

			if (!scale) {
				continue;
			}
			
			if (scale=="W") {
				result.power = value;
			} else if (scale=="kWh") {
				result.usage = value;
			}
        }
    }
    return result;
}