var mongoose = require('mongoose');
var Schema = mongoose.Schema;

var ObjectId = Schema.Types.ObjectId;
var Mixed = Schema.Types.Mixed;

var deviceSchema = new Schema({
	Id : ObjectId,
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
	Id : ObjectId,
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

module.exports = {
	Device : mongoose.model('Device', deviceSchema),
	DeviceState : mongoose.model('DeviceState', deviceStateSchema)
};