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
                    displaySuccesAlert();
                },
                error: function (xhr, textStatus, errorThrown) {
                    console.log('error: ' + textStatus);
                    displayErrorAlert();
                }
            });

        return false;
    });
});

function displaySuccesAlert() {
	var alert = new Alert('Succes', 'A command to change the settings was sent to the sensors');
	alert.render('alertDiv');
}

function displayErrorAlert() {
	var alert = new Alert('Error', 'Failed to push changes to the sensors, please see the console of your browser for more info', 'error');
	alert.render('alertDiv');
}