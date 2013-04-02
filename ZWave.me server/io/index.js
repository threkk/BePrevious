var exec = require('child_process').exec;
var fs = require("fs");
var cronJob = require('cron').CronJob;
var moment = require('moment');
var job = new cronJob({
    cronTime: '00 02 12 * * 1-5',
    onTick: function () {
        createTar('input.txt');
    }
});
startJob();

function startJob() {
    job.start();
}


function createTar(filename) {
    var child = exec('tar -zcf ' + returnDate() + '.tgz ' + filename, function (error, stdout, stderr) {
        console.log('execute');
        console.log(stdout);
        console.log(stderr);
        if (!error) {
            emptyFile();
        }
    });
}

function returnDate() {
    var now = moment().format('ll');
    return now;
}

function emptyFile() {
    fs.writeFile('input.txt', '', function (err) {
        if (err) console.log(err);
    });
}

function writeToFile(text) {
    var log = fs.createWriteStream('input.txt', {
        'flags': 'a'
    });
    log.end("this is a message");
}