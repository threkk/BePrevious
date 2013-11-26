var logger = require('./logging').getLogger('service');

var Patient = require('./models').Patient;

function Service() {

}

Service.prototype = {
	savePatient : function(patientData, callback) {
		Patient.saveUpdate(patientData, function(err) {
			if (err) {
				logger.error('Failed to save patient with data: ' + JSON.stringify(patientData));
				logger.error('Error: ' + JSON.stringify(err));
			}
			callback(err);
		});
	},

	getPatients : function(callback) {
		Patient.find({}).exec(callback);
	}

};

function handleError(message, err) {
	logger.error(message);
	logger.error('Error: ' + JSON.stringify(err));
}

module.exports.service = new Service();