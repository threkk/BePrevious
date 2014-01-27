var async = require('async');
var _ = require('lodash');

var localDB = require('../localdatabase').localDB;
var client = require('./client');
var Device = require('./device').Device;

module.exports.updateDevicesCommand = function(deviceManager, callback) {
	client.getApiData(0, function(err, data) {
		if (err) {
			return callback(err);
		}

		if (!data || !data.devices) {
			return callback('no information received from zwave client');
		}

		// update the device managers controller data
		deviceManager.controller = data.controller.data;

		// update the information the controller has on connected devices
		updateDevices(deviceManager, parseNewDevices(data), callback);
	});
}

function parseNewDevices(data) {
	var newDevices = [];
	for ( var nodeId in data.devices) {
		if (nodeId == 1 || nodeId == 255) {
			// We skip the node ids of the zwave controller, and the broadcast
			continue;
		}
		var deviceName = localDB.getName(nodeId);
		var deviceData = data.devices[nodeId].data;
		var commandClasses = data.devices[nodeId].instances[0].commandClasses;
		var newDevice = {
			id : nodeId,
			name : deviceName,
			basicType : deviceData.basicType.value,
			genericType : deviceData.genericType.value,
			specificType : deviceData.specificType.value,
			manufacturerId : deviceData.manufacturerId.value,
			productId : deviceData.manufacturerProductId.value,
			productType : deviceData.manufacturerProductType.value,
			isListening : deviceData.isListening.value,
			isFLiRS : !deviceData.isListening.value
					&& (deviceData.sensor250.value || deviceData.sensor1000.value),
			isFailed : deviceData.isFailed.value,
			hasWakeup : 0x84 in commandClasses,
			hasBattery : 0x80 in commandClasses
		};

		if (newDevice.hasWakeup) {
			newDevice.wakeupInterval = commandClasses[0x84].data.interval.value
		}
		
		if (0x25 in commandClasses) {
			// COMMAND_CLASS_SWITCH_BINARY
			newDevice.type = 'switch';
		} else if (0x30 in commandClasses) {
			// COMMAND_CLASS_SENSOR_BINARY
			newDevice.type = 'sensor';
		}

		newDevices.push(newDevice);
	}
	return newDevices;
}

function updateDevices(deviceManager, newDevices, callback) {
	try {
		for ( var index in newDevices) {
			var newDevice = newDevices[index];
			if (_.findIndex(deviceManager.devices, function(device) {
				return device.data.id == newDevice.id;
			}) < 0) {
				deviceManager.insertDevice(newDevice);
			}
		}

		for ( var index in deviceManager.devices) {
			var oldDevice = deviceManager.devices[index];
			if (_.findIndex(newDevices, function(newDevice) {
				return oldDevice.data.id == newDevice.id;
			}) < 0) {
				deviceManager.removeDevice(index);
			}
		}
	} catch (err) {
		return callback('Failed to update device list ' + JSON.stringify(err));
	}

	var updateFuncs = [];
	deviceManager.devices.forEach(function(device) {
		newDevices.forEach(function(newDevice) {
			if (device.data.id == newDevice.id) {
				updateFuncs.push(function(callback) {
					device.updateData(newDevice, function(err) {
						callback(err);
					});
				});
				return;
			}
		});
	});

	async.series(updateFuncs, callback);
}