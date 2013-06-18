var jsftp = require("jsftp");
var moment = require('moment');
var fs = require('fs');
var logger = log4js.getLogger("io");
var configuration = require('../configuration.js');
var ftpconnection = new jsftp(configuration.ftp);

function Ftp() {}

Ftp.prototype = {
    directory: './data',
    dateFormat: 'YYYY-MM-DD',

	 send: function () {
    	var self = this;
        fs.readdir(this.directory, function (err, files) {
            files.map(function (file) {
                if (file.substr(-4) != '.tgz') {
                    return;
                }

                self._sendFile(self.directory + '/' + file, function (err, status) {
                    if (!err) {
                        logger.info('succesfully sended file : ' + file);
                    } else {
                        logger.error(err);
                    }

                });
            });
        });
    },
    
    _sendFile: function (file, callback) {

        var inputstream = fs.createReadStream(file);
        inputstream.pause();

        ftpconnection.getPutSocket(file.substr(file.lastIndexOf('/') + 1), function (err, socket) {
            if (err) return callback(err);
            inputstream.pipe(socket); // Transfer from source to the remote file
            inputstream.resume();
            inputstream.on('error', function () {
                return callback('error');
            });
            inputstream.on('end', function () {

                fs.unlink(file, function (err) {
                    if (err) {
                        callback(err);
                    } else {
                        callback(null, {
                            status: 200
                        });
                    }
                });
            });
        });
    }
}
var f = new Ftp();
module.exports.ftp = f;