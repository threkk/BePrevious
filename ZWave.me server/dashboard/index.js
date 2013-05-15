var client = require('../client').client;

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

exports.routes = {
	get: getHome,
	'devices' : {
		get: getDevices,
		'/edit/:id': {
			get: editDevice
		}
	}
}