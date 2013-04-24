var _ = require('lodash');
var logger = log4js.getLogger("client");

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
        description: {}
    }
}


Device.prototype = {
    update: function (deviceData) {
        this._merge(this.data, deviceData);
    },

    _merge: function(oldData, newData) {
        var dirty = false;
        for (var property in newData) {
            var oldValue = oldData[property];
            var newValue = newData[property];

            if (!_.isEqual(oldValue, newValue)) {
                logger.debug("property: " + property + " changed");
                logger.debug("old: " + JSON.stringify(oldValue));
                logger.debug("new: " + JSON.stringify(newValue));

                oldData[property] = newValue;
                this.manager.client.emit('device_updated', {
                    oldValue: oldValue,
                    newValue: newValue
                });
            }
        }
        return dirty
    }
};


module.exports = {
    Device: Device
};