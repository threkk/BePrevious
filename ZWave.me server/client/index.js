var EventEmitter = require('events').EventEmitter;
var emitter = new EventEmitter();
var configuration = require('../configuration.js');
var restify = require('restify');
var logger = log4js.getLogger("client");

function Client(restClient) {
    this.restClient = restClient;
}

Client.prototype = {
    updateRate: 500,
    updateTime: 0,
    devices: [],

    init: function () {
        console.log('using client: ' + configuration.host);
        setTimeout(function () {
            client.update();
        }, this.updateRate);
    },

    update: function () {
        this.restClient.get('/ZWaveAPI/Data/' + this.updateTime, function (err, req, res, json) {
            if (err) {
                logger.error('client failed to retrieve data');
            } else {
                this.handleUpdate(json);
            }
            this.updateTime = Math.round((new Date()).getTime() / 1000);
            setTimeout(function () {
                client.update();
            }, this.updateRate);
        }.bind(this));
    },

    handleUpdate: function (data) {
        var devices = data.devices;
        console.log(JSON.stringify(data));
        if (devices) {
            for (var nodeId in devices) {
                var device = devices[nodeId];
                this.devices.push({
                    id: nodeId,
                    basicType: device.data.basicType.value,
                    genericType: device.data.genericType.value,
                    specificType: device.data.specificType.value,
                    isListening: device.data.isListening.value,
                    isFLiRS: !device.data.isListening.value && 
                    	(device.data.sensor250.value || device.data.sensor1000.value),
                    hasWakeup: 0x84 in device.instances[0].commandClasses,
                    hasBattery: 0x80 in device.instances[0].commandClasses
                });
            }

            this.devices.map(function (device) {
                logger.debug(JSON.stringify(device));
            });
        }
    }
};

var restClient = restify.createJsonClient({
    url: configuration.host,
    version: '*'
});

var client = new Client(restClient);
client.init();

module.exports = emitter;