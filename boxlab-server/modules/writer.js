var fs = require('fs');
var moment = require('moment');

var paths = require('../modules/paths');
var logger = require('../modules/logging').getLogger('io');

function Writer() {

}

Writer.prototype = {
    write: function (json, callback) {
        var filename = moment().format('YYYY-MM-DD') + ".json";
        var path = paths.relative(paths.data_out, filename);
        var data = JSON.stringify(json) + '\r\n';
        
        fs.appendFile(path, data, function (err) {
            if (err) {
                return callback(err);
            }
            
            callback(null, {
                code: 200
            });
        });
    }
}

module.exports.writer = new Writer();