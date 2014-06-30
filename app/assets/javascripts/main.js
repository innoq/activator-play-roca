require.config({
	paths: {
		jquery: "bower_components/jquery/dist/jquery",
		pjax: "bower_components/jquery-pjax/jquery.pjax"
	},
	shim: {
		pjax: {
			deps: ["jquery"]
		}
	}
});

require(["jquery", "pjax"], function($) {
	$(document.body).pjax("a", "#contents");
});
