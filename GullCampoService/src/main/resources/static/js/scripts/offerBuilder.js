function getMapOfChecks(pvpchecks, sumchecks) {
	const map = new Map();
	pvpchecks.each(function() {
		map.set($(this).prop("id"), $(this).is(":checked"));
	});
	sumchecks.each(function() {
		var pvps = $(this).next().attr('class').split(/\s+/);
		var thischeck = $(this);
		for (let i = 0; i < pvps.length; i++){
			var este = pvps[i];
			if (thischeck.is(":checked")) {
				map.set(este, true);
			} else if(map.get(este) == null) {
				map.set(este, false);
			}
		}
	});
	return map;
}

function pvpVisibility(pvpCheckbox) {
	var label = pvpCheckbox.prop("id");
	var pvps = $(".pvp-value-field." + label);
	if (pvpCheckbox.is(":checked")) {
		pvps.show();
	} else {
		pvps.hide();
	}
}

function sumVisibility(sumCheckbox) {
	var label = sumCheckbox.prop("id");
	var sums = $(".sum-value-field." + label);
	if (sumCheckbox.is(":checked")) {
		sums.show();
	} else {
		sums.hide();
	}
}

function concatena(concatenables, concatenador) {
	for (let i = 0; i < concatenables.length; i++) {
		var este = concatenables[i];
		if (este.val().trim() != "") {
			if (concatenador.text().trim() != "") {
				concatenador.text(concatenador.text() + " / ");
			}
			concatenador.text(concatenador.text() + este.val());
		}
	}
}

function pvpOnClick(checkbox, lineas, atts, pvps, pvpmap) {
	pvpVisibility(checkbox);
	
	lineas.each(function() {
		var thisline = $(this);
		atts.each(function() {
			var thisatt = $(this);
			var concatenables = [];
			pvps.each(function() {
				var thispvp = $(this);
				if (pvpmap.get(thispvp.prop("id"))) {
					var cons = $(".field-concatenable." + thispvp.prop("id") + "." + thisline.prop("id") + "." + thisatt.prop("id"));
					cons.each(function(){
						concatenables.push($(this));
					});
				}
			});
			var concatenador = $(".concatenation-field." + thisline.prop("id") + "." + thisatt.prop("id"));
			concatenador.text("");
			concatena(concatenables, concatenador);
		});
	});
	
}

function setup(pvps, sums, lineas, atts) {
	var pvpmap = getMapOfChecks(pvps, sums);
	pvps.on("change", function() {
		pvpmap = getMapOfChecks(pvps, sums);
		pvpVisibility($(this));
		pvpOnClick($(this), lineas, atts, pvps, pvpmap);
	});
	sums.on("change", function() {
		pvpmap = getMapOfChecks(pvps, sums, pvpmap);
		sumVisibility($(this));
		pvpOnClick($(this), lineas, atts, pvps, pvpmap);
	});
}

$(document).ready(function() {
	var lineas = $(".linea-cliente");
	var pvps = $(".pvpcheckbox");
	var sums = $(".sumcheckbox");
	var atts = $(".attribute-td");
	
	setup(pvps, sums, lineas, atts);
	
	sums.trigger("change");
	pvps.trigger("change");
});