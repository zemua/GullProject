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

function setPvp(marginEl, cost) {
	var tag = marginEl.prop("id");
	var pvpEl = $(".pvp-" + tag).first();
	var margin = Number(marginEl.val());
	if (margin < 100 && margin >= 0) {
		var pvp = (cost/(1-(margin/100)));
		pvp = pvp.toFixed(2);
		pvpEl.val(pvp);
		resize(pvpEl);
		marginEl.parent().css("background-color", "");
		marginEl.css("background-color", "");
	} else {
		marginEl.parent().css("background-color", "red");
		marginEl.css("background-color", "red");
	}
}

$(document).ready(function() {
	var margins = $(".margin-input-field");
	margins.each(function() {resize($(this))});
	margins.on("input", function() {
		resize($(this));
		var porc = $(this).val().replace(",", ".");
		$(this).val(porc);
		var coste = getCostValue($(this));
		if (coste != 0){
			setPvp($(this), coste);
		} else {
			$(this).val("");
		}
	});
});