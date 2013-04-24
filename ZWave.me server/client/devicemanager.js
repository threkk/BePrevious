var logger = log4js.getLogger("client");
var moment = require('moment');
var _ = require('lodash');

var DescriptionRepository = require('./descriptionrepository.js').DescriptionRepository;

var command_start_inclusion = 'controller.AddNodeToNetwork(1)';
var command_stop_inclusion = 'controller.AddNodeToNetwork(0)';
var command_start_exclusion = 'controller.RemoveNodeFromNetwork(1)';
var command_stop_exclusion = 'controller.RemoveNodeFromNetwork(0)';

function DeviceManager(client) {
    this.client = client;
    this.descriptions = new DescriptionRepository();
   
    this.controller = {};
    this.devices = [];

	this.mode = 'idle';
}

DeviceManager.prototype = {
	
	update: function() {
		var self = this;
		this.client.getApiData(0, function(err, data) {
			var newDevices = [];
			if (!data || !data.devices) {
				return;
			}
            for (var nodeId in data.devices) {
                this.controller = data.controller.data;
                if (nodeId == 255 || nodeId == this.controller.nodeId.value) {
                    // We skip broadcase and self
                    continue;
                }

                var deviceData = data.devices[nodeId];
                var newDevice = {
                    id: nodeId,
                    basicType: deviceData.data.basicType.value,
                    genericType: deviceData.data.genericType.value,
                    specificType: deviceData.data.specificType.value,
                    isFailed: deviceData.data.isFailed.value,
                    manufacturerId:deviceData.data.manufacturerId.value,
                    manufacturerProductId: deviceData.data.manufacturerProductId.value,
                    manufacturerProductType: deviceData.data.manufacturerProductType.value,
                    isListening: deviceData.data.isListening.value,
                    isFLiRS: !deviceData.data.isListening.value &&
                        (deviceData.data.sensor250.value || deviceData.data.sensor1000.value),
                    hasWakeup: 0x84 in deviceData.instances[0].commandClasses,
                    hasBattery: 0x80 in deviceData.instances[0].commandClasses
                };
                
               	if(newDevice.hasWakeup){
               	 	var lastWakeUp = deviceData.instances[0].commandClasses[0x84].data.lastWakeup.value;           
                	newDevice.lastWakeup = moment(parseInt(lastWakeUp, 10)*1000).fromNow();
            	}
                if (newDevice.hasBattery) {
                    newDevice.batteryLevel = deviceData.instances[0].commandClasses[0x80].data.last.value
                }
                newDevices.push(newDevice);
            }
            
            self._updateDevices(newDevices);            
		});
	}, 
	
	startInclusionMode: function (duration, callback) {
        var self = this;
        
        if (self.mode=='including') {
        	return;
        }
        self.client.runCommand(command_start_inclusion, function(err, json){
        	if (err) {
        		return callback(err);
        	}
        	
        	self.mode = 'including';
        	
        	callback(null,json);

        	setTimeout(function(){
        		self.stopInclusionMode(function(err){
        			logger.error('failed to stop inclusion mode');
        		});
        	}, duration);
        });
    },

    stopInclusionMode: function (callback) {
        var self = this;
        if (self.mode != 'including') {
        	return;
		}
        this.client.runCommand(command_stop_inclusion, function (err, json) {
            self.mode = 'idle';
            self.update();
            callback && callback(err, json);
        });
    },

    startExclusionMode: function (duration, callback) {
        var self = this;
        
        if (self.mode=='excluding') {
        	return;
        }
        self.client.runCommand(command_start_exclusion, function(err, json){
        	if (err) {
        		return callback(err);
        	}
        	
        	self.mode = 'excluding';
        	
        	callback(null,json);

        	setTimeout(function(){
        		self.stopExclusionMode(function(err){
        			logger.error('failed to stop exclusion mode');
        		});
        	}, duration);
        });
    },

    stopExclusionMode: function (callback) {
        var self = this;
        if (self.mode != 'excluding') {
        	return;
		}
        this.client.runCommand(command_stop_exclusion, function (err, json) {
            self.mode = 'idle';
            self.update();
            callback && callback(err, json);
        });
    },
	
	getController: function () {
        return this.controller;
    },
	
	getDevice: function (nodeId) {
        for (var key in this.devices) {
            var device = this.devices[key];
            if (device.id == nodeId) {
                return device;
            }
        }
        return null;
    }, 
    
    _updateDevices: function (newDevices) {
        for (var index in newDevices) {
            var newDevice = newDevices[index];
            if (_.findIndex(this.devices, function (device) {
                return device.id == newDevice.id;
            }) < 0) {
                this.client.emit('device_added', newDevice);
                this.devices.push(newDevice);
            }
        }

        for (var index in this.devices) {
            var oldDevice = this.devices[index];
            if (_.findIndex(newDevices, function (newDevice) {
                return oldDevice.id == newDevice.id;
            }) < 0) {
                this.client.emit('device_removed', oldDevice);
                this.devices.splice(index, 1);
            }
        }

		var self = this;
        this.devices.forEach(function (device) {            
            self.descriptions.updateDeviceDescription(device);
            logger.debug('devices found: ' + JSON.stringify(device));
        });
    }, 
}


module.exports.DeviceManager = DeviceManager