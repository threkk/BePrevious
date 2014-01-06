var moment = require('moment');

var exerciseEntryService = require('../modules/service/exerciseentryservice');

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

function postEntry(req, res) {
	var entry = req.body;
	entry.identification = req.params.identification;
	exerciseEntryService.saveEntry(entry, function(err) {
		if (err) {
			res.send(500, err);
		} else {
			res.end();
		}
	});
}

function getEntries(req, res) {
	exerciseEntryService.getEntries(req.params.identification, {
		from : parseDate(req.query.from),
		to : parseDate(req.query.to)
	}, function(err, results) {
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