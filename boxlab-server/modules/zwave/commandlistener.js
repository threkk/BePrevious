var _ = require('lodash');
var async = require('async');
var EventEmitter = require('events').EventEmitter;
var emitter = new EventEmitter();

var logger = require('../logging').getLogger('client');
var localDB = require('../localdatabase').localDB;
var client = require('./client');

var pathRegex = new RegExp(
		/^devices\.(\d+)\.instances\.(\d+)\.commandClasses\.(\d+)\.(.*)$/)

var updateDelay = 10000;
var updateRate = 500;

var commandCacheSize = 50;
var commandCache = [];

function CommandListener() {
	this.running = false;
	this.start();
}

CommandListener.prototype = {
	start : function() {
		this.running = true;
		var self = this;

		function test() {
			return self.running;
		}

		function iteration(callback) {
			var timestamp = localDB.getLastCommandUpdate();
			client.getApiData(timestamp, function(err, data) {
				if (err) {
					logger.error('Failed to retrieve data from api: ' + err.code);
				} else {
					self._update(data);
					timestamp = Math.round((+new Date() - updateDelay) / 1000);
					localDB.setLastCommandUpdate(timestamp);
				}
			});
			setTimeout(callback, updateRate);
		}

		function finish(err) {
			if (err) {
				logger.error('command listener encountered an error: ' + JSON.stringify(err));
			}

			logger.debug('command listener has stopped running');
		}

		async.whilst(test, iteration, finish);
	},

	stop : function() {
		this.running = false;
	},

	_update : function(data) {
		//console.log('data: '+JSON.stringify(data));
		for ( var key in data) {
			var match = pathRegex.exec(key);
			if (match) {
				var nodeId = match[1];
				var instanceId = match[2];
				var commandId = match[3];
				var json = data[key];

				this._handleCommandUpdate(nodeId, instanceId, commandId, json);
			}
		}
	},

	_handleCommandUpdate : function(nodeId, instanceId, commandId, json) {
		var timestamp = json.updateTime;
		var commandUpdate = {
			nodeId : nodeId,
			instanceId : instanceId,
			timestamp : timestamp,
			command : {
				id : commandId,
				name : json.name,
				value : json.value,
				type : json.type
			}
		}

		// check to see if this command has been handled
		// this is true if the command can be found in the cache
		var index = _.findIndex(commandCache, function(handledCommand) {
			return _.isEqual(handledCommand, commandUpdate);
		});

		if (index < 0) {
			commandCache.splice(0, 0, commandUpdate);
			if (commandCache.length > commandCacheSize) {
				commandCache.pop();
			}

			this.emit('command', this._normalizeCommandUpdate(commandUpdate));
		}
	}, 
	
	_normalizeCommandUpdate: function(commandUpdate) {
		var normalized = _.cloneDeep(commandUpdate);
		var command = normalized.command;
		
		if (command.type=='int') {
			command.value = parseInt(command.value, 10) || -1;
		}
		
		normalized.nodeId = parseInt(normalized.nodeId, 10) || -1;
		command.id = parseInt(command.id, 10) || -1;
				
		return normalized;
	}
}

CommandListener.prototype.__proto__ = EventEmitter.prototype;

module.exports.commandListener = new CommandListener();