var client = require('../client');

function getHome(req,res) {
	res.render('home.hbs', {
		controllerData: client.getControllerData()
	});
}

function getDevices(req,res) {
	res.render('devices.hbs', {
		devices: client.devices
	});
}

exports.routes = {
	get: getHome,
	'devices' : {
		get: getDevices
	}
}