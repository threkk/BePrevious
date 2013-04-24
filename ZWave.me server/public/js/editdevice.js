$(document).ready(function () {
	$("#editForm").submit(function(e) {		
		var deviceId = $("#deviceId").val()
		var timeout = $("#inputTimeout").val();
		var url = 'http://localhost:8083/ZWaveAPI/Run/devices['+deviceId+'].instances[0].commandClasses[0x84].Set('+timeout+',1)'
		$.post(url)
			.done(function(data) {
				alert("Data Loaded: " + data);
					});
   		return false;
	});
});