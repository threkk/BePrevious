var Hashids = require("hashids");

var logger = require('../modules/logging').getLogger('io');
var localDB = require('../modules/localdatabase').localDB;

var secret = 'boxlab-best-lab';

module.exports.getIdentity = function() {
	var identity = localDB.getIdentity();

	if (!identity) {
		identity = new Hashids(secret, 36).encrypt(new Date().getTime());
		localDB.setIdentity(identity);
		logger.debug('new boxlab identity generated: ' + identity);
	}

	return identity;
}