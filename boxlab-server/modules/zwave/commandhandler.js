var EventEmitter = require('events').EventEmitter;

var commandListener = require('./commandlistener').commandListener;
var deviceManager = require('./devicemanager').deviceManager;
var logger = require('../logging').getLogger('client');

var COMMAND_CLASS_SWITCH_BINARY = 37;
var COMMAND_CLASS_SENSOR_BINARY = 48;

function CommandHandler() {
    this.listen();
}

CommandHandler.prototype = {
    listen: function () {
    	var self = this;
        commandListener.on('command', function (commandUpdate) {
            var device = deviceManager.getDevice(commandUpdate.nodeId);
            if (!device) {
                return;
            }

            var state = self.handleCommand(commandUpdate);
            if (self.isObjectEmpty(state)) {
                return;
            }

            device.updateState(state, function (err, dirty, state) {
                if (dirty) {
                    self.emit('state_changed', {
                        nodeId: device.data.id,
                        timestamp: commandUpdate.timestamp,
                        state: state
                    });
                }
            });
        });
    },

    handleCommand: function (commandUpdate) {
        var command = commandUpdate.command;
        var state = {};
        if (command.id == COMMAND_CLASS_SENSOR_BINARY) {
            state.value = command.value ? 1 : 0;
        } else if (command.id == COMMAND_CLASS_SWITCH_BINARY) {
            state.value = (command.value == 255) ? 1 : 0;
        }

        return state;
    },

    isObjectEmpty: function (object) {
        var result = true;
        for (keys in object) {
            result = false;
            break;
        }
        return result;
    }
}

CommandHandler.prototype.__proto__ = EventEmitter.prototype;

module.exports.handler = new CommandHandler();