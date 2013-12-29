function InternetConnector() {

}

InternetConnector.prototype = {
	connect: function(callback) {
		callback();
	}, 
	
	disconnect: function(callback) {
		callback();
	}
}

module.exports.connector = new InternetConnector();