$(document).ready(function() {
	$(':submit').click(function() {
		var inputName = $("#inputName");
		var inputTimeout = $("#inputTimeout");
		var inputCalibratedTemp = $("#inputCalibratedTemp");

		var deviceId = $("#deviceId").val()
		var data = {};

		if (inputName.length != 0) {
			var name = $("#inputName").val();
			if (name) {
				data.name = name;
			}
		}

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
			type : "POST",
			url : url,
			data : data,
			success : function(msg) {
				displaySuccesAlert();
			},
			error : function(xhr, ajaxOptions, thrownError) {
				displayErrorAlert(xhr.responseText.error);
			}
		});

		return false;
	});
});

function displaySuccesAlert() {
	var alert = new Alert('Succes', 'A command to change the settings was sent to the sensor(s)');
	alert.render('alertDiv');
}

function displayErrorAlert(errorMessage) {
	var alert = new Alert('Error', 'Failed to push changes to the sensor(s): ' + errorMessage,
			'error');
	alert.render('alertDiv');
}