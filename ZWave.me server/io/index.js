var exec = require('child_process').exec;
var fs = require("fs");
var cronJob = require('cron').CronJob;
var moment = require('moment');
var job = new cronJob({
    cronTime: '00 24 14 * * 1-5',
    onTick: function () {
        
    }
});
var logger = log4js.getLogger("io");

function Writer() {}

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

        })
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

    compressFiles: function () {
        fs.readdir(this.directory, function (err, files) {

            files.map(function (file) {
                if (file.substr(-5) != '.json') {
                    return;
                }

                if (file.substr(0, file.length-5) == moment().format(this.dateFormat)) {
                    return;
                }
				
                this._compressFile(this.directory+'/'+file, function (err) {
                    if (!err) {
                        fs.unlink(this.directory+'/'+file, function (err) {
                            if (err) {
                                logger.error(err);
                            } else {
                            	logger.debug('deleted file ' + file);
                            }
                        }.bind(this));
                    } else {
                    	logger.error('failed to compress file' + JSON.stringify(err));
                    }
                }.bind(this));
            }.bind(this));
        }.bind(this));
    },

    _compressFile : function (filename, callback) {
    	logger.debug('compressing file '+filename);
    	var command = "tar -zcf " + filename.substr(0, filename.length-5) + ".tgz " + filename;
        var child = exec(command, function (error, stdout, stderr) {
            callback(error, {
                stdout: stdout,
                stderr: stderr
            });
        });
    }
}

var w = new Writer();

module.exports = w;
