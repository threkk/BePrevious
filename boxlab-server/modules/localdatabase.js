var fs = require("fs");
var _ = require('lodash');
var EventEmitter = require('events').EventEmitter;
var emitter = new EventEmitter();

var paths = require('./paths');
var logger = require('../modules/logging').getLogger('io');

function LocalDatabase() {
	this.sourcefile = paths.relative(paths.resource, 'localdatabase.json');
	this.encoding = 'utf8';
	this.data = {};

	var self = this;
	this._load(function(err) {
		if (err) {
			return logger.error("failed to load local database: " + JSON.stringify(err));
		}

		logger.debug('local database loaded');
	});
}

LocalDatabase.prototype = {
	setLastCommandUpdate : function(lastCommandUpdate) {
		this.update({
			lastCommandUpdate : lastCommandUpdate
		});
	},

	getLastCommandUpdate : function() {
		return this.data.lastCommandUpdate || 0;
	},

	setIdentity : function(identity) {
		if (this.getIdentity()) {
			throw new Error('Identity is allready set');
		}
		this.update({
			identity : identity
		});
	},

	getIdentity : function(identity) {
		return this.data.identity;
	},

	getName : function(nodeid) {
		var result = null;
		var names = this.data.names || [];
		for ( var key in names) {
			var name = names[key];
			if (name.nodeid == nodeid) {
				result = name.value;
			}
		}
		return result;
	},

	setName : function(nodeid, name) {
		var names = this.data.names || [];
		var index = _.findIndex(names, function(name) {
			return name.nodeid == nodeid;
		});

		offsets = offsets.slice(0);

		if (index < 0) {
			offsets.push({
				nodeid : nodeid,
				value : name
			});
		} else {
			names[index].value=name;
		}

		this.update({
			names : names
		});
	},

	getTempOffset : function(nodeid) {
		var result = 0;
		var offsets = this.data.temperatureOffsets || [];
		for ( var key in offsets) {
			var offset = offsets[key];
			if (offset.nodeid == nodeid) {
				result = offset.offsetValue;
			}
		}
		return result;
	},

	setTempOffset : function(nodeid, offsetValue) {
		var offsets = this.data.temperatureOffsets || [];
		var index = _.findIndex(offsets, function(offset) {
			return offset.nodeid == nodeid;
		});

		offsets = offsets.slice(0);

		var newOffset = {
			nodeid : nodeid,
			offsetValue : offsetValue
		};

		if (index < 0) {
			offsets.push(newOffset);
		} else {
			var oldOffset = parseInt(offsets[index].offsetValue, 10) || 0;
			newOffset.offsetValue += oldOffset;
			offsets[index] = newOffset;
		}

		this.update({
			temperatureOffsets : offsets
		});
	},

	update : function(data) {
		var dirty = this._merge(this.data, data);
		if (dirty) {
			this._save(function(err) {
				if (err) {
					logger.debug("failed to persist local database: " + JSON.stringify(err));
				}
			});
		}
	},

	_load : function(callback) {
		var data;
		try {
			data = fs.readFileSync(this.sourcefile, this.encoding);
		} catch (e) {
			if (e.code === 'ENOENT') {
				logger.debug('no json data can be found (database empty)');
				return callback(null);
			} else {
				logger.error('failed to read from database: ' + JSON.stringify(e));
				return callback(e);
			}
		}

		data = data.toString();
		if (data.length == 0) {
			logger.debug('loaded an empty local json database');
		} else {
			this.data = JSON.parse(data.toString());
		}
		callback(null, this.data);
	},

	_save : function(callback) {
		fs.writeFile(this.sourcefile, JSON.stringify(this.data), this.encoding, function(err) {
			if (err) {
				return callback(err);
			} else {
				return callback(null);
			}
		});
	},

	_merge : function(oldData, newData) {
		var dirty = false;
		for ( var property in newData) {
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

LocalDatabase.prototype.__proto__ = EventEmitter.prototype;

module.exports.localDB = new LocalDatabase();