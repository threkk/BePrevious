var fs = require('fs');
var async = require('async');
var JSFtp = require("jsftp");

var logger = log4js.getLogger("io");
var localDB = require('./localdatabase.js').localDB;
var configuration = require('../configuration.js');

var localDirectory = '../data';
var remoteDirectory = '/remote_test';
var dateFormat = 'YYYY-MM-DD';

function Ftp() {
   
}

Ftp.prototype = {
    initDirectory: function (callback) {
        this.ftp.raw.mkd(remoteDirectory, function (err, data) {
            if (err) return callback(err);

            if (data.code != 257) {
                callback({
                    error: 'Failed to create directory "' + remoteDirectory + '"',
                    code: data.code,
                    response: data.text
                });
            } else {
                callback(null);
            }
        });
    },

    connect: function () {
    	logger.debug("connecting to ftp");
        this.ftp = new JSFtp({
            host: configuration.ftp.host,
            port: configuration.ftp.port,
            user: configuration.ftp.user,
            pass: configuration.ftp.pass
        });
    },

    disconnect: function () {
    	logger.debug("disconnecting from ftp");
        this.ftp && this.ftp.destroy();
    },

    send: function (callback) {
        var self = this;

        self.connect();
        self.initDirectory(function (err) {
            if (err) {
                self.disconnect();
                callback(err)
            } else {
                self._send(function (fn) {
                    self.disconnect();
                    callback(fn);
                });
            }
        });
    },

    _send: function (callback) {
        var self = this;
        self._listLocalFiles(function (err, localFiles) {
            if (err) {
                disconnect();
                return callback(err);
            }
            self._filterFiles(localFiles, function (err, result) {
                if (err) return callback(err);
                console.log('files i need to send: ' + JSON.stringify(result));

                var funcs = [];
                for (var index in result) {
                    funcs.push(function (fn) {
                        self._sendfile(result[index], fn);
                    });
                }

                async.series(funcs, callback);
            })
        });
    },

    _sendfile: function (file, callback) {
        console.log('sending file: ' + file + ' to remote ->' + remoteDirectory + '/' + file);
        this.ftp.put(localDirectory + '/' + file, remoteDirectory + '/' + file, function (err) {
            console.log('err: ' + err);
            callback(err);
        });
    },

    _filterFiles: function (localFiles, callback) {
        this._listRemoteFiles(function (err, remoteFiles) {
            if (err) return callback(err);

            function iterator(localFile, fn) {
                fn(remoteFiles.indexOf(localFile) < 0);
            }

            async.filter(localFiles, iterator, function (result) {
                callback(null, result);
            });
        });
    },

    _listRemoteFiles: function (callback) {
        this.ftp.ls(remoteDirectory, function (err, res) {
            if (err) return callback(err);
            var result = [];
            res.forEach(function (file) {
                if (file && file.name) {
                    result.push(file.name);
                }
            });
            callback(null, result);
        });
    },

    _listLocalFiles: function (callback) {
        var self = this;
        var result = [];
        fs.readdir(localDirectory, function (err, files) {
            if (err) return callback(err);

            for (var index in files) {
                var file = files[index];
                if (file.substr(-4) != '.tgz') {
                    //file is not compressed
                    return;
                }
                result.push(file);
            }

            callback(null, result);
        });
    }




}
var f = new Ftp();
//module.exports.ftp = f;