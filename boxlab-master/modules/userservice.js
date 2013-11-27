var logger = require('./logging').getLogger('service');

function Service() {

}

Service.prototype = {
	getUser : function(username, password, callback) {
		logger.debug('retrieving user information for ' + username);
		callback(null, {});
	}
};

module.exports.service = new Service();