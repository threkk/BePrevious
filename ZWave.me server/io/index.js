var exec = require('child_process').exec;
var fs = require("fs");
var cronJob = require('cron').CronJob;
var moment = require('moment');
var job = new cronJob({
    cronTime: '00 24 14 * * 1-5',
    onTick: function () {
        createTar("input.json");
    }
});
startJob();

function startJob() {
	console.log(returnDate());
    job.start();
}


function createTar(filename) {
    var child = exec("tar -zcf "  + returnDate() + ".tgz " + filename, function (error, stdout, stderr) {
        console.log('execute');
        console.log(stdout);
        console.log(stderr);
        if (!error) {
            emptyFile();
        }
    });
}

function returnDate() {
    var now = moment().format("YYYY-MM-DD");
    return now;
}

function emptyFile() {
    fs.writeFile('input.txt', '', function (err) {
        if (err) console.log(err);
    });
}

function appendToFile(text) {

   var stream = fs.createWriteStream("input.txt" , {
   		'flags': 'a'
   });
	stream.once('open', function(fd) {
  	stream.write(text);
  	stream.write("\r\n");
  	stream.end();
});
}