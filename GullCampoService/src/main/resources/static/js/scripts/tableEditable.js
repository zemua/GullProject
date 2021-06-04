$(document).ready(function() {
	var fields = $(".field").each(function() {
		var text = $(this).find(".field-text");
		var input = $(this).find(".field-input");
	
		$(this).click(function() {
			text.focus();
		});
		
		text.on('input', function() {
			input.val($(this).text());
		});
	})
});