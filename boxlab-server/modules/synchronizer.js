var fs = require('fs');

var async = require('async');
var restify = require('restify');
var moment = require('moment');

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
	async.series([ _uploadDevices, _uploadFiles ], callback);
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
		logger.debug('downloaded: ' + JSON.stringify(data));
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
		lines += JSON.stringify(data[key]) + '\r\n';
	}

	fs.appendFile(file, lines, callback);
}

/**
 * uploads the currently registered devices to the master server
 * 
 * @param callback
 */
function _uploadDevices(callback) {
	logger.debug('need to send devices to server', JSON.stringify(stateUpdate));
	logger.debug('NOT YET IMPLEMENTED');
	callback();
}

/**
 * uploads all the json files to the master server
 * 
 * @param callback
 */
function _uploadFiles(callback) {
	_listUploadFiles(function(err, files) {
		logger.debug("files to upload: " + JSON.stringify(files));
		async.eachSeries(files, function(file, fn) {
			_parseFile(file, function(err, data) {
				if (err) {
					return fn(err);
				}

				_uploadParsedFile(data, fn);
			})
		}, callback);
	});
}

/**
 * parses the given file to a json array
 * 
 * @param file
 *            the file to parse
 * @param callback(error,
 *            result)
 */
function _parseFile(file, callback) {
	fs.readFile(file, function(err, data) {
		if (err) {
			return callback(err);
		}

		// data is an object, not a string..
		var formatted = '' + data;
		// split the data into an array per line
		formatted = formatted.split('\r\n');

		var parsed = [];
		for ( var key in formatted) {
			if (formatted[key].length < '{}'.length) {
				continue;
			}

			parsed.push(JSON.parse(formatted[key]));
		}

		callback(null, parsed);
	});
}

/**
 * uploads the given parsed data to the master server
 * 
 * @param data
 * @param callback
 */
function _uploadParsedFile(data, callback) {
	async.eachSeries(data, function(stateUpdate, fn) {
		logger
				.debug('need to send "%s" to server', JSON
						.stringify(stateUpdate));
		logger.debug('NOT YET IMPLEMENTED');
		fn();
	}, callback)
}

/**
 * lists the files that need to be parsed and uploaded to the master server
 * 
 * @param callback(error,result)
 */
function _listUploadFiles(callback) {
	// read all files in the data/out directory
	function readFiles(fn) {
		fs.readdir(paths.data_out, fn)
	}

	// filter out files that do not need to be sent
	function filterFiles(files, fn) {
		async.filter(files, function(filename, fn) {
			var keep = false;
			var ext = filename.split('.').pop();

			if (ext == 'json') {
				// we want to upload files that are json files
				keep = true;

				var name = filename.substr(0, filename.length - 5);
				var todayName = moment().format('YYYY-MM-DD');
				if (name == todayName) {
					// dont want to upload the file that were still modifying
					// e.g. files that are of this date
					keep = false;
				}
			}

			fn(keep);
		}, function(filtered) {
			fn(null, filtered);
		});
	}

	// prepend the correct path to the filenames
	function sanitizeFiles(files, fn) {
		var sanitized = [];
		for ( var key in files) {
			var filename = files[key];
			sanitized.push(paths.relative(paths.data_out, filename));
		}
		fn(null, sanitized);
	}

	async.waterfall([ readFiles, filterFiles, sanitizeFiles ], callback);
}

synchronize(function(err) {
	if (err) {
		console.log('synchronization unsuccesfull' + JSON.stringify(err));
	} else {
		console.log('synchronization succesfull');
	}
});