var EventEmitter = require('events').EventEmitter;
var emitter = new EventEmitter();
var configuration = require('../configuration.js');
var restify = require('restify');
var logger = log4js.getLogger("client");

var client = restify.createJsonClient({
  url: configuration.host,
  version: '*'
});

console.log('using host: ' + configuration.host);


setInterval(function(){
	var timestamp = Math.round((new Date()).getTime() / 1000);
	
	client.get('/ZWaveAPI/Data/' + timestamp, function (err, req, res, json) {
			if (err) {
				logger.error('client failed to retrieve data');
			} else {
				var doorSensorPath = "devices.1.instances.0.commandClasses.32.data.level";
				var doorSensor = json[doorSensorPath];
				
				if (doorSensor) {
					if (doorSensor.value==255) {
						logger.debug('deur sensor triggered');
					} else if (doorSensor.value==0) {
						logger.debug('deur sensor idle');
					}
				}
			}  		
	});
}, 200);

module.exports = emitter;