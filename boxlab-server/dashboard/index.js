var logger = log4js.getLogger("client");
var client = require('../client').client;
var localDB = require('../io').localDB;
var async = require('async');

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
        if (deviceData.batteryLevel) {
        	var fraction = Math.round(deviceData.batteryLevel / 25) * 25;
        	deviceData.batteryImage = '/images/battery/battery-' + fraction + '.png';
        }
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
	if (nodeid > 0) { 
	    _updateDevices(nodeid, req.body, function(err){
	    	res.end();
	    });
	}
}

function updateAllDevices(req, res) {
	var nodeid = parseInt(req.params.id, 10) || -1;
	if (nodeid < 0) {
		return;
	}
	
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
    _updateDevices(nodeids, req.body, function(err){
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
	            setSleepTime(nodeids, options.sleeptime, function(err) {
	            	callback(err);
	            });
            }
        },
        function (callback) {
            if (!options.calibratedTemp) {
            	callback(null);
            } else {
            	setCalibratedTemp(nodeids, options.calibratedTemp, function(err) {
            		callback(err);
            		nodeids.forEach(function(nodeid){
            			var device = client.deviceManager.getDevice(nodeid);
            			device.updateMultiLevel();
            		});
            	});
            }
        }
    ], function (err, results) {
    	fn(err, results);
    });
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
	
	async.series(funcs, function(err, results) {
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
	        var device = client.deviceManager.getDevice(nodeid);
	        if (!device) {
	            return callback('no device with id ' + nodeid + ' was found');
	        }
	        
	        device.updateMultiLevel(function (err, multilevel) {
	        	if (err) {
	        		return callback('failed to update device ' + nodeid + ' multilevel');
	        	}
	        	var temp = multilevel.Temperature;
        		var offset = calibratedTemp - temp;
        		localDB.setTempOffset(nodeid, offset);
        		
        		logger.debug('setting temp offset to (' + offset + 'c) for ' + nodeid);
        		
        		return callback(null);
	        });	        
	    });
	});
    
    
    async.series(funcs, function(err, results) {
	    callback && callback(err);
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