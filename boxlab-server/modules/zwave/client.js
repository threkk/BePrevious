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

function runCommand(command, callback) {
	restClient.get(apiCommandPath + command, function(err, req, res, json) {
		callback && callback(err, json);
	});
}

function getApiData(timestamp, callback) {
	restClient.get(apiDataPath + timestamp, function(err, req, res, json) {
		callback(err, json);
	});
}

function startInclusionMode(callback) {
	runCommand(command_start_inclusion, callback);
}

function stopInclusionMode(callback) {
	runCommand(command_stop_inclusion, callback);
}

function startExclusionMode(callback) {
	runCommand(command_start_exclusion, callback);
}

function stopExclusionMode(callback) {
	runCommand(command_stop_exclusion, callback);
}

module.exports = {
	runCommand : runCommand,
	getApiData : getApiData,

	startInclusionMode : startInclusionMode,
	stopInclusionMode : stopInclusionMode,

	startExclusionMode : startExclusionMode,
	stopExclusionMode : stopExclusionMode
}