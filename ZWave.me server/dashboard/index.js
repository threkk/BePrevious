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

exports.routes = {
	get: getHome,
	'devices' : {
		get: getDevices
	}
}