var exec = require('child_process').exec;
var fs = require('fs');
var path = require('path');
var moment = require('moment');
var async = require('async');

var logger = require('../modules/logging').getLogger('io');

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
    }
}

module.exports.writer = new Writer();