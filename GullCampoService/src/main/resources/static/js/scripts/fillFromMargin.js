function validateCostDouble(costs){
	var resultado = true;
	costs.each(function() {
		var content = $(this).text().replace(",", ".");
		if (!$.isNumeric(content)){
			$(this).parent().css("background-color", "red");
			$(this).css("background-color", "red");
			resultado = false;
		}
	});
	return resultado;
}

function getCostValue(marginEl) {
	var tag = marginEl.prop("id");
	var costes = $(".cost-" + tag);
	var validos = validateCostDouble(costes);
}

$(document).ready(function() {
	var margins = $(".margin-input-field");
	margins.on("input", function() {
		var coste = getCostValue($(this));
	});
});