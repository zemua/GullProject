function shallBeHidden(filtros, linea) {
	var result = false;
	filtros.each(function() {
		var searchText = $(this).children().first().val();
		if (searchText.trim()) {
			var indextag = $(this).prop("id");
			var content = linea.find('.' + indextag).first().text().toLowerCase();
			var splitSearch = searchText.toLowerCase().split(" ");
			$.each(splitSearch, function(i, v) {
				if(content.indexOf(v)==-1){
					result = true;
				}
			});
		}
	});
	return result;
}

function filtrarLineas(filtros, lineas) {
	lineas.each(function() {
		var ocultar = shallBeHidden(filtros, $(this));
		if (ocultar) {
			$(this).hide();
		} else {
			$(this).show();
		}
	});
}

function setLimpiarFiltros(element, filtros){
	element.on("click", function(event) {
		event.preventDefault();
		filtros.val("");
		filtros.first().trigger("input");
	});
}

$(document).ready(function() {
	var filtros = $(".filtro");
	var lineas = $(".line-container");
	var filtroInput = $(".filtro").children(".field-text");
	filtroInput.on("input", function() {
		filtrarLineas(filtros, lineas);
	});
	// execute once on load in case any input has text
	filtrarLineas(filtros, lineas);
	var botonLimpiar = $("#limpiar-filtros");
	setLimpiarFiltros(botonLimpiar, filtroInput);
});