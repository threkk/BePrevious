var logger = log4js.getLogger("client");
var _ = require('lodash');
var localDB = require('../io');

function Device(manager) {
    this.manager = manager;

    this.data = {
        id: -1,
        basicType: -1,
        genericType: -1,
        specificType: -1,
        manufacturerId: -1,
        productId: -1,
        productType: -1,
        isListening: false,
        isFLiRS: false,
        hasWakeup: false,
        hasBattery: false,
        isFailed: false,
        multiLevel: {},
        description: {}
    }
}

Device.prototype = {
    update: function (deviceData) {
        this._merge(this.data, deviceData);
        this.updateMultiLevel();
    },

    _merge: function (oldData, newData) {
        var dirty = false;
        for (var property in newData) {
            var oldValue = oldData[property];
            var newValue = newData[property];

            if (!_.isEqual(oldValue, newValue)) {
            	dirty = true;
                oldData[property] = newValue;
                this.manager.client.emit('device_updated', {
                    oldValue: oldValue,
                    newValue: newValue
                });
            }
        }
        return dirty
    },

    updateMultiLevel: function () {
    	var nodeid = this.data.id;
        var command = 'devices[' + nodeid + '].instances[0].commandClasses[49]';
		var self = this;
        
        this.manager.client.runCommand(command, function (err, json) {
            if (err) {
                return logger.error(err);
            }
            if (!json) {
                return;
            }

            var data = json.data;

            var multilevel = {};
            for (var key in json.data) {
                var instance = json.data[key];
                if (instance && instance.sensorTypeString) {
                    var sensorType = instance.sensorTypeString.value;
                    var value = instance.val.value;
                    
                    // adjust the read value with the stored temperature offset
                    if (sensorType == 'Temperature') {
                    	value = value + localDB.getTempOffset(nodeid);
                    }
                    
                    multilevel[sensorType] = instance.val.value;
                }
            
            }
            self._merge(self.data, {multilevel:multilevel});
        });
    }
};


module.exports = {
    Device: Device
};