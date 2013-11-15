var exec = require('child_process').exec;
var fs = require('fs');
var path = require('path');
var moment = require('moment');
var async = require('async');
var logger = log4js.getLogger("io");

function Compresser() {

}

Compresser.prototype = {
	directory : './data',
	dateFormat : 'YYYY-MM-DD',

	compressFiles : function(fn) {
		var self = this;
		fs.readdir(this.directory, function(err, files) {
			if (err) {
				// failed to list files
				return fn(err);
			}

			var funcs = [];
			files.forEach(function(filename) {
				if (filename.substr(-5) != '.json') {
					// file does not need compression, its not a json file
					return;
				}

				if (filename.substr(0, filename.length - 5) == moment().format(
						this.dateFormat)) {
					// file does not need compression, file is for today
					return;
				}

				funcs.push(function(callback) {
					self._compressFile(filename, function(err) {
						if (err) {
							logger.error('failed to compress file: '
									+ JSON.stringify(err));
							return callback(err);
						}

						callback(null);
					});
				});
			});

			async.series(funcs, function(err, results) {
				logger.debug("finished compressing files");
				fn && fn(err);
			});
		});
	},

	_compressFile : function(filename, callback) {
		logger.debug("compressing " + filename);
		var dst = filename.substr(0, filename.length - 5) + '.tgz';
		var command = "cd ./data && tar -zcf " + dst + " " + filename
				+ " --remove-files";

		var child = exec(command, function(error, stdout, stderr) {
			if (error) {
				error = {
					errorMessage : error,
					command : command
				};
			}
			callback(error, {
				stdout : stdout,
				stderr : stderr
			});
		});
	}
}

module.exports.compresser = new Compresser();