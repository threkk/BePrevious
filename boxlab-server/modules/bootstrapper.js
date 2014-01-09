var fs = require('fs');
var async = require('async');

var logger = require('./logging').getLogger('app');
var paths = require('./paths');
var localDB = require('./localdatabase').localDB;
var scheduler = require('./scheduler');
var synchronizer = require('./synchronizer');
var deviceManager = require('./zwave/devicemanager').deviceManager;

function bootstrap(callback) {
	var funcs = [ checkPermissions, initDatabase, scheduleSynchronizer, startDeviceManager ];
	async.series(funcs, callback);
}

function checkPermissions(callback) {
	try {
		fs.chmodSync(paths.data_in, 755);
		fs.chmodSync(paths.data_out, 755);
		fs.chmodSync(paths.resources, 755);
		callback();
	} catch (err) {
		logger.error('no write permissions in the data or resource folders');
		callback(err);
	}
}

function initDatabase(callback) {
	localDB.initialize(function(err) {
		if (err) {
			logger.error('failed to initialize the local database')
		} else {
			logger.debug('succesfully loaded the local database');
		}
		callback(err);
	});
}

function scheduleSynchronizer(callback) {
	function onTick() {
		synchronizer.synchronize(function(err) {
			if (err) {
				logger.info('synchronization unsuccesfull:' + JSON.stringify(err));
			} else {
				logger.info('synchronization succesfull');
			}
		});
	}

	scheduler.schedule('00 00 03 * * 1-7', onTick, function(err) {
		if (err) {
			logger.error('failed to schedule synchronization');
		}
		callback(err);
	});
}

function startDeviceManager(callback) {
	deviceManager.start();
	callback();
}

module.exports.bootstrap = bootstrap;