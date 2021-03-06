var logger = require('../logging').getLogger('service');
var Device = require('../schemas').Device;
var DeviceState = require('../schemas').DeviceState;

function Service() {

}

Service.prototype = {
	save : function(deviceData, callback) {
		var device = new Device(deviceData);
		device.save(function(err) {
			if (err) {
				logger.error('Failed to save device: ' + JSON.stringify(deviceData));
			}
			callback(err);
		});
	},

	getDevice : function(identification, nodeId, callback) {
		Device.find({
			identification : identification,
			id : nodeId
		}).exec(callback);
	},

	getDevices : function(identification, callback) {
		Device.find({
			identification : identification
		}).exec(callback);
	},

	saveState : function(deviceStateData, callback) {
		var deviceState = new DeviceState(deviceStateData);
		deviceState.save(function(err) {
			if (err) {
				logger.error('Failed to save device state: ' + JSON.stringify(deviceStateData));
			}
			callback(err);
		});
	},

	getState : function(identification, query, callback) {
		DeviceState.find({
			identification : identification
		}).where('timestamp').gte(query.from).lte(query.to).exec(callback);
	}
};

module.exports = new Service();