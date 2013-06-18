function Alert(title, message, type) {
	this.title = title;
	this.message = message;
	this.type = type;
}

Alert.prototype = {
	render: function(elementID) {
		var clazz = 'alert alert-success';
		if (this.type == 'warning') {
			clazz = 'alert alert-block';
		} else if (this.type=='error') {
			clazz = 'alert alert-error';
		}
		
		var alertHTML = '<div class="' + clazz + '" data-dismiss="alert">'
            + '	<button type="button" class="close" data-dismiss="alert">&times;</button>'
            + '	<h4>' + this.title + '</h4> ' + this.message + '</div>';
            
        $("#"+elementID).html(alertHTML);
	}
}