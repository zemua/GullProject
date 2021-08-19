function forceDouble(klase, forn, boton) {
	var formulario = $(forn);
	var button = $(boton);
	button.click(function(event) {
		event.preventDefault();
		if (validateDouble(klase) && validateMargins(klase)) {
			fillEmptiesWithZeros();
			formulario.submit();
		} else {
			notifica("Hay valores no válidos en celdas seleccionadas");
		}
	});
}

function notifica(mensaje) {
	$("#notification").fadeIn("slow").text(mensaje);
	$(".dismiss").click(function(){
		$("#notification").fadeOut("slow");
	});
	setTimeout(function() {
		$("#notification").fadeOut("slow");
	}, 2000);
}

function fillEmptiesWithZeros() {
	var fields = $(".is-double-checkable");
	fields.each(function() {
		if ($(this).val().trim() == "" || !$.isNumeric($(this).val().trim())) {
			$(this).val(0.0);
		}
	});
}

function validateDouble(klase) {
	var valores = $(klase);
	var resultado = true;
	valores.each(function(){
		var content;
		if ($(this).is("span")) {
			$(this).text($(this).text().replace(",", "."));
			$(this).trigger('input');
			content = $(this).text();
		} else {
			$(this).text($(this).val().replace(",", "."));
			content = $(this).val();
		}
		//this.dispatchEvent(new Event('input'));
		if (!$.isNumeric(content) && content.trim() != ""){
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
		if (valor >= 100 || valor < 0) {
			$(this).parent().css("background-color", "red");
			$(this).css("background-color", "red");
			result = false;
		} else {
			$(this).parent().css("background-color", "");
			$(this).css("background-color", "");
		}
	});
	return result;
}

$(document).ready(function() {
	forceDouble(".is-double", "#val-form", "#submit-button");
	forceDouble(".is-double-checkable.checked", "#val-form-checkable", "#submit-button-checkable");
});