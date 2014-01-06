var logger = require('../logging').getLogger('service');
var Message = require('../schemas').Message;

function handleError(message, err, data) {
	logger.error('%s with data: %s', message, JSON.stringify(data));
	logger.error('Error: %s', JSON.stringify(err));
}

function Service() {

}

Service.prototype = {
	saveMessage : function(data, callback) {
		Message.saveUpdate(data, function(err) {
			if (err) {
				handleError('Failed to save message', err, data);
			}
			callback(err);
		});
	},

	getMessages : function(identification, query, callback) {
		var statement = Message.find({
			identification : identification
		});

		if (query.from) {
			statement = statement.where('created').gte(query.from);
		}

		if (query.to) {
			statement = statement.where('created').lte(query.to);
		}

		statement.exec(callback);
	}
};

module.exports = new Service();