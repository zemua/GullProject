$(document).ready(function() {
    var selects = $('.column-selector');
	var options = $('.mono-option');
	
	selects.each(function(){
		$(this).data('seleccion', '');
	});
	
	selects.change(function() {
		var valor = this.value;
		var seleccion = $(this).data('seleccion');
		
		// enable unselected item
		options.filter(function() {
			return seleccion==this.value;
		}).prop('disabled', false);
		
		// disable selected item
		options.filter(function(){
			return this.value==valor;
		}).prop('disabled', valor !== "");
		
		// set selected value for re-enabling
		$(this).data('seleccion', valor);
	});
});