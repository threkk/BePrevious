var async = require('async');
var fs = require('fs');
var moment = require('moment');

var logger = require('../logging').getLogger('io');
var identification = require('../identification');
var paths = require('../paths');
var deviceManager = require('../zwave/devicemanager').deviceManager;

var upload_devices_path = '/boxlab/api/' + identification.getIdentity() + '/devices';
var upload_file_path = '/boxlab/api/' + identification.getIdentity() + '/devices/state';

/**
 * uploads the currently registered devices to the master server
 * 
 * @param callback
 */
function uploadDevices(client, callback) {
	deviceManager.update(function(err) {
		if (err) {
			return callback(err);
		}
		var devices = deviceManager.devices;
		if (devices.length < 1) {
			logger.debug('no devices found to upload');
			callback();
		} else {
			async.map(devices, function(device, fn) {
				fn(null, device.data);
			}, function(err, results) {
				if (err) {
					return callback(err);
				}

				client.post(upload_devices_path, {
					devices : results
				}, function(err, req, res) {
					callback(err);
				})
			});
		}
	});
}

/**
 * uploads all the json files to the master server
 * 
 * @param callback
 */
function uploadFiles(client, callback) {
	_listUploadFiles(function(err, files) {
		logger.debug("files to upload: " + JSON.stringify(files));
		async.eachSeries(files, function(file, fn) {
			_uploadFile(client, file, fn);
		}, callback);
	});
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

function _uploadFile(client, file, callback) {
	function doUpload(fn) {
		_parseFile(file, function(err, data) {
			if (err) {
				return fn(err);
			}

			async.eachSeries(data, function(stateUpdate, fn) {
				client.post(upload_file_path, stateUpdate, function(err, req, res) {
					fn(err);
				})
			}, fn);
		})
	}

	function doRename(fn) {
		fs.rename(file, file + '.sent', callback)
	}

	async.series([ doUpload, doRename ], callback);
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

module.exports = {
	uploadDevices : uploadDevices,
	uploadFiles : uploadFiles
}