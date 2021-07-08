function resize(input) {
    input.width(input.val().length * 8);
}

function validateCostDouble(costs){
	var resultado = true;
	costs.each(function() {
		var content = $(this).text().replace(",", ".");
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

function amountCost(costs) {
	var resultado = 0;
	if (validateCostDouble(costs)){
		costs.each(function() {
			var content = $(this).text().replace(",", ".");
			resultado += Number(content);
		});
	}
	return resultado;
}

function getCostValue(marginEl) {
	var tag = marginEl.prop("id");
	var costes = $(".cost-" + tag);
	var coste = amountCost(costes);
	return coste;
}

function setMargins(pvpEl, cost) {
	var tag = pvpEl.prop("id");
	var marginEl = $(".margin-" + tag).first();
	if (!$.isNumeric(pvpEl.val()) && pvpEl.val().trim() != ""){
		pvpEl.parent().css("background-color", "red");
		pvpEl.css("background-color", "red");
	} else {
		pvpEl.parent().css("background-color", "");
		pvpEl.css("background-color", "");
	}
	var pvp = Number(pvpEl.val());
	if (pvp != 0) {
		var margin = (100*(1-(cost/pvp)));
		margin = margin.toFixed(2);
		marginEl.val(margin);
		resize(marginEl);
	} else {
		marginEl.val(-100);
		resize(marginEl);
	}
}

$(document).ready(function() {
	var pvps = $(".pvp-input-field");
	pvps.on("input", function() {
		resize($(this));
		var precio = $(this).val().replace(",", ".");
		$(this).val(precio);
		var coste = getCostValue($(this));
		if (coste != 0){
			setMargins($(this), coste);
		}
	});
});