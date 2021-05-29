$(document).ready(function() {
    // Initialise the table
    $("#tabla-ordenable").tableDnD({
		onDrop : function() {
			$(".linea-order").each(function(index, element) {
				console.log(index);
				$(element).val(index);
			});
		}
	});
});