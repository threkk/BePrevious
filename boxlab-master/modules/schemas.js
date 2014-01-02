var mongoose = require('mongoose');
var Schema = mongoose.Schema;

function saveUpdatePlugin(schema) {
	schema.add({
		updated : Number
	});
	schema.statics.saveUpdate = function(data, callback) {
		var self = this;
		self.findOne({
			_id : data._id
		}, function(err, result) {
			if (!result) {
				new self(data).save(callback);
			} else {
				var changed = false;
				for ( var index in data) {
					if (result[index] != data[index]) {
						result[index] = data[index];
						changed = true;
					}
				}

				if (changed) {
					result.save(callback);
				} else {
					callback();
				}
			}
		});
	};
}

function createdDatePlugin(schema) {
	schema.add({
		created : Number
	});

	schema.pre('save', function(next) {
		var millis = +new Date();
		if (!this.created) {
			this.created = millis;
		}
		this.updated = millis;
		next();
	});
}

function prepare(name, schema) {
	saveUpdatePlugin(schema);
	createdDatePlugin(schema);
	return mongoose.model(name, schema);
}

var deviceSchema = new Schema({
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
	kWh : Number,
	W : Number,
	temperature : Number,
	luminescence : Number,
	value : Number,
});

var patientSchema = new Schema({
	identification : {
		type : String,
		required : true
	},
	firstName : String,
	lastName : String
});

var exerciseEntrySchema = new Schema({
	identification : {
		type : String,
		required : true
	},
	date : Number,
	exerciseId : Number,
	done : Boolean,
	repetitions : []
});

var messageSchema = new Schema({
	identification : {
		type : String,
		required : true
	},

	message : String,
	fromPatient : Boolean,
	read : Boolean
});

module.exports = {
	Device : prepare('Device', deviceSchema),
	DeviceState : prepare('DeviceState', deviceStateSchema),
	Patient : prepare('Patient', patientSchema),
	ExerciseEntry : prepare('ExerciseEntry', exerciseEntrySchema),
	Message : prepare('Message', messageSchema)
};