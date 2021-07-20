const map = new Map();
function getMapOfChecks(pvpchecks, sumchecks) {
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

function copyToClipboard(text) {
    var dummy = document.createElement("textarea");
    // to avoid breaking orgain page when copying more words
    // cant copy when adding below this code
    // dummy.style.display = 'none'
    document.body.appendChild(dummy);
	// For some reason, I had to use .textContent instead of .value
    dummy.value = text;
    dummy.select();
    document.execCommand("copy");
    document.body.removeChild(dummy);
}

function exportar(cabeceras, pvpstop, campos, pvpfield, lineas) {
	let texto = "";
	
	cabeceras.each(function() {
		texto += $(this).text();
		texto += "\t"
	});
	
	pvpstop.each(function() {
		if ($(this).prev().is(":checked")) {
			texto += $(this).text();
			texto += "\t";
		}
	});
	texto += "\n";
	
	lineas.each(function() {
		let label = $(this).prop("id");
		let currentCampo = campos.filter("."+label);
		currentCampo.each(function() {
			texto += $(this).text();
			texto += "\t";
		});
		var currentpvps = pvpfield.filter("."+label);
		currentpvps.each(function() {
			let masterlabel = $(this).data("masterid");
			let master = $("#"+masterlabel).first();
			if (master.is(":checked")) {
				texto += $(this).text();
				texto += "\t";
			}
		});
		texto += "\n";
	});
	
	copyToClipboard(texto);
	
	$("#notification").fadeIn("slow").text('copiado al portapapeles');
		$(".dismiss").click(function(){
			$("#notification").fadeOut("slow");
		});
	setTimeout(function() {
			$("#notification").fadeOut("slow");
		}, 2000);
}

function setupExport(copyButton, cabeceras, pvpstop, campos, pvpfield, lineas) {
	copyButton.click(function(event) {
		event.preventDefault();
		exportar(cabeceras, pvpstop, campos, pvpfield, lineas);
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
	
	var copyButton = $("#portapapeles-button").first();
	var cabeceras = $("[data-exportablealways]");
	var pvpstop = $("[data-exportablemaybe]");
	var campos = $("[data-exportable]");
	var pvpfield = $("[data-exportablecond]");
	setupExport(copyButton, cabeceras, pvpstop, campos, pvpfield, lineas);
});