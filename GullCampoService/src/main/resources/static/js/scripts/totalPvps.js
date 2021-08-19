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

function reload(sumatorioEl) {
	var label = sumatorioEl.prop("id");
	var pvps = $("." + label + ".checked.sumatorio");
	var amount = totalAmount(pvps);
	sumatorioEl.children().first().text(amount);
	updateMargin(sumatorioEl);
}

function match(sumatorioEl) {
	var label = sumatorioEl.prop("id");
	var pvps = $("." + label);
	var costs = $(".costbreakdown-" + label);
	var totMargin = $(".margin-" + label).first();
	var totCost = $(".cost-" + label).first();
	
	var totalCost = getTotalCost(costs.filter(".checked"));
	totCost.text(totalCost.toFixed(2));
	var totalPvp = Number(sumatorioEl.children().first().text());
	updateMargin(sumatorioEl);
}

function getTotalCost(costs) {
	var resultado = 0;
	costs.each(function() {
		resultado += Number($(this).text());
	});
	return resultado;
}

function totalAmount(pvps) {
	var resultado = 0;
	pvps.each(function() {
		var content = $(this).text().replace(",", ".");
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
			reload(sumatorioEl);
			match(sumatorioEl);
		});
		reload(sumatorioEl);
		match(sumatorioEl);
		pvps.each(function() {
			var tag = $(this).prop("id");
			var marginEl = $(".margin-" + tag).first();
			marginEl.on("input", function() {reload(sumatorioEl)});
		});
		var checkboxes = $(".check-" + label);
		checkboxes.on('change', function() {
			reload(sumatorioEl);
			match(sumatorioEl);
		});
	});
});