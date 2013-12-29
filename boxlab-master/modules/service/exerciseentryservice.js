var logger = require('../logging').getLogger('service');
var ExerciseEntry = require('../schemas').ExerciseEntry;

function Service() {

}

Service.prototype = {
	saveEntry : function(data, callback) {
		ExerciseEntry.saveUpdate(data, function(err) {
			if (err) {
				logger.error('Failed to save device with data: ' + JSON.stringify(data));
				logger.error('Error: '+JSON.stringify(err));
			}
			callback(err);
		});
	},

	getEntries : function(identification, query, callback) {
		ExerciseEntry.find({
			identification : identification
		}).where('date').gte(query.from).lte(query.to).exec(callback);
	},

	deleteEntry : function(id, callback) {
		ExerciseEntry.remove({
			_id : id
		}, function(err) {
			if (err) {
				logger.error('Failed to delete device with data: ' + JSON.stringify(data));
				logger.error('Error: '+JSON.stringify(err));
			}
			callback(err);
		});
	}
};

module.exports = new Service();