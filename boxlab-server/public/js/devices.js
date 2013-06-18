var duration = 30000;
var minDuration = 10000;
var maxDuration = 300000

var timer;
var devices;

var data = {
    'inclusion': {
        title: 'Include new devices',
        suffix: 'included',
        description: 'Press and hold the tamper button on the device to include it in the network',
        startURL: '/client/devices/inclusion/start',
        stopURL: '/client/devices/inclusion/stop'
    },
    'exclusion': {
        title: 'Exclude device',
        suffix: 'excluded',
        description: 'Press and hold the tamper button on the device to exclude it from the network',
        startURL: '/client/devices/exclusion/start',
        stopURL: '/client/devices/exclusion/stop'
    }
};

$(document).ready(function () {
	var socket = io.connect('/client');
	socket.on('device_added', function (data) {
	    console.log('device added');
	    devices++;
	    updateModalLabels();
  	});
  	socket.on('device_removed', function (data) {
	    console.log('device removed');
	    devices++;
	    updateModalLabels();
  	});

    $(function () {
        $("#slider").slider({
            value: (duration/1000),
            min: (minDuration/1000),
            max: (maxDuration/1000),
            step: 10,
            slide: function (event, ui) {
            	var value = ui.value;
                $("#sleeptime").text(value);
                
                duration = value * 1000;
            }
        });
        $("#sleeptime").text($("#slider").slider("value"));
    });

    $('#deviceModal').on('show', function () {
        var mode = $(this).data('mode');
        var titleLabel = $('#modalLabel');
        var suffixLabel = $('#suffixLabel');
        var textLabel = $('p#text-placeholder');

        //set the title and text of the dialog based on the type
        titleLabel.text(data[mode].title);
        textLabel.text(data[mode].description);
        suffixLabel.text(data[mode].suffix);
        
       	updateModalLabels();

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

function updateModalLabels() {
	var devicesLabel = $('#devicesLabel');
	devicesLabel.text('' + devices);
}

function startMode(mode) {
	devices = 0;
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

	var timerspan = $("#timerspan");
	timerspan.text((duration - progress) / 1000);
    timer = setInterval(function () {
        progress += interval;
		
        var percentage = Math.round((progress / duration) * 100) + '%';
		
        callback && callback(percentage);
        if (progress >= duration) {
            stopTimer();
            callback('stop');
        }
        timerspan.text((duration - progress) / 1000);
    }, interval);
}

function stopTimer() {
    if (timer != null) {
        clearInterval(timer);
        timer = null;
    }
}