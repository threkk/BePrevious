var logger = log4js.getLogger("client");
var _ = require('lodash');
var localDB = require('../io').localDB;

var updateDelay = 5;
var updateRate = 500;
var commandQueueSize = 25;

function CommandManager(client) {
    this.client = client;
    this.updates = [];
    this.init();
}

CommandManager.prototype = {
    pathRegex: new RegExp(/^devices\.(\d+)\.instances\.(\d+)\.commandClasses\.(\d+)\.(.*)$/),

    init: function () {
        var self = this;
        var timestamp = localDB.getLastCommandUpdate();
        this.timer = setInterval(function () {
            self.client.getApiData(timestamp, function (err, data) {
                if (err) {
                    return logger.error('failed to retrieve data from api: ' + JSON.stringify(err));
                }
                self.update(data);
	        	timestamp = Math.round(+new Date()/1000) - updateDelay;
                localDB.setLastCommandUpdate(timestamp);
            })
        }, updateRate);
    },

    update: function (data) {
        for (var key in data) {
            var match = this.pathRegex.exec(key);
            if (match) {
                var nodeId = match[1];
                var instanceId = match[2];
                var commandId = match[3];
                var json = data[key];

                this._handleCommandUpdate(nodeId, instanceId, commandId, json);
            }
        }
    }, 
    
    _handleCommandUpdate: function (nodeId, instanceId, commandId, json) {
        var timestamp = json.updateTime;
        var commandUpdate = {
            nodeId: nodeId,
            instanceId: instanceId,
            timestamp: timestamp,
            command: {
                id: commandId,
                name: json.name,
                value: json.value,
                type: json.type
            }
        }

		
        var index = _.findIndex(this.updates, function(handledCommand) {
    		return _.isEqual(handledCommand, commandUpdate);
    	});
    	
    	if (index < 0) {	        this.updates.splice(0, 0, commandUpdate);
	        if (this.updates.length>50) {
	       		this.updates.pop();
	        };
	        
	        this.client.emit('commandupdate', commandUpdate);
        }
    }
}

module.exports.CommandManager = CommandManager;