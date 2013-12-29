var path = require('path');
var logger = require('./logging').getLogger();

var baseDir = __dirname;

function resolvePaths() {
	var paths = {
		data_in : path.resolve(baseDir, '../data/in'),
		data_out : path.resolve(baseDir, '../data/out'),
		resources : path.resolve(baseDir, '../resources')
	};

	for ( var key in paths) {
		logger.debug('path ' + key + ' = ' + paths[key])
	}

	return paths;
}

function relativePath(base, relative) {
	return path.resolve(base, relative)
}

module.exports = resolvePaths();
module.exports.relative = relativePath;