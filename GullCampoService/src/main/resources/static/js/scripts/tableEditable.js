$(document).ready(function() {
	var fields = $(".field").each(function() {
		var texts = $(this).find(".field-text");
		var inputs = $(this).find(".field-input");
	
		$(this).click(function() {
			texts.focus();
		});
		
		texts.on('input', 'input:text', function() {
			inputs.val($(this).html());
		});
	})
});