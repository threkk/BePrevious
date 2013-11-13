var exec = require('child_process').exec;
var fs = require('fs');
var path = require('path');
var cronJob = require('cron').CronJob;
var moment = require('moment');
var async = require('async');
var logger = log4js.getLogger("io");

function Writer() {

}

Writer.prototype = {
    directory: './data',
    dateFormat: 'YYYY-MM-DD',

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


var w = new Writer();
module.exports = {
    writer: w,
    compresser: require('./compresser.js'),
    ftp: require('./ftp.js').ftp,
    localDB: require('./localdatabase.js').localDB
}