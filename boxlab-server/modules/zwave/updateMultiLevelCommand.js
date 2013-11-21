var async = require('async');
var logger = require('../logging').getLogger('client');
var localDB = require('../localdatabase').localDB;
var client = require('./client').client;


module.exports.updateMultiLevelCommand = function (deviceManager, callback) {
    async.forEach(deviceManager.devices, updateMultiLevel, function (err) {
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
            //device has no multilevel information
            return callback();
        }

        device.updateState(parseResult(nodeId, json), callback);
    });
}

function parseResult(nodeId, json) {
    var result = {};
    for (var key in json.data) {
        var instance = json.data[key];
        if (instance && instance.sensorTypeString) {
            var sensorType = instance.sensorTypeString.value;
            var value = instance.val.value;

            // adjust the read value with the stored temperature offset
            if (sensorType == 'Temperature') {
                value = Math.round(value);
                value = value + localDB.getTempOffset(nodeId);
            }

			//normalize the name
			var name = sensorType.charAt(0).toLowerCase() + sensorType.slice(1);

            result[name] = value;
        }
    }
    return result;
}