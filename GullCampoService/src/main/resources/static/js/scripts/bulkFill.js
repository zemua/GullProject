function getFieldsOfInput(input){
	var indextag = input.prop("id");
	var campos = $("input." + indextag + ':visible');
	return campos;
}

function getInputElement(boton){
	var margin = $(boton).parent().next().children().first();
	var pvp = $(boton).parent().next().next().children().first();
	if (margin.val().trim()){
		return margin;
	} else if (pvp.val().trim()) {
		return pvp;
	}
	return null;
}

function clearInputElement(boton) {
	var margin = $(boton).parent().next().children().first();
	var pvp = $(boton).parent().next().next().children().first();
	margin.val("");
	pvp.val("");
}

$(document).ready(function() {
	var botonesRelleno = $("button.bulk-fill");
	botonesRelleno.each(function() {
		var boton = this;
		$(this).on("click", function(event) {
			event.preventDefault();
			var input = getInputElement(boton);
			var campos = getFieldsOfInput(input);
			if (input && $.isNumeric($(input).val().replace(",", "."))) {
				campos.val(input.val());
				campos.trigger("input");
				clearInputElement(boton)
			}
		});
	});
});