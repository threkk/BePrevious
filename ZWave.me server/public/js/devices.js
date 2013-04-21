var duration = 30000;
var timer;

var data = {
    'inclusion': {
        title: 'Include new devices',
        description: 'Press and hold the tamper button on the device to include it in the network',
        startURL: '/client/devices/inclusion/start',
        stopURL: '/client/devices/inclusion/stop'
    },
    'exclusion': {
        title: 'Exclude device',
        description: 'Press and hold the tamper button on the device to exclude it from the network',
        startURL: '/client/devices/exclusion/start',
        stopURL: '/client/devices/exclusion/stop'
    }
};

$(document).ready(function () {
    $('#deviceModal').on('show', function () {
        var mode = $(this).data('mode');
        var titleLabel = $('#modalLabel');
        var textLabel = $('p#text-placeholder');

        //set the title and text of the dialog based on the type
        titleLabel.text(data[mode].title);
        textLabel.text(data[mode].description);

        var proggresBar = $('#progressbar');
        proggresBar.css('width', '0%');
        startTimer(function (val) {
            if (val == 'stop') {
                $('#deviceModal').modal('hide');
            } else {
                proggresBar.css('width', val);
            }
        });
    });

    $('#deviceModal').on('hide', function () {      
        if (timer != null) {
        	 stopTimer();
        	 stopMode($(this).data('mode'));
        }
    });
});

function startMode(mode) {
    $.ajax({
        type: "POST",
        url: data[mode].startURL,
        data: {
            duration: duration
        },
        success: function () {
            $('#deviceModal')
                .data('mode', mode)
                .data('duration', duration)
                .modal('show');
        },
    });
}

function stopMode(mode) {
    $.ajax({
        type: "POST",
        url: data[mode].stopURL,
    });
}

function startTimer(callback) {
    var progress = 0;
    var interval = 1000;

    if (timer) {
        stopTimer();
    }

    timer = setInterval(function () {
        progress += interval;

        var percentage = Math.round((progress / duration) * 100) + '%';

        callback && callback(percentage);

        if (progress >= duration) {
            stopTimer();
            callback('stop');
        }
    }, interval);
}

function stopTimer() {
	if (timer != null) {
	    clearInterval(timer);
	    timer = null;
    }
}