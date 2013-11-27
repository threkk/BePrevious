var log4js = require('log4js');
var path = require("path");

var defaultCategory = 'default';
var initialized = false;

function initializeLogger() {
	log4js.configure(path.join(__dirname, '../log4js.json'), {});
	initialized = true;
}

module.exports.getLogger = function(category) {
	if (!initialized) {
		initializeLogger();
	}

	if (!category) {
		category = defaultCategory;
	}

	return log4js.getLogger(category);
};