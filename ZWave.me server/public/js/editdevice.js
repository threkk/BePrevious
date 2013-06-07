$(document).ready(function () {
    $(':submit').click(function () {
        var inputTimeout = $("#inputTimeout");
        var inputCalibratedTemp = $("#inputCalibratedTemp");

        var deviceId = $("#deviceId").val()
        var data = {};

        if (inputTimeout.length != 0) {
            var sleeptime = $("#inputTimeout").val();
            if (sleeptime) {
                data.sleeptime = sleeptime;
            }
        }

        if (inputCalibratedTemp.length != 0) {
            var calibratedTemp = $("#inputCalibratedTemp").val();
            if (calibratedTemp) {
                data.calibratedTemp = calibratedTemp;
            }
        }
		
        var url;
        if ($(this).val() == 'saveAll') {
            url = '/devices/edit/all/' + deviceId
        } else {
            url = '/devices/edit/' + deviceId
        }

        $.ajax({
                type: "POST",
                url: url,
                data: data,
                success: function (msg) {
                	console.log('pre succes')
                    displaySuccesAlert();
                },
                error: function (xhr, textStatus, errorThrown) {
                    displayErrorAlert();
                }
            });

        return false;
    });
});

function displaySuccesAlert() {
	console.log('succes alert');
}

function displayErrorAlert() {
	console.log('error alert');
}