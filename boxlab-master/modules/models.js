var mongoose = require('mongoose');
var Schema = mongoose.Schema;

var ObjectId = Schema.Types.ObjectId;
var Mixed = Schema.Types.Mixed;

var deviceSchema = new Schema({
	id : ObjectId,
	identification : {
		type : String,
		required : 'Boxlab identity needs to be provided!'
	},
	name : String,
	nodeId : Number,
	basicType : Number,
	genericType : Number,
	specificType : Number,
	manufacturerId : Number,
	productId : Number,
	productType : Number,
	isListening : Boolean,
	isFLiRS : Boolean,
	hasWakeup : Boolean,
	hasBattery : Boolean,
	isFailed : Boolean
});

var deviceStateSchema = new Schema({
	id : ObjectId,
	identification : {
		type : String,
		required : true
	},
	nodeId : {
		type : Number,
		required : true
	},
	timestamp : {
		type : Number,
		required : true
	},
	power : Number,
	usage : Number,
	temperature : Number,
	luminescence : Number,
	value : Number,
});

var patientSchema = new Schema({
	id : ObjectId,
	identification : {
		type : String,
		required : true
	},
	firstName : String,
	lastName : String
});

function merge(source, target) {
	for ( var index in source) {
		if (target[index] != source[index]) {
			target[index] = source[index];
		}
	}
}

function prepare(name, schema) {
	schema.statics.saveUpdate = function(data, callback) {
		var self = this;
		self.findOne({
			_id : data._id
		}, function(err, result) {
			if (!result) {
				console.log('save: ' + JSON.stringify(data));
				new self(data).save(callback);
			} else {
				console.log('merge: ' + JSON.stringify(data));
				merge(data, result);
				result.save(callback);
			}
		});
	};
	return mongoose.model(name, schema);
}

module.exports = {
	Device : mongoose.model('Device', deviceSchema),
	DeviceState : mongoose.model('DeviceState', deviceStateSchema),
	Patient : prepare('Patient', patientSchema),
};