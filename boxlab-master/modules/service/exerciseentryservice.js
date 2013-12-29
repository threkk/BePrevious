var logger = require('../logging').getLogger('service');
var ExerciseEntry = require('../schemas').ExerciseEntry;

function handleError(message, err, data) {
	logger.error('%s with data: %s', message, JSON.stringify(data));
	logger.error('Error: %s', JSON.stringify(err));
}

function Service() {

}

Service.prototype = {
	saveEntry : function(data, callback) {
		ExerciseEntry.saveUpdate(data, function(err) {
			if (err) {
				handleError('Failed to save device', err, data);
			}
			callback(err);
		});
	},

	getEntries : function(identification, query, callback) {
		var statement = ExerciseEntry.find({
			identification : identification
		});

		if (query.from) {
			statement = query.where('created').gte(query.from);
		}

		if (query.to) {
			statement = query.where('created').lte(query.to);
		}

		statement.exec(callback);

	},

	deleteEntry : function(id, callback) {
		ExerciseEntry.remove({
			_id : id
		}, function(err) {
			if (err) {
				handleError('Failed to delete device', err, data);
			}
			callback(err);
		});
	}
};

module.exports = new Service();