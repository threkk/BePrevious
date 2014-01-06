var logger = require('../logging').getLogger('service');

function Service() {

}

Service.prototype = {
	getUser : function(username, password, callback) {
		callback(null, {});
	}
};

module.exports = new Service();