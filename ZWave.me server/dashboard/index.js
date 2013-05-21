var client = require('../client').client;
var logger = log4js.getLogger("client");

function getHome(req,res) {
	res.render('home.hbs', {
		controllerData: client.deviceManager.controller
	});
}

function getDevices(req,res) {
	var devices = [];
	
	for(var key in client.deviceManager.devices) {
		var device = client.deviceManager.devices[key];
		var deviceData = JSON.parse(JSON.stringify(device.data));
		
		var status = 'Ok';
		if (deviceData.batteryLevel<25) {
			status = 'Low battery';
		} else if (deviceData.isFailed) {
			status = 'Failed';
		}
		deviceData.status = status;
		devices.push(deviceData);
	}
	
	res.render('devices.hbs', {
		devices: devices
	});
}

function editDevice(req, res) {
	var id = req.params.id;
	var device = client.deviceManager.getDevice(id);
	
	if (!device) {
		throw new Error('device '+id+' not found');
	} else {
		res.render('editDevice.hbs', {
			device: device.data
		});
	}
}

function updateDevice(req,res) {
	var deviceId = req.params.id;
	var device = client.deviceManager.getDevice(deviceId);
	var data = req.body;
	var timeout = data.sleeptime;
	var command = 'devices[' + deviceId + '].instances[0].commandClasses[0x84].Set(' + timeout + ',1)'
	device.manager.client.runCommand(command, function (err, json) {
	 if(err){
	 logger.error(err);
	 return;
	 }
	 logger.debug(JSON.stringify(json));
	});
}

function updateAllDevices(req,res) {
logger.debug('update all called');
	
}

exports.routes = {
	get: getHome,
	'devices' : {
		get: getDevices,
		'/edit/:id': {
			get: editDevice,
			post: updateDevice
		},
		'/edit/all/:id': {
			post: updateAllDevices
		}
	}
}