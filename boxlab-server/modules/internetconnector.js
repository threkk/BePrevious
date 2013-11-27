function InternetConnector() {

}

InternetConnector.prototype = {
	connect: function(callback) { 
	}, 
	
	disconnect: function(callback) {
	}
}

module.exports.connector = new InternetConnector();