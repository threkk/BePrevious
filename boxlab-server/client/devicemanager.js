var logger = log4js.getLogger("client");
var moment = require('moment');
var async = require('async');
var _ = require('lodash');

var DescriptionRepository = require('./descriptionrepository.js').DescriptionRepository;
var Device = require('./device.js').Device;

var command_start_inclusion = 'controller.AddNodeToNetwork(1)';
var command_stop_inclusion = 'controller.AddNodeToNetwork(0)';
var command_start_exclusion = 'controller.RemoveNodeFromNetwork(1)';
var command_stop_exclusion = 'controller.RemoveNodeFromNetwork(0)';

var tickRate = 1000;
var deviceUpdateTicks = 30;
var deviceMultilevelTicks = 15;

function DeviceManager(client) {
    this.client = client;
    this.descriptions = new DescriptionRepository();

    this.controller = {};
    this.devices = [];
    this.mode = 'idle';

    var self = this;

    //schedule the devicemanager to update every minute
    var updateInterval = 30 * 1000;
    var tick = 0;
    setInterval(function() {
    	//update every tick if we are including/excluding, or every device update tick
    	if (self.mode !='idle' || ((tick % deviceUpdateTicks) == 0)) {
        	self.update(function(err){
        		if (!err) {
        			logger.debug('updated devices list');
        		} else {
        			logger.debug('failed to update devices list ' + JSON.stringify(err));
        		}
        	});
        }
        
		if ((tick % deviceMultilevelTicks) == 0) {
	        //update the multilevel values of every device
	        var updateFuncs = [];
	        self.devices.forEach(function(device){
	        	updateFuncs.push(function(callback){
	        		device.updateMultiLevel(function(err){
	        			callback(err);
	        		});
	        	});
	        });
	        
	        async.series(updateFuncs, function(err, results) {
	        	if (!err) {
	        		logger.debug('updated multi level value of devices');
	        	} else {
	        		logger.debug('failed to update multilevel values of devices: '+JSON.stringify(err));
	        	}
	        });
		}
        
        tick++;
    }, tickRate);

}

DeviceManager.prototype = {
    update: function (fn) {
        var self = this;
        self.client.getApiData(0, function (err, data) {
        	if (err) { 
        		fn && fn(err);
        	}
        	
            var newDevices = [];
            if (!data || !data.devices) {
                return;
            }
            
            self.controller = data.controller.data;
            
            for (var nodeId in data.devices) {
                if (nodeId == 255 || nodeId == self.controller.nodeId.value) {
                    // We skip broadcase and self
                    continue;
                }

                var deviceData = data.devices[nodeId].data;
                var commandClasses = data.devices[nodeId].instances[0].commandClasses;
                var newDevice = {
                    id: nodeId,
                    basicType: deviceData.basicType.value,
                    genericType: deviceData.genericType.value,
                    specificType: deviceData.specificType.value,
                    manufacturerId: deviceData.manufacturerId.value,
                    productId: deviceData.manufacturerProductId.value,
                    productType: deviceData.manufacturerProductType.value,
                    isListening: deviceData.isListening.value,
                    isFLiRS: !deviceData.isListening.value &&
                        (deviceData.sensor250.value || deviceData.sensor1000.value),
                    isFailed: deviceData.isFailed.value,
                    hasWakeup: 0x84 in commandClasses,
                    hasBattery: 0x80 in commandClasses
                };

                if (newDevice.hasBattery) {
                	var batteryLevel = commandClasses[0x80].data.last.value;
                    if (batteryLevel == 255) { 
						batteryLevel = 0; // by CC Battery specs
					}
                    newDevice.batteryLevel = batteryLevel;
                }
                
                if(newDevice.hasWakeup){
                	newDevice.wakeupInterval= commandClasses[0x84].data.interval.value
                }
                newDevices.push(newDevice);
            }

            self._updateDevices(newDevices, function(err) {
            	fn && fn(err);
            });
        });
    },

    startInclusionMode: function (duration, callback) {
        var self = this;
        if (self.mode == 'including') {
            return;
        }
        
        self.mode = 'including';
        self.client.runCommand(command_start_inclusion, function (err, json) {
            if (err) {
                return callback(err);
            }
			
            callback && callback(null, json);

            setTimeout(function () {
                self.stopInclusionMode(function (err) {
                	if (err) {
                    	logger.error('failed to stop inclusion mode: ' + JSON.stringify(err));
                    }
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
        if (self.mode == 'excluding') {
            return;
        }
        
        self.mode = 'excluding';
        self.client.runCommand(command_start_exclusion, function (err, json) {
            if (err) {
                return callback(err);
            }

            callback && callback(null, json);

            setTimeout(function () {
                self.stopExclusionMode(function (err) {
                    if (err) {
                    	logger.error('failed to stop exclusion mode: ' + JSON.stringify(err));
                    }
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

    getDevice: function (id) {
        for (var key in this.devices) {
            var device = this.devices[key];
            if (device.data.id == id) {
                return device;
            }
        }
        return null;
    },

	// input is an array of device data. syncs the list of devicemanager with newDevices data.
    _updateDevices: function (newDevices, fn) {
    	try {
	        for (var index in newDevices) {
	            var newDevice = newDevices[index];
	            if (_.findIndex(this.devices, function (device) {
	                return device.data.id == newDevice.id;
	            }) < 0) {
	                this._insertDevice(newDevice);
	            }
	        }
	
	        for (var index in this.devices) {
	            var oldDevice = this.devices[index];
	            if (_.findIndex(newDevices, function (newDevice) {
	                return oldDevice.data.id == newDevice.id;
	            }) < 0) {
	                this._removeDevice(index);
	            }
	        }
        } catch(err) {
        	fn && fn(err);
        }

		var updateFuncs = [];
        this.devices.forEach(function (device) {
            newDevices.forEach(function (newDevice) {
                if (device.data.id == newDevice.id) {
                	updateFuncs.push(function(callback){
                		device.update(newDevice, function(err){
                			callback(err);
                		});
                	});
                    return;
                }
            });
        });
        
        async.series(updateFuncs, function(err, results) {
        	fn && fn(err);
        });
    },

    _insertDevice: function (deviceData) {
        this.descriptions.updateDeviceDescription(deviceData);
        var device = new Device(this);
        for (var key in deviceData) {
            device.data[key] = deviceData[key];
        }
   	 	this.devices.push(device);
    	this.client.emit('device_added', device.data);
    },

    _removeDevice: function (index) {
        var oldDevice = this.devices[index];
        this.client.emit('device_removed', oldDevice.data);
        this.devices.splice(index, 1);
    }, 
    
    _startUpdateTimer: function(duration, tick) {
    	var self = this;
    	var timer = setInterval(function(){
    		self.update();
    	},tick);
    	
    	setTimeout(function(){
    		clearInterval(timer);
    	}, duration);
    }
}


module.exports.DeviceManager = DeviceManager