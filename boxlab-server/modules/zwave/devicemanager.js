var async = require('async');
var EventEmitter = require('events').EventEmitter;

var configuration = require('../../configuration');
var logger = require('../logging').getLogger('client');

var Device = require('./device').Device;
var client = require('./client').client;

var MODE_IDLE = 'idle';
var MODE_INCLUDING = 'including';
var MODE_EXCLUDING = 'excluding';

function tickTest(ticks) {
    return function (manager, tick) {
        return ((tick % ticks) == 0);
    }
}

var updateCommands = {
    updateDevicesCommand: {
        command: require('./updateDevicesCommand').updateDevicesCommand,
        test: function (manager, tick) {
            return ((manager.mode != MODE_IDLE) || tick % configuration.deviceUpdateTicks);
        }
    },
    deviceBatteryTicks: {
        command: require('./updateBatteryCommand').updateBatteryCommand,
        test: tickTest(configuration.deviceBatteryTicks)
    },
    updateMeterCommand: {
        command: require('./updateMeterCommand').updateMeterCommand,
        test: tickTest(configuration.deviceMeterTicks)
    },
    updateMultiLevelCommand: {
        command: require('./updateMultiLevelCommand').updateMultiLevelCommand,
        test: tickTest(configuration.deviceMultilevelTicks)
    }
}

    function DeviceManager() {
        this.controller = {};
        this.devices = [];

        this.modeTimer = null;
        this.mode = 'idle';
        this.running = false;

        this.start();
    }

DeviceManager.prototype = {
    start: function () {
        var tick = 0;
        var tickRate = configuration.tickRate;
        var self = this;

        self.running = true;

        function test() {
            return self.running;
        }

        function iteration(callback) {
           	self.update(function(err){
           		if (err) {
                    logger.debug('failed to update device manager: ' + JSON.stringify(err));
                }

                // increment the tick
                tick++;

                setTimeout(callback, tickRate);
           	}, tick);
        }

        function finish(err) {
            if (err) {
                logger.error('device manager encountered an error: ' + JSON.stringify(err));
            }

            logger.debug('device manager has stopped running');
        }

        async.whilst(test, iteration, finish);
    },

    stop: function () {
        this.running = false;
    },

    update: function (callback, tick) {
    	var self = this;
        var commands = [];
        for (var index in updateCommands) {
            var updateCommand = updateCommands[index];
            if (tick && !updateCommand.test(this, tick)) {
            	continue;
            }
            commands.push(updateCommand.command);
        }

        var iterator = function (command, callback) {
            command(self, callback);
        }

        async.each(commands, iterator, callback);
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

    insertDevice: function (deviceData) {
        var device = new Device(this);
        for (var key in deviceData) {
            device.data[key] = deviceData[key];
        }
        this.devices.push(device);
        logger.debug('device added: ' + JSON.stringify(device.data));
        this.emit('device_added', device.data);
    },

    removeDevice: function (index) {
        var oldDevice = this.devices[index];
        this.emit('device_removed', oldDevice.data);
        this.devices.splice(index, 1);
    },

    changeMode: function (mode, duration, callback) {
        this.timer && clearTimeout(this.timer);
        var self = this;
        if (duration > 0) {
            self._startMode(mode, duration, callback);
            self.timer = setTimeout(function () {
                self._stopMode(mode);
            }, duration);
        } else {
            self._stopMode(mode, callback);
        }
    },

    _startMode: function (mode, duration, callback) {
        if (this.mode != MODE_IDLE) {
            return callback && callback();
        }

        var self = this;
        if (mode == 'include') {
            client.startInclusionMode(duration, function (err) {
                if (!err) {
                    self.mode = MODE_INCLUDING;
                }
                callback && callback(err);
            });
        } else if (mode == 'exclude') {
            client.startExclusionMode(duration, function (err) {
                if (!err) {
                    self.mode = MODE_EXCLUDING;
                }
                callback && callback(err);
            });
        }
    },

    _stopMode: function (mode, callback) {
        if (this.mode == MODE_IDLE) {
            return callback();
        }

        var self = this;
        if (mode == 'include') {
            client.stopInclusionMode(function (err) {
                self.mode = MODE_IDLE;
                callback && callback(err);
            });
        } else if (mode == 'exclude') {
            client.stopExclusionMode(function (err) {
                self.mode = MODE_IDLE;
                callback && callback(err);
            });
        }
    }
}

DeviceManager.prototype.__proto__ = EventEmitter.prototype;

module.exports.deviceManager = new DeviceManager();