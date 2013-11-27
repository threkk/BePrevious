var cronJob = require('cron').CronJob;

function Scheduler() {

}

Scheduler.prototype = {
    schedule: function (cronPattern, onTick, callback) {
        try {
            var job = new cronJob({
                cronTime: cronPattern,
                onTick: onTick,
                start: false
            });
            job.start();
            callback(null, job);
        } catch (ex) {
            callback(ex);
        }
    }
}

module.exports.scheduler = new Scheduler();