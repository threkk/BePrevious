var async = require('async');
var restify = require('restify');

var configuration = require('../configuration.js');

var logger = require('./logging').getLogger('io');
var connector = require('./internetconnector').connector;
var localDB = require('./localdatabase').localDB;

var downloader = require('./synchronization/download');
var uploader = require('./synchronization/upload');

var client = restify.createJsonClient({
	url : configuration.master_host,
	version : '*'
});

/**
 * Starts the synchronization with the master server. Data is downloaded and
 * uploaded from and to the master server.
 * 
 * @param callback
 */
function synchronize(callback) {
	function doConnect(fn) {
		connector.connect(fn);
	}

	function doSync(fn) {
		logger.info('synchronizing with master server');
		var start = +new Date();
		async.series([ downloadFiles, uploadFiles ], function(err) {
			if (!err) {
				localDB.setLastSyncDate(start);
			}
			callback(err);
		});
	}

	function doDisconnect(fn) {
		connector.disconnect(function(err) {
			if (err) {
				logger.error('failed to disconnect to the internet');
			}
			fn();
		});
	}

	async.series([ doConnect, doSync, doDisconnect ], callback);
}

/**
 * Starts the downloading of all necessary information from the master server.
 * More specifically, the messages and entries from the server up from the last
 * synchronization date.
 * 
 * @param callback
 */
function downloadFiles(callback) {
	logger.info('downloading data from master server');

	function doDownloadMessages(fn) {
		downloader.downloadMessages(client, fn);
	}

	function doDownloadEntries(fn) {
		downloader.downloadEntries(client, fn);
	}

	async.series([ doDownloadMessages, doDownloadEntries ], callback);
}

function uploadFiles(callback) {
	logger.info('uploading data to master server (not yet implemented)');

	function doUploadDevices(fn) {
		uploader.uploadDevices(client, fn);
	}

	function doUploadFiles(fn) {
		uploader.uploadFiles(client, fn);
	}

	async.series([ doUploadDevices, doUploadFiles ], callback);
}

module.exports.synchronize = synchronize;
module.exports.synchronize(function(err) {
	console.log('finished');
	if (err)
		console.log('err: ' + JSON.stringify(err));
});
