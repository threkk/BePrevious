var Hashids = require("hashids");

var logger = require('../modules/logging').getLogger('io');
var localDB = require('../modules/localdatabase').localDB;

module.exports.getIdentity = function() {
	var identity = localDB.getIdentity();
	if (!identity) {
		var hashids = new Hashids('boxlab-best-lab', 36);
		identity = hashids.encrypt(new Date().getTime());
		localDB.setIdentity(identity);
		logger.debug('new boxlab identity generated: ' + identity);
	}

	return identity;
}