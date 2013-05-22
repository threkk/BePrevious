var logger = log4js.getLogger("client");
var client = require('../client').client;
var localDB = require('../io').localDB;

/**
 *	renders the home page
 */

function getHome(req, res) {
    res.render('home.hbs', {
        controllerData: client.deviceManager.controller
    });
}

/**
 *	renders the devices page
 */

function getDevices(req, res) {
    var devices = [];

    for (var key in client.deviceManager.devices) {
        var device = client.deviceManager.devices[key];
        var deviceData = JSON.parse(JSON.stringify(device.data));

        var status = 'Ok';
        if (deviceData.batteryLevel < 25) {
            status = 'Low battery';
        } else if (deviceData.isFailed) {
            status = 'Failed';
        }
        deviceData.status = status;
        devices.push(deviceData);
    }

    res.render('devices.hbs', {
        devices: devices
    });
}

/**
 *	renders the edit device page
 */

function editDevice(req, res) {
    var id = req.params.id;
    var device = client.deviceManager.getDevice(id);

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
    updateDevices(nodeid, req.body);
}



function updateAllDevices(req, res) {
	var nodeid = parseInt(req.params.id, 10) || -1;
    var devices = client.deviceManager.devices;
    var device = client.deviceManager.getDevice(nodeid);
    var nodeids = [];
    for (var key in devices) {
        var deviceData = devices[key].data;

        if ((device.data.productType == deviceData.productType) 
            && device.data.manufacturerId == deviceData.manufacturerId) {
            nodeids.push(deviceData.id);
        }
    }
    updateDevices(nodeids, req.body);
}

function updateDevices(nodeids, options) {
	console.log(typeof nodeids)
    if (typeof nodeids == "number") {
        nodeids = [nodeids]
    }

    nodeids.forEach(function (nodeid) {
        if (options.sleeptime) {
            setSleepTime(nodeid, options.sleeptime);
        }

        if (options.calibratedTemp) {
            setCalibratedTemp(nodeid, options.calibratedTemp);
        }
        
		var device = client.deviceManager.getDevice(nodeid);
        device.update();
    });
}

function setSleepTime(nodeid, sleeptime) {
    logger.debug("setting sleeptime for ", nodeid);
    var command = 'devices[' + nodeid + '].instances[0].commandClasses[0x84].Set(' + sleeptime + ',1)'
    client.runCommand(command, function (err, json) {
        if (err) {
            return logger.error(err);
        }
    });
}

function setCalibratedTemp(nodeid, calibratedTemp) {
    var device = client.deviceManager.getDevice(nodeid);
    if (!device) {
        return logger.error('no device with id ' + nodeid + ' was found');
    }

    device.updateMultiLevel(function (err, multilevel) {
        if (err) {
            return logger.error('failed to update device ' + nodeid + ' multilevel');
        }

        var temp = multilevel.Temperature;
        var offset = temp - calibratedTemp;
        localDB.setTempOffset(nodeid, offset);
    });
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