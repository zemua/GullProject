function forceDouble(klase, forn, boton, type) {
	var formulario = $(forn);
	var button = $(boton);
	button.click(function(event) {
		event.preventDefault();
		if (validateDouble(klase, type) && validateMargins(klase)) {
			formulario.submit();
		}
	});
}

function validateDouble(klase, type) {
	var valores = $(klase);
	var resultado = true;
	valores.each(function(){
		var content;
		if (type === "text") {
			$(this).text($(this).text().replace(",", "."));
			content = $(this).text();
		} else {
			$(this).text($(this).val().replace(",", "."));
			content = $(this).val();
		}
		//this.dispatchEvent(new Event('input'));
		if (!$.isNumeric(content)){
			$(this).parent().css("background-color", "red");
			$(this).css("background-color", "red");
			resultado = false;
		} else {
			$(this).parent().css("background-color", "");
			$(this).css("background-color", "");
		}
	});
	return resultado;
}

function validateMargins(klase){
	var margins = $(klase + ".margin-input-field");
	var result = true;
	margins.each(function(){
		var valor = Number($(this).val());
		if (valor >= 100) {
			$(this).parent().css("background-color", "red");
			$(this).css("background-color", "red");
			result = false;
		}
	});
	return result;
}

$(document).ready(function() {
	forceDouble(".is-double", "#val-form", "#submit-button", "text");
	forceDouble(".is-double-checkable.checked", "#val-form-checkable", "#submit-button-checkable", "val");
});