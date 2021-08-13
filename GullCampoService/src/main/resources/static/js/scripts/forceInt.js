function forceInt(klase, forn, boton) {
	var formulario = $(forn);
	var button = $(boton);
	button.click(function(event) {
		event.preventDefault();
		if (validateInt(klase)) {
			formulario.submit();
		}
	});
}

function validateInt(klase) {
	var valores = $(klase);
	var resultado = true;
	valores.each(function(){
		var content;
		if ($(this).is("span")) {
			content = $(this).text();
		} else {
			content = $(this).val();
		}
		//this.dispatchEvent(new Event('input'));
		if (!(/^[1-9]\d*$/.test(content)) && content.trim() != ""){
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

$(document).ready(function() {
	forceInt(".is-int", "#val-form", "#submit-button");
});