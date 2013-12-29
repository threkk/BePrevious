var log4js = require('log4js');
var path = require('path');

var defaultCategory = 'default';

module.exports.configure = function() {
	log4js.configure(path.resolve('../resources', 'log4js.json'), {});
}

module.exports.getLogger = function(category) {
	if (!category) {
		category = defaultCategory;
	}

	return log4js.getLogger(category);
};