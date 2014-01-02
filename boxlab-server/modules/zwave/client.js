var restify = require('restify');

var configuration = require('../../configuration.js');
var apiDataPath = '/ZWaveAPI/Data/';
var apiCommandPath = '/ZWaveAPI/Run/';

var command_start_inclusion = 'controller.AddNodeToNetwork(1)';
var command_stop_inclusion = 'controller.AddNodeToNetwork(0)';
var command_start_exclusion = 'controller.RemoveNodeFromNetwork(1)';
var command_stop_exclusion = 'controller.RemoveNodeFromNetwork(0)';

var restClient = restify.createJsonClient({
	url : configuration.zwave_host,
	connectTimeout : configuration.pollingTimeout,
	version : '*'
});

function Client() {

}

Client.prototype = {
	runCommand : function(command, callback) {
		restClient.get(apiCommandPath + command, function(err, req, res, json) {
			callback && callback(err, json);
		});
	},

	getApiData : function(timestamp, callback) {
		restClient.get(apiDataPath + timestamp, function(err, req, res, json) {
			callback(err, json);
		});
	},

	startInclusionMode : function(callback) {
		this.runCommand(command_start_inclusion, callback);
	},

	stopInclusionMode : function(callback) {
		this.runCommand(command_stop_inclusion, callback);
	},

	startExclusionMode : function(callback) {
		this.runCommand(command_start_exclusion, callback);
	},

	stopExclusionMode : function(callback) {
		this.runCommand(command_stop_exclusion, callback);
	}
};

module.exports.client = new Client();