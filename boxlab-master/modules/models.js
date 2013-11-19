var mongoose = require('mongoose');
var Schema = mongoose.Schema;

var ObjectId = Schema.Types.ObjectId;
var Mixed = Schema.Types.Mixed;

var deviceSchema = new Schema({
	Id : ObjectId,
	identifier : String,
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
	identifier : String,
	nodeId : Number,
	timestamp : Number,
	data : Mixed
});

module.exports = {
	Device : mongoose.model('Device', deviceSchema),
	DeviceState : mongoose.model('DeviceState', deviceStateSchema)
};