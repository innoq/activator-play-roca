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
	var selector = "#contents";
	$(document.body).pjax("a", selector);
	$(document).on("submit", "form[role=search]", function(ev) {
		$.pjax.submit(ev, selector)
	})
});
