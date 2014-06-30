require.config({
	paths: {
		jquery: "/assets/jquery/dist/jquery",
		pjax: "/assets/jquery-pjax/jquery.pjax"
	},
	shim: {
		pjax: {
			deps: ["jquery"]
		}
	}
});

require(["jquery", "pjax"], function($) {
	$("form[novalidate]").on("submit", validateForm);

	var selector = "#contents";
	$(document.body).pjax("a", selector);
	$(document).on("submit", "form[role=search]", function(ev) {
		$.pjax.submit(ev, selector)
	});

	function validateForm(ev) { // XXX: simplistic, for illustration purposes only
		var fields = $("input, textarea, select", this).filter("[data-on-error]");
		var errors = fields.map(function(i, node) {
			var field = $(node);
			var group = field.closest(".form-group"); // XXX: Bootstrap-specific
			var error = group.find(".error");
			var message = field.data("on-error"); // XXX: misleading attribute name

			if(node.validity.valid ||
					!message || !error.length) { // can't cope client-side
				error.text("").addClass("hidden");
				return null;
			}

			error.text(message).removeClass("hidden");
			return node;
		});
		if(errors.length) {
			ev.preventDefault();
		}
	}
});
