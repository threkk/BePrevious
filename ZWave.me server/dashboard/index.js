var client = require('../client').client;

function getHome(req,res) {
	res.render('home.hbs', {
		controllerData: client.deviceManager.controller
	});
}

function getDevices(req,res) {
	res.render('devices.hbs', {
		devices: client.deviceManager.devices
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