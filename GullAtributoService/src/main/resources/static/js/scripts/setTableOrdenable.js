function guardarOrden() {
	$(".linea-order").each(function(index, element) {
		$(element).val(index);
	});
}

$(document).ready(function() {
    // Initialise the table
    $("#tabla-ordenable").tableDnD({
		onDrop : function() {
			guardarOrden();			
		}
	});
});

$(function() {
	$("#tabla-ordenable").tablesorter()
	
	.bind("sortEnd", function(e, t) {
		guardarOrden();
	});
});