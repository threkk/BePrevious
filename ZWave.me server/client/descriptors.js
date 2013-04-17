var fs = require('fs');
var path = require('path')
var logger = log4js.getLogger("client");
var xm = require('xml-mapping');

var directory = './ZDDX/';
var descriptors = [];

function parseDescriptors() {
    console.log('aids');
    fs.readdir(directory, function (err, files) {
        if (err) {
            logger.error('failed to traverse descriptor directory: ' + JSON.stringify(err));
            return;
        } else {
            files.forEach(function (filename) {
                if (filename.substr(-4) != '.xml') {
                    return;
                }


                parseFile(filename);
            });
        }
    });
}

function parseFile(filename) {
    var absolutePath = path.join(directory, filename);
    var encoding = 'utf8';
    var xml = fs.readFileSync(absolutePath, encoding);

    var json = xm.load(xml);

    descriptors.push(json);
}

function getDescription(manufacturerId, productType, productId) {
    
    manufacturerId = lpad(manufacturerId.toString(16), "0", 4);
    productType = lpad(productType.toString(16), "0", 4);
    productId = lpad(productId.toString(16), "0", 4);


    for (var key in descriptors) {
        var descriptor = descriptors[key];
        if ((descriptor.ZWaveDevice.deviceData.manufacturerId == manufacturerId) 
            	&& (descriptor.ZWaveDevice.deviceData.productType == productType) 
            	&& (descriptor.ZWaveDevice.deviceData.productId == productId)) {
            return descriptor;
        }
    }
}

function lpad(str, padString, length) {
    while (str.length < length)
        str = padString + str;
    return str;
}

parseDescriptors();

module.exports.getDescription = getDescription;