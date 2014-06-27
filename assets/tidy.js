(function() {
	tidy();

	function tidy() {
		hostname = location.hostname;
		if (hostname.search(/youku.com/i) != -1) {
			youku();
		} else if (hostname.search(/56.com/i) != -1) {
			web56();
		}
	}

	function youku() {
		removeElements(document.querySelectorAll(".yk-header"));
		removeElements(document.querySelectorAll(".app-download"));
		removeElements(document.querySelectorAll(".yk-vcontent"));
		removeElements(document.querySelectorAll(".sidebar"));
		removeElements(document.querySelectorAll(".yk-footer"));
	}

	function web56() {
		removeElements(document.querySelectorAll("header"));
		removeElements(document.querySelectorAll(".topBanner"));
		removeElements(document.querySelectorAll(".video_desc"));
		removeElements(document.querySelectorAll(".share_layer"));
		removeElements(document.querySelectorAll("#herderOutside h1"));

		removeElements(document.querySelectorAll("div#bottomNvaBtn"));
		removeElements(document.querySelectorAll("div#bottomNva"));
		removeElements(document.querySelectorAll("#player_bottom"));
		removeElements(document.querySelectorAll("div#variety"));
		removeElements(document.querySelectorAll("div#video_list"));
		removeElements(document.querySelectorAll("div#cmt"));
		removeElements(document.querySelectorAll("#down_app_centre"));
		removeElements(document.querySelectorAll("footer"));
	}

	function removeElements(elements) {
		if (!Boolean(elements))
			return;

		for ( var i = 0; i < elements.length; i++) {
			var node = elements[i];
			node.parentNode.removeChild(node);
		}
	}

}());
