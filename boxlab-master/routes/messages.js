var moment = require('moment');

var messageService = require('../modules/service/messageservice');

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
	req.body.identification = req.params.identification;
	messageService.saveMessage(req.body, function(err) {
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