var userService = require('../modules/service/userservice');
var patientService = require('../modules/service/patientservice');

function postCredentials(req, res) {
	var username = req.body.username;
	var password = req.body.password;

	userService.getUser(username, password, function(err, user) {
		if (err) {
			return res.send(500, {
				error : err
			});
		}

		if (user) {
			// authorized
			res.send(200, {
				message : 'Succesfully authenticated ' + user.name
			});
		} else {
			// unauthorized
			res.send(401, {
				message : 'Invalid username/password combination'
			});
		}
	});
}

function postPatient(req, res) {
	patientService.savePatient(req.body, function(err) {
		if (err) {
			res.send(500, err);
		} else {
			res.end();
		}
	});
}

function getPatients(req, res) {
	patientService.getPatients(function(err, patients) {
		if (err) {
			res.send(500, err);
		} else {
			res.send(patients);
		}
	});
}

module.exports.routes = {
	'/authenticate' : {
		post : postCredentials
	},
	'/patients' : {
		post : postPatient,
		get : getPatients
	}
};