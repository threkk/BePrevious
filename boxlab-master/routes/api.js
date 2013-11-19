var userService = require('../modules/userservice').service;

module.exports.routes = {
	'/authenticate' : {
		post : function(req, res) {
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
	}
};