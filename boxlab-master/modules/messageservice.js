var logger = require('./logging').getLogger('service');

var Message = require('./models').Message;

function Service() {

}

Service.prototype = {
	saveMessage : function(data, callback) {
		Message.saveUpdate(data, function(err) {
			if (err) {
				logger.error('Failed to save message with data: ' + JSON.stringify(data));
				logger.error('Error: ' + JSON.stringify(err));
			}
			callback(err);
		});
	},

	getMessages : function(identifier, query, callback) {
		Message.find({
			identifier : identifier
		}).where('timestamp').gte(query.from).lte(query.to).exec(callback);
	}
};

module.exports.service = new Service();