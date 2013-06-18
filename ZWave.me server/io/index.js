var exec = require('child_process').exec;
var fs = require('fs');
var path = require('path');
var cronJob = require('cron').CronJob;
var moment = require('moment');
var async = require('async');
var logger = log4js.getLogger("io");

function Writer() {
    this.init();
}

Writer.prototype = {
    directory: './data',
    dateFormat: 'YYYY-MM-DD',

    init: function () {
        fs.mkdir(this.directory, 0777, function (err) {
            if (!err) {
                logger.debug("created data directory");
            } else {
                if (err && err.code !== 'EEXIST') {
                    logger.error(err);
                    throw new Error('Failed to create data directory');
                }
            }
        });
    },

    write: function (json, callback) {
        var filename = moment().format(this.dateFormat) + ".json";
        fs.appendFile(this.directory + '/' + filename, JSON.stringify(json) + '\r\n', function (err) {
            if (err) {
                callback(err);
            } else {
                callback(null, {
                    code: 200
                });
            }
        });
    },

    compressFiles: function (fn) {
    	var self = this;
        fs.readdir(this.directory, function (err, files) {
            if (err) {
                //failed to list files
                return fn(err);
            }

            var funcs = [];
            files.forEach(function (filename) {
                if (filename.substr(-5) != '.json') {
                    //file does not need compression, its not a json file
                    return;
                }

                if (filename.substr(0, filename.length - 5) == moment().format(this.dateFormat)) {
                    //file does not need compression, file is for today
                    return;
                }

                funcs.push(function (callback) {
                    self._compressFile(filename, function (err) {
                        if (err) {
                            logger.error('failed to compress file: ' + JSON.stringify(err));
                            return callback(err);
                        }

                        callback(null);
                    });
                });
            });
            
            async.series(funcs, function(err, results) {
			    fn && fn(err);
			});
        });
    },

    _compressFile: function (filename, callback) {    
        var dst = filename.substr(0, filename.length - 5) + '.tgz';
        var command = "cd ./data && tar -zcf " + dst + " " + filename + " --remove-files";

        var child = exec(command, function (error, stdout, stderr) {
        	if (error) {
        		error = {
        			errorMessage: error, 
        			command: command
        		};
        	}
            callback(error, {
                stdout: stdout,
                stderr: stderr
            });
        });
    },

    _deleteFile: function (filename, callback) {
        var location = path.join(this.directory, filename);
        fs.unlink(location, function (err) {
            if (err) {
                return callback(err);
            }

            callback(null, filename + ' was deleted');
        });
    }
}


var w = new Writer();
module.exports = {
    writer: w,
    ftp: require('./ftp.js').ftp,
    localDB: require('./localdatabase.js').localDB
}