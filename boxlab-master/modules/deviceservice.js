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
				logger.error('Failed to save device with data: '
						+ JSON.stringify(deviceData));
			}
			callback(err);
		});
	}, 
	
	saveState: function(deviceState, callback) {
		var deviceState = new DeviceState(deviceData);
		deviceState.save(function(err) {
			if (err) {
				logger.error('Failed to save device state with data: ' + JSON.stringify(deviceData));
			}
			callback(err);
		});
	}
};

module.exports.service = new Service();