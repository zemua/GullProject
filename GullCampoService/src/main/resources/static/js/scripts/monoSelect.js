function restringir(sel, opt) {
	var selects = $(sel);
	var options = $(opt);
	
	selects.each(function(){
		$(this).data('seleccion', '');
	});
	
	selects.change(function() {
		var current = this;
		var valor = this.value;
		var seleccion = $(this).data('seleccion');
		
		// enable unselected item
		options.filter(function() {
			return seleccion==this.value;
		}).prop('disabled', false);
		
		// disable selected item
		options.filter(function(){
			if (current == this.parentNode) {
				return false;
			} else {
				return this.value==valor;
			}
		}).prop('disabled', valor !== "");
		
		// set selected value for re-enabling
		$(this).data('seleccion', valor);
	});
}

$(document).ready(function() {
	restringir('.column-selector', '.mono-option');
	restringir('.name-selector', '.mono-name');
});