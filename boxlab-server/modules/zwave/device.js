var _ = require('lodash');

function Device(manager) {
	this.manager = manager;

	this.data = {
		id : -1,
		name: null,
		basicType : -1,
		genericType : -1,
		specificType : -1,
		manufacturerId : -1,
		productId : -1,
		productType : -1,
		isListening : false,
		isFLiRS : false,
		hasWakeup : false,
		hasBattery : false,
		isFailed : false
	}
	
	this.state = {
		value: 0
	};
}

Device.prototype = {

	updateData: function(data, callback) {
		var clonedData = _.clone(this.data);
		var dirty = this._merge(clonedData, data);
		if (dirty) {
			this.data = clonedData;
		}
		callback(null, dirty, clonedData)
	},
	
	updateState: function(state, callback) {
		var clonedState = _.clone(this.state);
		var dirty = this._merge(clonedState, state);
		if (dirty) {
			this.state = clonedState;
		}
		callback(null, dirty, clonedState)
	},

	_merge : function(oldData, newData) {
		var dirty = false;
		for ( var property in newData) {
			var oldValue = oldData[property];
			var newValue = newData[property];

			if (!_.isEqual(oldValue, newValue)) {
				oldData[property] = newValue;
				dirty = true;
			}
		}
		return dirty
	}
};

module.exports.Device = Device;