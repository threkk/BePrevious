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
	
    startInclusionMode: function (duration, callback) {
		console.log('start include');
		callback();
    },

    stopInclusionMode: function (callback) {
		console.log('stop include');
		callback();
    },

    startExclusionMode: function (duration, callback) {
		console.log('start exclude');
		callback();
    },

    stopExclusionMode: function (callback) {
       	console.log('stop exclude');
		callback();
    }
};

module.exports.client = new Client();