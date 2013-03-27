var EventEmitter = require('events').EventEmitter;
var emitter = new EventEmitter();
var configuration = require('../configuration.js');
var restify = require('restify');
var logger = log4js.getLogger("client");

function Client(restClient) {
    this.restClient = restClient;
}

Client.prototype = {
    updateRate: 500,
    updateTime: 0,
    
    controllerNodeId: null,
    devices: [],
    updates: [],
    pathRegex : null,

    init: function () {
    	this.pathRegex = new RegExp(/^devices\.(\d+)\.instances\.(\d+)\.commandClasses\.(\d+)\.(.*)$/);
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

    _update: function () {
        this.restClient.get('/ZWaveAPI/Data/' + this.updateTime, function (err, req, res, json) {
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
	        		
	        		if (nodeId == this.controllerNodeId) {
	        			continue;
	        		}
	        		
	        		this._handleCommandUpdate(nodeId,instanceId,commandId,json);
	        	}
	        }
        } else if (data.devices) {
        	this.controllerNodeId = data.controller.data.nodeId.value;
        	var devices = data.devices;
            for (var nodeId in devices) {
            	if (nodeId == 255 || nodeId == this.controllerNodeId) {
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
    	
    	for(var key in this.updates) {
    		var commandUpdate = this.updates[key];
    		if (commandUpdate.timestamp==timestamp) {
    			return;
    		}
    	}
    	
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
    	
    	this.updates.splice(0,0,commandUpdate);
    	
    	//TODO: memmory leak, empty array when it gets too big
    	
    	console.log('update: ' + JSON.stringify(commandUpdate));
    }
};

Client.prototype.__proto__ = EventEmitter.prototype;

var restClient = restify.createJsonClient({
    url: configuration.host,
    version: '*'
});

var client = new Client(restClient);
client.init();

module.exports = client;