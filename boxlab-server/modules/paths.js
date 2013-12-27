var path = require('path');
var logger = require('../modules/logging').getLogger();

function resolvePaths() {
	var paths = {
		data_in : path.resolve('../data/in'),
		data_out : path.resolve('../data/out'),
		resources: path.resolve('../resources')
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