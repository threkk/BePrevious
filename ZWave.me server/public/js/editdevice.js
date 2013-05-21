$(document).ready(function () {
    $(':submit').click(function(){
       // e.preventDefault();
       var valueSubmit = $(this).val();
       
        var inputTimeout = $("#inputTimeout");
        var tempOffset = $("#tempOffset").val();

        var deviceId = $("#deviceId").val()
        var data = {
        	id: deviceId,
        }

        if (inputTimeout.length != 0) {
			data.sleeptime = $("#inputTimeout").val();
			var url;
			if(valueSubmit == 'saveAll'){            
        	 url = 'http://localhost:8080/devices/edit/all/' + data.id
            } else {
             url = 'http://localhost:8080/devices/edit/' + data.id
            }
            $.post(url,data)
                .done(function (done) {
                alert("Data Loaded: " + done);
            });

        }
        
        console.log('edit device.js');
        
        return false;
    });
});