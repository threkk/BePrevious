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

    this.init();
}

Client.prototype = {

    pathRegex: new RegExp(/^devices\.(\d+)\.instances\.(\d+)\.commandClasses\.(\d+)\.(.*)$/),

    init: function () {
        setTimeout(function () {
            client._update();
        }, this.updateRate);

        this.emit('initialized');
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

    getControllerData: function () {
        return this.controllerData;
    },

    startInclusionMode: function (duration, callback) {
        var self = this;
        this._runCommand('controller.AddNodeToNetwork(1)', function (err, json) {
            if (err) {
                logger.error('failed to start inclusion mode');
            } else {
                setTimeout(function () {
                    self._runCommand('controller.AddNodeToNetwork(0)', function (err, json) {
                        if (err) {
                            logger.error('failed to stop inclusion mode');
                        }
                        self.updateTime = 0;
                    });
                }, duration);
                console.log('run command callback finished');
            }
        });
    },

    stopInclusionMode: function (callback) {
        var self = this;
        this._runCommand('controller.AddNodeToNetwork(0)', function (err, json) {
            callback && callback(err, json);
	        self.updateTime = 0;
        });
    },

    startExclusionMode: function (duration, callback) {
        var self = this;
        this._runCommand('controller.RemoveNodeFromNetwork(1)', function (err, json) {
            if (err) {
                logger.error('failed to start inclusion mode');
            } else {
                setTimeout(function () {
                    self._runCommand('controller.RemoveNodeFromNetwork(0)', function (err, json) {
                        if (err) {
                            logger.error('failed to stop inclusion mode');
                        }
                        self.updateTime = 0;
                    });
                }, duration);
            }
        });
    },

    stopExclusionMode: function (callback) {
    	var self = this;
        this._runCommand('controller.RemoveNodeFromNetwork(0)', function (err, json) {
            callback && callback(err, json);
            self.updateTime = 0;
        });
    },

    _runCommand: function (command, callback) {
        logger.debug('executing command: ' + command);
        this.restClient.get(apiCommandPath + command, function (err, req, res, json) {
            logger.debug('finished executing command: ' + command);
            callback && callback(err, json);
            logger.debug('callback executed');
        });
    },

    _update: function () {
    	var self = this;
        this.restClient.get(apiDataPath + this.updateTime, function (err, req, res, json) {
            if (err) {
                logger.error('client failed to retrieve data');
            } else {
                this._handleUpdate(json);
            }
            self.updateTime = Math.round((new Date()).getTime() / 1000);
            setTimeout(function () {
                client._update();
            }, this.updateRate);
        }.bind(this));
    },

    _handleUpdate: function (data) {
        if (this.updateTime > 0) {
            for (var key in data) {
                var match = this.pathRegex.exec(key);
                if (match) {
                    var nodeId = match[1];
                    var instanceId = match[2];
                    var commandId = match[3];
                    var json = data[key];

                    if (nodeId == this.controllerData.nodeId.value) {
                        continue;
                    }

                    this._handleCommandUpdate(nodeId, instanceId, commandId, json);
                }
            }
        } else if (data.devices) {
            var newDevices = [];
            for (var nodeId in data.devices) {
                this.controllerData = data.controller.data;
                if (nodeId == 255 || nodeId == this.controllerData.nodeId.value) {
                    // We skip broadcase and self
                    continue;
                }

                var deviceData = data.devices[nodeId];
                var newDevice = {
                    id: nodeId,
                    basicType: deviceData.data.basicType.value,
                    genericType: deviceData.data.genericType.value,
                    specificType: deviceData.data.specificType.value,
                    isListening: deviceData.data.isListening.value,
                    isFLiRS: !deviceData.data.isListening.value &&
                        (deviceData.data.sensor250.value || deviceData.data.sensor1000.value),
                    hasWakeup: 0x84 in deviceData.instances[0].commandClasses,
                    hasBattery: 0x80 in deviceData.instances[0].commandClasses
                };

                newDevices.push(newDevice);
            }
            this._handleDeviceUpdate(newDevices);
        }
    },

    _handleDeviceUpdate: function (newDevices) {
        for (var index in newDevices) {
            var newDevice = newDevices[index];
            if (_.findIndex(this.devices, function (device) {
                return device.id == newDevice.id;
            }) < 0) {
                this.emit('device_added', newDevice);
                this.devices.push(newDevice);
            }
        }

        for (var index in this.devices) {
            var oldDevice = this.devices[index];
            if (_.findIndex(newDevices, function (newDevice) {
                return oldDevice.id == newDevice.id;
            }) < 0) {
                this.emit('device_removed', oldDevice);
                this.devices.splice(index, 1);
            }
        }

        this.devices.map(function (device) {
            logger.debug('devices found: ' + JSON.stringify(device));
        });
    },

    _handleCommandUpdate: function (nodeId, instanceId, commandId, json) {
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


        for (var key in this.updates) {
            if (_.isEqual(this.updates[key], commandUpdate)) {
                return;
            }
        }

        this.updates.splice(0, 0, commandUpdate);
        this.updates = this.updates.slice(-50);
        this.emit('update', commandUpdate);
    }
};

Client.prototype.__proto__ = EventEmitter.prototype;

function getDevices(req, res) {
    res.jsonp(client.devices);
}

function getDevice(req, res) {
    res.jsonp(client.getDevice(req.params.id));
}

function getControllerData(req, res) {
    res.jsonp(client.controllerData);
}

var client = new Client(restify.createJsonClient({
    url: configuration.host,
    version: '*'
}));


var routes = {
    'devices': {
        get: getDevices,
        '/:id': {
            get: getDevice
        }
    },
    'controller': {
        get: getControllerData
    }
};

module.exports = {
    client: client,
    routes: routes
};