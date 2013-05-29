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
            url = 'http://145.92.3.184:8080/devices/edit/all/' + deviceId
        } else {
            url = 'http://145.92.3.184:8080/devices/edit/' + deviceId
        }
        
        $.post(url, data).done(function (done) {
            alert("Data Loaded: " + done);
        });

        return false;
    });
});