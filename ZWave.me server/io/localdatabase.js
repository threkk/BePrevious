var logger = log4js.getLogger("io");
var fs = require("fs");
var _ = require('lodash');

function LocalDatabase() {
    this.sourcefile = './localdatabase.json';
    this.encoding = 'utf8';
    this.data = {};

    var self = this;
    this.load(function (err) {
        if (err) {
            return logger.error("failed to load local database: " + JSON.stringify(err));
        }

        logger.debug('local database loaded');
    });
}

LocalDatabase.prototype = {
    getTempOffset: function (nodeid) {
        var result = 0;
        var offsets = this.data.temperatureOffsets || [];
        for (var key in offsets) {
            var offset = offsets[key];
            if (offset.nodeid == nodeid) {
                result = offset.offsetValue;
            }
        }
        return result;
    },

    setTempOffset: function (nodeid, offsetValue) {
        var offsets = this.data.temperatureOffsets || [];
        var index = _.findIndex(offsets, function (offset) {
            return offset.nodeid == nodeid;
        });

        offsets = offsets.slice(0);
		
		var newOffset = {
	        nodeid: nodeid,
	        offsetValue: offsetValue
        };
        
        if (index < 0) {
            offsets.push(newOffset);
        } else {
        	var oldOffset = parseInt(offsets[index].offsetValue,10) || 0;
        	newOffset.offsetValue += oldOffset;
            offsets[index] = newOffset;
        }

        this.update({
            temperatureOffsets: offsets
        });
    },

    update: function (data) {
    	var dirty = this._merge(this.data, data);
        if (dirty) {
            this.save(function (err) {
                if (err) {
                    logger.debug("failed to persist local database: " + JSON.stringify(err));
                } else {
                    logger.debug("succesfully persisted local database");
                }
            });
        }
    },

    load: function (callback) {
        var self = this;
        fs.readFile(this.sourcefile, this.encoding, function (err, data) {
            if (err) {
                return callback(err);
            }

            self.data = JSON.parse(data.toString());
            callback(null, self.data);
        });
    },

    save: function (callback) {
        fs.writeFile(this.sourcefile, JSON.stringify(this.data), this.encoding, function (err) {
            if (err) {
                return callback(err);
            } else {
                return callback(null);
            }
        });
    },

    _merge: function (oldData, newData) {
        var dirty = false;
        for (var property in newData) {
            var oldValue = oldData[property];
            var newValue = newData[property];

            if (!_.isEqual(oldValue, newValue)) {
                oldData[property] = newValue;
                dirty = true;
            }
        }

        return dirty
    }
}

var localDB = new LocalDatabase();

module.exports.localDB = localDB;