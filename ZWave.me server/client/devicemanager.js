var logger = log4js.getLogger("client");
var moment = require('moment');
var _ = require('lodash');

var DescriptionRepository = require('./descriptionrepository.js').DescriptionRepository;
var Device = require('./device.js').Device;

var command_start_inclusion = 'controller.AddNodeToNetwork(1)';
var command_stop_inclusion = 'controller.AddNodeToNetwork(0)';
var command_start_exclusion = 'controller.RemoveNodeFromNetwork(1)';
var command_stop_exclusion = 'controller.RemoveNodeFromNetwork(0)';

function DeviceManager(client) {
    this.client = client;
    this.descriptions = new DescriptionRepository();

    this.controller = {};
    this.devices = [];

    var self = this;

    //schedule the devicemanager to update every minute
    var updateInterval = 30 * 1000;
    setInterval(function() {
        self.update();
        
        //update the multilevel values of every device
        self.devices.forEach(function(device){
        	device.updateMultiLevel();
        });
    }, updateInterval);

    this.mode = 'idle';
}

DeviceManager.prototype = {
    update: function () {
        var self = this;
        this.client.getApiData(0, function (err, data) {
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
                    newDevice.batteryLevel = commandClasses[0x80].data.last.value
                }
                if(newDevice.hasWakeup){
                	newDevice.wakeupInterval= commandClasses[0x84].data.interval.value
                }
                newDevices.push(newDevice);
            }

            self._updateDevices(newDevices);
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
			
			self._startUpdateTimer(duration, 1500);
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

			self._startUpdateTimer(duration, 1500);
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

    _updateDevices: function (newDevices) {
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

        this.devices.forEach(function (device) {
            newDevices.forEach(function (newDevice) {
                if (device.data.id == newDevice.id) {
                    device.update(newDevice);
                    return;
                }
            });
        });
    },

    _insertDevice: function (deviceData) {
        this.descriptions.updateDeviceDescription(deviceData);
        var device = new Device(this);
        for (var key in deviceData) {
            device.data[key] = deviceData[key];
        }
        
        var self = this;
        device.updateMultiLevel(function(err) {
        	self.client.emit('device_added', device.data);
       	 	self.devices.push(device);
        });
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