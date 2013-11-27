var logger = require('./logging').getLogger('service');
var Device = require('./models').Device;
var DeviceState = require('./models').DeviceState;

function Service() {

}

Service.prototype = {
	save : function(deviceData, callback) {
		var device = new Device(deviceData);
		device.save(function(err) {
			if (err) {
				logger.error('Failed to save device with data: ' + JSON.stringify(deviceData));
			}
			callback(err);
		});
	},

	getDevice : function(identifier, nodeId, callback) {
		Device.find({
			identifier : identifier,
			nodeId : nodeId
		}).exec(callback);
	},

	getDevices : function(identifier, callback) {
		Device.find({
			identifier : identifier
		}).exec(callback);
	},

	saveState : function(deviceStateData, callback) {
		var deviceState = new DeviceState(deviceStateData);
		deviceState.save(function(err) {
			if (err) {
				logger.error('Failed to save device state with data: '
						+ JSON.stringify(deviceStateData));
			}
			callback(err);
		});
	},

	getState : function(identifier, query, callback) {
		DeviceState.find({
			identifier : identifier
		}).where('timestamp').gte(query.from).lte(query.to).exec(callback);
	}
};

module.exports.service = new Service();