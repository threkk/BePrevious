    var FTPClient = require('ftp');
    var fs = require('fs');


    function Ftp() {}

    Ftp.prototype = {

        sendFile: function (file, callback) {

            var c = new FTPClient();
            c.on('ready', function () {
                c.put(file, file, function (err) {
                    c.end();
                    if (err) {
                        callback(err)
                    } else {
                        callback(null, {
                            code: 200
                        });
                    }
                    c.end();
                });
            });
            // connect to localhost:21 as anonymous
            c.connect();
        }
    }