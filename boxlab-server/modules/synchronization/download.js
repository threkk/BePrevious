var fs = require('fs');
var async = require('async');

var logger = require('../logging').getLogger('io');
var identification = require('../identification');
var paths = require('../paths');
var localDB = require('../localdatabase').localDB;

var messages_file = paths.relative(paths.data_in, 'messages.json');
var entries_file = paths.relative(paths.data_in, 'entries.json');

/**
 * downloads the messages from the master server
 * 
 * @param callback
 */
function downloadMessages(client, callback) {
	var query = {
		from : localDB.getLastSyncDate()
	};

	var path = '/boxlab/api/' + identification.getIdentity() + '/messages';
	var query = '?from=' + localDB.getLastSyncDate();
	client.get(path + query, function(err, req, res, data) {
		if (err) {
			return callback(err);
		}

		if (data.length > 0) {
			logger.info('%d messages were downloaded', data.length);
		} else {
			logger.info('no new messages were found');
		}

		_appendFile(messages_file, data, callback);
	});
}

/**
 * downloads the entries from the master server
 * 
 * @param callback
 */
function downloadEntries(client, callback) {
	var query = {
		from : localDB.getLastSyncDate()
	};

	var path = '/boxlab/api/' + identification.getIdentity() + '/entries';
	var query = '?from=' + localDB.getLastSyncDate();
	client.get(path + query, function(err, req, res, data) {
		if (err) {
			return callback(err);
		}
		if (data.length > 0) {
			logger.info('%d exercise entries were downloaded', data.length);
		} else {
			logger.info('no new exercise entries were downloaded');
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

module.exports = {
	downloadMessages : downloadMessages,
	downloadEntries : downloadEntries
};