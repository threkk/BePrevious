var log4js = require('log4js');
var paths = require('./paths');

var defaultCategory = 'default';

module.exports.configure = function() {
	log4js.configure(paths.relative(paths.resources, 'log4js.json'), {});
}

module.exports.getLogger = function(category) {
	if (!category) {
		category = defaultCategory;
	}

	return log4js.getLogger(category);
};