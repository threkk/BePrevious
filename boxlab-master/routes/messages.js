var moment = require('moment');

var messageService = require('../modules/messageservice').service;

function parseDate(input) {
	var intValue = parseInt(input, 10);
	if (intValue) {
		input = intValue;
	}

	var parsedDate = moment(input);
	if (parsedDate.isValid()) {
		return parsedDate;
	} else {
		return null;
	}
}

function postMessage(req, res) {
	var messageData = {
		identification : req.params.identification,
		nodeId : req.body.nodeId,
		timestamp : req.body.timestamp,
		power : req.body.state.power,
		usage : req.body.state.usage,
		temperature : req.body.state.temperature,
		luminescence : req.body.state.luminescence,
		value : req.body.state.luminescence,
	};

	messageService.saveMessage(messageData, function(err) {
		if (err) {
			res.send(500, err);
		} else {
			res.end();
		}
	});
}

function getMessages(req, res) {
	var query = {
		from : (parseDate(req.query.from) || moment().startOf('month')).valueOf(),
		to : (parseDate(req.query.to) || moment().endOf('month')).valueOf()
	};

	var identification = req.params.identification;
	messageService.getMessages(identification, query, function handleResult(err, results) {
		if (err) {
			res.send(500, {});
		} else {
			res.send(results);
		}
	});
}

module.exports.routes = {
	post : postMessage,
	get : getMessages
};