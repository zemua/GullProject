function getCotizaciones(selector) {
	return $("."+selector);
}

function getCostCheckboxes(cotizacion) {
	return $(".cost" + cotizacion.prop("id"));
}

function getAttCheckboxes(cotizacion) {
	return $(".attr" + cotizacion.prop("id"));
}

function setEnablerDisabler(costs, atts) {
	costs.on("change", function() {
		var disabled = true;
		costs.each(function() {
			if ($(this).is(':checked')) {
				disabled = false;
			}
		});
		atts.each(function() {
			$(this).prop("disabled", disabled);
		});
	});
}

function go(selector) {
	var cotizaciones = getCotizaciones(selector);
	cotizaciones.each(function() {
		var costs = getCostCheckboxes($(this));
		var atts = getAttCheckboxes($(this));
		setEnablerDisabler(costs, atts);
		costs.trigger("change");
	});
}

$(document).ready(function() {
	go("es-cotizacion");
});