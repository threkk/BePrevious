var moment = require('moment');

var messageService = require('../modules/service/messageservice');

function parseDate(input) {
	if (!input) {
		return null;
	} else {
		var intValue = parseInt(input, 10);
		if (intValue) {
			input = intValue;
		}
	}

	var parsed = moment(input);
	if (parsed.isValid()) {
		return parsed.valueOf();
	} else {
		return null;
	}
}

function postMessage(req, res) {
	var message = req.body;
	message.identification = req.params.identification;
	messageService.saveMessage(message, function(err) {
		if (err) {
			res.send(500, err);
		} else {
			res.end();
		}
	});
}

function getMessages(req, res) {
	messageService.getMessages(req.params.identification, {
		from : parseDate(req.query.from),
		to : parseDate(req.query.to)
	}, function handleResult(err, results) {
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