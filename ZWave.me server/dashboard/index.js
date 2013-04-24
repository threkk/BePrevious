var client = require('../client').client;

function getHome(req,res) {
	res.render('home.hbs', {
		controllerData: client.deviceManager.controller
	});
}

function getDevices(req,res) {
	var deviceData = [];
	
	for(var key in client.deviceManager.devices) {
		var device = client.deviceManager.devices[key];
		deviceData.push(device.data);
	}
	
	res.render('devices.hbs', {
		devices: deviceData
	});
}

function editDevice(req, res) {
	var nodeId = req.params.id;
	var device = client.deviceManager.getDevice(nodeId);
	
	if (!device) {
		throw new Error('device not found');
	} else {
		res.render('editDevice.hbs', {
		device: device
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