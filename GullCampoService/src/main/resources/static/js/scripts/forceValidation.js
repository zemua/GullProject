function forceDouble(klase, forn, boton) {
	var formulario = $(forn);
	var button = $(boton);
	button.click(function(event) {
		event.preventDefault();
		if (validateDouble(klase)) {
			formulario.submit();
		}
	});
}

function validateDouble(klase) {
	var valores = $(klase);
	var resultado = true;
	valores.each(function(){
		$(this).text($(this).text().replace(",", "."));
		this.dispatchEvent(new Event('input'));
		if (!$.isNumeric($(this).text())){
			$(this).parent().css("background-color", "red");
			resultado = false;
		} else {
			$(this).parent().css("background-color", "");
		}
	});
	return resultado;
}

$(document).ready(function() {
	forceDouble(".is-double", "#val-form", "#submit-button");
});