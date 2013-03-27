var exec = require('child_process').exec;

var cronJob = require('cron').CronJob;

var job = new cronJob({
    cronTime: '*/5 * * * * *',
    onTick: function () {
    	createTar('input.txt');
    	
    }
});
startJob();

function startJob() {
	job.start();
}

function endJob() {
	job.stop();
}


function createTar(filename) {
    var child = exec("tar -zcf test.tgz " + filename, function (error, stdout, stderr) {
        console.log('execute');
        console.log(stdout);
        console.log(stderr);
        if (error) {
            console.log('exec error: ' + error);
        }
    });
}
