(function() {
	tidy();

	function tidy() {
		hostname = location.hostname;
		if (hostname.search(/youku.com/i) != -1) {
			youku();
		}
	}

	function youku() {		
		removeElements(document.querySelectorAll(".yk-header"));
		removeElements(document.querySelectorAll(".app-download"));
		removeElements(document.querySelectorAll(".yk-vcontent"));
		removeElements(document.querySelectorAll(".sidebar"));
		removeElements(document.querySelectorAll(".yk-footer"));
	}

	function removeElements(elements) {
		for ( var i = 0; i < elements.length; i++) {
			var node = elements[i];
			node.parentNode.removeChild(node);
		}
	}

}());
