var EventEmitter = require('events').EventEmitter;
var emitter = new EventEmitter();
var configuration = require('../configuration.js');
var restify = require('restify');
var logger = log4js.getLogger("client");
var _ = require('lodash');

var apiDataPath = '/ZWaveAPI/Data/';
var apiCommandPath = '/ZWaveAPI/Run/';

function Client(restClient) {
    this.restClient = restClient;
    
    this.updateRate = 500;
    this.updateTime = 0;
    
    this.controllerData = {};
    this.devices = [];
    this.updates = [];
}

Client.prototype = {
   
    pathRegex : new RegExp(/^devices\.(\d+)\.instances\.(\d+)\.commandClasses\.(\d+)\.(.*)$/),

    init: function () {
        setTimeout(function () {
            client._update();
        }, this.updateRate);
        
        this.emit('initialized');
    },
    
    getDevice: function(nodeId) {
    	for (var key in this.devices) {
    		var device = this.devices[key];
    		if (device.id==nodeId) {
    			return device;
    		}
    	}
    	return null;
    },
    
    getControllerData: function() {
    	return this.controllerData;
    },
    
    startInclusionMode: function(duration, callback) {
    	var self = this;
    	self._runCommand('controller.AddNodeToNetwork(1)', function(err,json) {
    		if (err) {
    			logger.error('failed to start inclusion mode');
    		} else {
    	 		setTimeout(function() {
    	 			self._runCommand('controller.AddNodeToNetwork(0)', function(err,json) {
    	 				if (err) {
    	 			    	logger.error('failed to stop inclusion mode');
    	 				}
    	 			});
    	 		},duration);
    		}
    	});
    },
    
    _runCommand : function(command, callback) {
        this.restClient.post(apiCommandPath + command, 
    		function (err, req, res, json) {
    		callback && callback(err, json);
    	});
    },

    _update: function () {
        this.restClient.get(apiDataPath + this.updateTime, function (err, req, res, json) {
            if (err) {
                logger.error('client failed to retrieve data');
            } else {
                this._handleUpdate(json);
            }
            this.updateTime = Math.round((new Date()).getTime() / 1000);
            setTimeout(function () {
                client._update();
            }, this.updateRate);
        }.bind(this));
    },

    _handleUpdate: function (data) {
        if (this.updateTime>0) {     
	        for(var key in data) {
	        	var match = this.pathRegex.exec(key);
	        	if (match) {
	        		var nodeId = match[1];
	        		var instanceId = match[2];
	        		var commandId = match[3];
	        		var json= data[key];
	        		
	        		if (nodeId == this.controllerData.nodeId.value) {
	        			continue;
	        		}
	        		
	        		this._handleCommandUpdate(nodeId,instanceId,commandId,json);
	        	}
	        }
        } else if (data.devices) {
        	this.controllerData = data.controller.data;
        	var devices = data.devices;
            for (var nodeId in devices) {
            	if (nodeId == 255 || nodeId == this.controllerData.nodeId.value) {
					// We skip broadcase and self
					continue;
				}
                var device = devices[nodeId];
                this.devices.push({
                    id: nodeId,
                    basicType: device.data.basicType.value,
                    genericType: device.data.genericType.value,
                    specificType: device.data.specificType.value,
                    isListening: device.data.isListening.value,
                    isFLiRS: !device.data.isListening.value && 
                    	(device.data.sensor250.value || device.data.sensor1000.value),
                    hasWakeup: 0x84 in device.instances[0].commandClasses,
                    hasBattery: 0x80 in device.instances[0].commandClasses
                });
            }

            this.devices.map(function (device) {
                logger.debug('devices found: ' + JSON.stringify(device));
            });
        }
    }, 
    
    _handleCommandUpdate: function(nodeId,instanceId,commandId, json) {
    	var device = this.getDevice(nodeId);
    	var timestamp = json.updateTime;
    	var commandUpdate = {
    		device: device,
    		instance: instanceId,
    		timestamp: timestamp,
    		command: {
    			id: commandId,
    			name: json.name,
    			value: json.value,
    			type: json.type
    		}
    	}
    	
    	for(var key in this.updates) {
    		if (_.isEqual(this.updates[key],commandUpdate)) {
    			return;
    		}
    	}
    	
    	this.updates.splice(0,0,commandUpdate);
    	this.updates = this.updates.slice(-50);
    	this.emit('update', commandUpdate);
    }
};

Client.prototype.__proto__ = EventEmitter.prototype;

function getDevices(req, res) {
	res.jsonp(client.devices);
}

function getDevice(req,res) {
	res.jsonp(client.getDevice(req.params.id));
}

function getControllerData(req,res) {
	res.jsonp(client.controllerData);
}

var client = new Client(restify.createJsonClient({
    url: configuration.host,
    version: '*'
}));


var routes = {
	'devices' : {
		get: getDevices,
		'/:id' : {
			get: getDevice
		}
	},
	'controller' : {
		get: getControllerData
	}
};

module.exports = {
	client: client,
	routes: routes
};