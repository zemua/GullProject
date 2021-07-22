function multiCheckBox(masterKl, slaveKl) {
	var masters = $(masterKl + ':checkbox');
	masters.on("change", function(){
		propagate(this, slaveKl);
	});
}

function propagate(elClicado, slaveKl) {
	var indextag = $(elClicado).prop('id');
	var concerned = $(slaveKl + '.' + indextag + ':visible');
	if ($(elClicado).is(':checked')){
		concerned.prop('checked', true);
	} else {
		concerned.prop('checked', false);
	}
	concerned.trigger("change");
}

function markAdjacentDoublesForValidation(slavecheckbox, validables){
	var boxes = $(slavecheckbox);
	boxes.on('change', function() {
		setupClassesforValidation(this, validables);
	});
	
	// in case a checkbox is checked on load do it once on start
	boxes.trigger('change');
}

function setupClassesforValidation(current, validables) {
	var text1 = $(current).parent().prev('td').prev('td').children(validables);
	var text2 = $(current).parent().prev('td').children(validables);
	var costTexts = $(current).parent().parent().find(".cost-" + $(current).prop("id"));
	if ($(current).is(':checked')) {
		text1.addClass("checked");
		text2.addClass("checked");
		costTexts.addClass("checked");
	} else {
		text1.removeClass("checked");
		text2.removeClass("checked");
		costTexts.removeClass("checked");
		text1.parent().css("background-color", "");
		text2.parent().css("background-color", "");
		text1.css("background-color", "");
		text2.css("background-color", "");
	}
}

$(document).ready(function() {
	multiCheckBox(".mastercheck", ".slavecheck");
	markAdjacentDoublesForValidation(".slavecheck", ".is-double-checkable");
});