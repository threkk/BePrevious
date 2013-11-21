var async = require('async');
var _ = require('lodash');

var logger = require('../modules/logging').getLogger();
var localDB = require('../modules/localdatabase').localDB;
var deviceManager = require('../modules/zwave/devicemanager').deviceManager;

function getNormalizedDevices() {
    var devices = [];

    for (var key in deviceManager.devices) {
        var device = deviceManager.devices[key];
        var deviceData = _.merge(device.data, device.state);

        deviceData.status = 'Ok';
        if (deviceData.batteryLevel < 25) {
            deviceData.status = 'Low battery';
        } else if (deviceData.isFailed) {
            deviceData.status = 'Failed';
        }

        if (deviceData.batteryLevel) {
            var prefix = '/images/battery/battery-';
            var fraction = Math.round(deviceData.batteryLevel / 25) * 25;
            deviceData.batteryImage = prefix + fraction + '.png';
        }

        devices.push(deviceData);
    }

    return devices;
}

/**
 * optional callback callback(err)
 */
function setSleepTime(nodeids, sleeptime, callback) {
    if (typeof nodeids == "number") {
        nodeids = [nodeids]
    }

    var funcs = [];
    nodeids.forEach(function (nodeid) {
        funcs.push(function (callback) {
            var command = 'devices[' + nodeid + '].instances[0].commandClasses[0x84].Set(' + sleeptime + ',1)'
            client.runCommand(command, function (err, json) {
                if (!err) {
                    logger.debug('setting sleeptime(' + sleeptime + 's) for ' + nodeid);
                }
                return callback(err, json);
            });
        });
    });

    async.series(funcs, function (err, results) {
        callback && callback(err);
    });
}

/**
 * optional callback callback(err)
 */
function setCalibratedTemp(nodeids, calibratedTemp, callback) {
    if (typeof nodeids == "number") {
        nodeids = [nodeids]
    }

    var funcs = [];
    nodeids.forEach(function (nodeid) {
        funcs.push(function (callback) {
            var device = deviceManager.getDevice(nodeid);
            if (!device) {
                return callback('no device with id ' + nodeid + ' was found');
            }

            var device = deviceManager.getDevice(nodeid);
            var temp = device.state.temperature;
            var offset = calibratedTemp - temp;
            localDB.setTempOffset(nodeid, offset);

            logger.debug('setting temp offset to (' + offset + 'c) for ' + nodeid);
            callback();
        });
    });

    deviceManager.update(function (err) {
        if (err) {
            callback('failed to update device states');
        } else {
            async.series(funcs, function (err, results) {
                callback && callback(err);
            });
        }
    });
}

function updateAllDevices(req, res) {
    var nodeid = parseInt(req.params.id, 10) || -1;
    if (nodeid < 0) {
        return;
    }

    var devices = deviceManager.devices;
    var device = deviceManager.getDevice(nodeid);
    var nodeids = [];
    for (var key in devices) {
        var deviceData = devices[key].data;
        if ((device.data.productType == deviceData.productType) && device.data.manufacturerId == deviceData.manufacturerId) {
            nodeids.push(deviceData.id);
        }
    }
    _updateDevices(nodeids, req.body, function (err) {
        res.end();
    });
}

function _updateDevices(nodeids, options, fn) {
    if (typeof nodeids == "number") {
        nodeids = [nodeids]
    }

    if (!options.sleeptime && !options.calibratedTemp) {
        return;
    }

    async.series([
        function (callback) {
            if (!options.sleeptime) {
                callback(null);
            } else {
                setSleepTime(nodeids, options.sleeptime, function (err) {
                    callback(err);
                });
            }
        },
        function (callback) {
            if (!options.calibratedTemp) {
                callback(null);
            } else {
                setCalibratedTemp(nodeids, options.calibratedTemp, callback);
            }
        }
    ], function (err, results) {
        fn(err, results);
    });
}

/**
 *	renders the home page
 */
function getHome(req, res) {
    res.render('home.hbs', {
        controllerData: deviceManager.controller
    });
}

/**
 *	renders the devices page
 */
function getDevices(req, res) {
    res.render('devices.hbs', {
        devices: getNormalizedDevices()
    });
}

/**
 *	renders the edit device page
 */

function editDevice(req, res) {
    var id = req.params.id;
    var device = deviceManager.getDevice(id);

    if (!device) {
        throw new Error('device ' + id + ' not found');
    } else {
        res.render('editDevice.hbs', {
            device: device.data
        });
    }
}

/**
 *	receives the post from the edit device page
 */

function updateDevice(req, res) {
    var nodeid = parseInt(req.params.id, 10) || -1;
    if (nodeid > 0) {
        _updateDevices(nodeid, req.body, function (err) {
            res.end();
        });
    }
}

exports.routes = {
    get: getHome,
    'devices': {
        get: getDevices,
        '/edit/:id': {
            get: editDevice,
            post: updateDevice
        },
        '/edit/all/:id': {
            post: updateAllDevices
        }
    }
}