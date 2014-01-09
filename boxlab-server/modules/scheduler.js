var cronJob = require('cron').CronJob;

function schedule(cronPattern, onTick, callback) {
	var job = new cronJob({
		cronTime : cronPattern,
		onTick : onTick,
		start : false
	});
	job.start();

	callback();
}

module.exports.schedule = schedule;