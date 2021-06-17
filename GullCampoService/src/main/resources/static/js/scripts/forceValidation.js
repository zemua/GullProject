function forceDouble(klase, forn, boton) {
	var formulario = $(forn);
	var button = $(boton);
	button.click(function(event) {
		event.preventDefault();
		console.log("class to validate " + klase);
		if (validateDouble(klase)) {
			formulario.submit();
		}
	});
}

function validateDouble(klase) {
	var valores = $(klase);
	console.log("valores: " + valores);
	var resultado = true;
	valores.each(function(){
		if (!$.isNumeric($(this).text())){
			console.log($(this).text() + " no es numerico");
			$(this).parent().css("background-color", "red");
			resultado = false;
		} else {
			console.log("si es numerico");
			$(this).parent().css("background-color", "");
		}
	});
	return resultado;
}

$(document).ready(function() {
	forceDouble(".is-double", "#val-form", "#submit-button");
});