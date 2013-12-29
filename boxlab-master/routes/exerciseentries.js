var moment = require('moment');

var exerciseEntryService = require('../modules/service/exerciseentryservice');

function parseDate(input, def) {
	var intValue = parseInt(input, 10);
	if (intValue) {
		input = intValue;
	}

	var parsedDate = moment(input);
	if (parsedDate.isValid()) {
		return parsedDate;
	} else {
		return def;
	}
}

function postEntry(req, res) {
	req.body.identification = req.params.identification;
	exerciseEntryService.saveEntry(req.body, function(err) {
		if (err) {
			res.send(500, err);
		} else {
			res.end();
		}
	});
}

function getEntries(req, res) {
	var query = {
		from : parseDate(req.query.from, moment().startOf('month')).valueOf(),
		to : parseDate(req.query.to, moment().endOf('month')).valueOf()
	};

	var identification = req.params.identification;
	exerciseEntryService.getEntries(identification, query, function(err, results) {
		if (err) {
			res.send(500, {});
		} else {
			res.send(results);
		}
	});
}

function deleteEntry(req, res) {
	exerciseEntryService.deleteEntry(req.params.id, function(err) {
		if (err) {
			res.send(500, err);
		} else {
			res.end();
		}
	});
}

module.exports.routes = {
	post : postEntry,
	get : getEntries,
	'/all' : {
		get : getEntries
	},
	'/:id' : {
		del : deleteEntry
	}
};