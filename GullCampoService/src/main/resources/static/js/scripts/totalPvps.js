function updateMargin(pvpHolder) {
	var label = pvpHolder.prop("id");
	var pvp = Number(pvpHolder.children().first().text());
	var cost = Number($(".cost-" + label).first().text());
	var margin = $(".margin-" + label).first();
	if (pvp != 0) {
		margin.text((100*(1-(cost/pvp))).toFixed(2));
	} else {
		margin.text(-100);
	}
}

function reload() {
	var sumatorios = $(".pvp-totaller");
	sumatorios.each(function() {
		var label = $(this).prop("id");
		var pvps = $("." + label);
		var amount = totalAmount(pvps);
		$(this).children().first().text(amount);
		updateMargin($(this));
	});
}

function totalAmount(pvps) {
	var resultado = 0;
	pvps.each(function() {
		var content = $(this).val().replace(",", ".");
		if ($.isNumeric(content)){
			resultado = resultado + Number(content.trim());
		}
	});
	return resultado.toFixed(2);
}

$(document).ready(function() {
	var sumatorios = $(".pvp-totaller");
	sumatorios.each(function() {
		var sumatorioEl = $(this);
		var label = $(this).prop("id");
		var pvps = $("." + label);
		pvps.on("input", function() {
			var amount = totalAmount(pvps);
			sumatorioEl.children().first().text(amount);
			updateMargin(sumatorioEl);
		});
	});
	reload();
	$(".margin-input-field").on("input", function() {reload()});
});