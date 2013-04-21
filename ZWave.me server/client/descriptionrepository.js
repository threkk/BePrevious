var fs = require('fs');
var path = require('path')
var logger = log4js.getLogger("client");
var xm = require('xml-mapping');

function DescriptionRepository() {
    this.unknownImageURL = 'images/unknown_device.png';
    this.directory = './ZDDX/';
    this.descriptors = [];

    var files = fs.readdirSync(this.directory);
    for (var index in files) {
    	var filename = files[index];
    	if (filename.substr(-4) != '.xml') {
            return;
        }

        this._parseDescription(filename);
    }
    
    
}

DescriptionRepository.prototype = {
    updateDeviceDescription: function (device) {
        var descriptor = this._findDescription(
            device.manufacturerId,
            device.manufacturerProductType,
            device.manufacturerProductId)

        if (!descriptor) {
            return;
        }

        var description = {
            name: descriptor.name,
            description: descriptor.description,
            imageURL: descriptor.imageURL
        }

        device.description = description;
    },

    _parseDescription: function (filename) {
        var xml = fs.readFileSync(path.join(this.directory, filename), 'utf8');
        var json = xm.load(xml);

        var deviceData = json.ZWaveDevice.deviceData;
        var deviceDescription = json.ZWaveDevice.deviceDescription;
        var resourceLinks = deviceData.resourceLinks;
        var descriptorManufacturerId = deviceData.manufacturerId && deviceData.manufacturerId.value;
        var descriptorProductType = deviceData.productType && deviceData.productType.value;
        var descriptorProductId = deviceData.productId && deviceData.productId.value;

        this.descriptors.push({
            manufacturerId: (parseInt(descriptorManufacturerId, 16) || -1),
            productType: (parseInt(descriptorProductType, 16) || -1),
            productId: (parseInt(descriptorProductId, 16) || -1),
            name: deviceDescription.productName['$t'],
            description: deviceDescription.description.lang['$t'],
            imageURL: ((resourceLinks && resourceLinks.deviceImage.url) || this.unknownImageURL)
        });
    },

    _findDescription: function (manufacturerId, productType, productId) {
        for (var key in this.descriptors) {
            var descriptor = this.descriptors[key];
            if ((descriptor.manufacturerId == manufacturerId) && (descriptor.productType == productType)) {
            	console.log('descriptor found');
                return descriptor;
            }
        }
    }
}

module.exports.DescriptionRepository = DescriptionRepository;