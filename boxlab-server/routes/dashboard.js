var async = require('async');
var _ = require('lodash');

var logger = require('../modules/logging').getLogger();
var localDB = require('../modules/localdatabase').localDB;

var client = require('../modules/zwave/client');
var deviceManager = require('../modules/zwave/devicemanager').deviceManager;

function getNormalizedDevice(device) {
	var deviceData = _.merge(device.data, device.state);

	// determine the status of the device
	deviceData.status = 'Ok';
	if (deviceData.batteryLevel < 25) {
		deviceData.status = 'Low battery';
	} else if (deviceData.isFailed) {
		deviceData.status = 'Failed';
	}

	// add an image for the battery level
	if (deviceData.batteryLevel) {
		var prefix = '/images/battery/battery-';
		var fraction = Math.round(deviceData.batteryLevel / 25) * 25;
		deviceData.batteryImage = prefix + fraction + '.png';
	}

	return deviceData;
}

function getNormalizedDevices() {
	var devices = [];

	for ( var key in deviceManager.devices) {
		var device = deviceManager.devices[key];
		var normalized = getNormalizedDevice(device);
		devices.push(normalized);
	}

	return devices;
}

/**
 * optional callback callback(err)
 */
function setCalibratedTemp(nodeid, calibratedTemp, callback) {
	var device = deviceManager.getDevice(nodeid);
	if (!device) {
		return callback('no device with id ' + nodeid + ' was found');
	}

	var temp = device.state.temperature;
	var offset = calibratedTemp - temp;

	localDB.setTempOffset(nodeid, offset);
	logger.debug('setting temp offset to (' + offset + 'c) for ' + nodeid);
	
	callback();
}

/**
 * optional callback callback(err)
 */
function setSleepTime(nodeid, sleeptime, callback) {
	var instance = 'devices[' + nodeid + '].instances[0]';
	var command = instance + '.commandClasses[0x84].Set(' + sleeptime + ',1)'
	client.runCommand(command, function(err, json) {
		if (!err) {
			logger.debug('setting sleeptime(' + sleeptime + 's) for ' + nodeid);
		}
		callback(err, json);
	});
}

function setName(nodeid, name, callback) {
	localDB.setName(nodeid, name);
	callback();
}

function updateDevice(nodeid, options, callback) {
	var funcs = [];
	if (options.calibratedTemp) {
		funcs.push(function(callback) {
			setCalibratedTemp(nodeid, options.calibratedTemp, callback);
		});
	}
	if (options.sleeptime) {
		funcs.push(function(callback) {
			setSleepTime(nodeid, options.sleeptime, callback)
		});
	}

	if (options.name) {
		funcs.push(function(callback) {
			setName(nodeid, options.name, callback);
		});
	}

	async.series(funcs, callback);
}

function updateDevicesByType(nodeid, options, callback) {
	var targetDevice = deviceManager.getDevice(nodeid);

	function iterator(device, callback) {
		if (device.data.productType != targetDevice.data.productType) {
			// skip devices that are not of the same product type
			return callback();
		}

		if (device.data.manufacturerId != targetDevice.data.manufacturerId) {
			// skip devices that are not of the same manufacturer
			return callback();
		}

		updateDevice(device.data.id, options, callback);
	}

	async.each(deviceManager.devices, iterator, callback);
}

/**
 * renders the home page
 */
function getHome(req, res) {
	res.render('home.hbs', {
		controllerData : deviceManager.controller
	});
}

/**
 * renders the devices page
 */
function getDevices(req, res) {
	res.render('devices.hbs', {
		devices : getNormalizedDevices()
	});
}

/**
 * renders the edit device page
 */
function getEditDevice(req, res) {
	var device = deviceManager.getDevice(req.params.id);
	var normalized = getNormalizedDevice(device);
	if (!device) {
		console.log();
		throw new Error('device with node id #' + req.params.id + ' not found');
	} else {
		res.render('editDevice.hbs', {
			device : normalized
		});
	}
}

/**
 * receives the post from the edit device page
 */
function postUpdateDevice(req, res) {
	var nodeid = parseInt(req.params.id, 10) || -1;
	if (nodeid > 0) {

		function doDeviceManagerUpdate(callback) {
			deviceManager.update(callback);
		}

		function doDeviceUpdate(callback) {
			updateDevice(nodeid, req.body, callback);
		}

		async.series([ doDeviceManagerUpdate, doDeviceUpdate, doDeviceManagerUpdate ], function(
				err, results) {
			if (err) {
				res.send(500, {
					error : err
				});
			} else {
				res.end();
			}
		});
	}
}

/**
 * receives the post from the edit device page if it should apply to devices of
 * the same type
 */
function postUpdateDevicesByType(req, res) {
	var nodeid = parseInt(req.params.id, 10) || -1;
	if (nodeid > 0) {
		async.series([ deviceManager.update, function(callback) {
			updateDevicesByType(nodeid, req.body, callback);
		}, deviceManager.update ], function(err, results) {
			res.end();
		});
	}
}

exports.routes = {
	get : getHome,
	'devices' : {
		get : getDevices,
		'/edit/:id' : {
			get : getEditDevice,
			post : postUpdateDevice
		},
		'/edit/all/:id' : {
			post : postUpdateDevicesByType
		}
	}
}