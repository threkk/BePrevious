var fs = require('fs');

var async = require('async');
var restify = require('restify');

var configuration = require('../configuration.js');

var logger = require('./logging').getLogger('io');
var paths = require('./paths');

var identification = require('./identification');
var localDB = require('./localdatabase').localDB;

var messages_file = paths.relative(paths.data_in, 'messages.json');
var entries_file = paths.relative(paths.data_in, 'entries.json');

var restClient = restify.createJsonClient({
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
	var start = +new Date();
	async.series([ downloadFiles, uploadFiles ], function(err) {
		if (!err) {
			localDB.setLastSyncDate(start);
		}
		callback(err);
	});
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
	async.series([ _downloadMessages, _downloadEntries ], callback);
}

function uploadFiles(callback) {
	logger.info('uploading data to master server (not yet implemented)');
	callback();
}

/**
 * downloads the messages from the master server
 * 
 * @param callback
 */
function _downloadMessages(callback) {
	var query = {
		from : localDB.getLastSyncDate()
	};

	var path = '/boxlab/api/' + identification.getIdentity() + '/messages';
	restClient.get(path, function(err, req, res, data) {
		if (err) {
			return callback(err);
		}

		_appendFile(messages_file, data, callback);
	});
}

/**
 * downloads the entries from the master server
 * 
 * @param callback
 */
function _downloadEntries(callback) {
	var query = {
		from : localDB.getLastSyncDate()
	};

	var path = '/boxlab/api/' + identification.getIdentity() + '/entries';
	restClient.get(path, function(err, req, res, data) {
		if (err) {
			return callback(err);
		}

		_appendFile(entries_file, data, callback);
	});
}

/**
 * appends the found data to the given file
 * 
 * @param file
 *            the file to append to
 * @param data
 *            the array that needs to be appended to the file
 * @param callback
 */
function _appendFile(file, data, callback) {
	var lines = '';
	for ( var key in data) {
		lines += data[key] + '\r\n';
	}

	fs.appendFile(file, lines, callback);
}

synchronize(function(err) {
	if (err) {
		console.log('synchronization unsuccesfull' + JSON.stringify(err));
	} else {
		console.log('synchronization succesfull');
	}
});