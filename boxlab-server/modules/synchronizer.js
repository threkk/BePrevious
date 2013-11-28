var async = require('async');

function Synchronizer() {

}

Synchronizer.prototype = {
	synchronize : function(callback) {
		async.series(downloadFiles, uploadFiles, callback);
	},

	downloadFiles : function(callback) {
		callback();
	},

	uploadFiles : function(callback) {
		this._listUploadFiles(function(err, files) {
			if (err) {
				return callback(err);
			}
		});
	},

	_listUploadFiles : function(callback) {
		var files = [];
		fs.readdir(this.directory, function(err, files) {

			if (err) {
				// failed to list files
				return callback(err);
			} else {
				var result = [];
				for ( var index in files) {
					var filename = files[index];
					if (filename.length > 9 && filename.substr(-9) == '.uploaded') {
						continue;
					}
					result.push(files[index]);
				}
				callback(null, result);
			}
		});
	}
}

module.exports.synchronizer = new Synchronizer();