(function() {
	tidy();

	function tidy() {
		hostname = location.hostname;
		if (hostname.search(/youku.com/i) != -1) {
			youku();
		} else if (hostname.search(/56.com/i) != -1) {
			web56();
		} else if (hostname.search(/iqiyi.com/i) != -1) {
			iqiyi();
		} else if (hostname.search(/qq.com/i) != -1) {
			qq();
		} else if (hostname.search(/tudou.com/i) != -1) {
			tudou();
		}
	}
	
	function removeElements(elements) {
		if (!Boolean(elements))
			return;

		for ( var i = 0; i < elements.length; i++) {
			var node = elements[i];
			node.parentNode.removeChild(node);
		}
	}
	
	function fireTouchEvent(element){
		var evt = document.createEvent('TouchEvent');
		evt.initTouchEvent('touchstart', true, true);
		evt.view = element;
		evt.altKey = false;
		evt.ctrlKey = false;
		evt.shiftKey = false;
		evt.metaKey = false;
		element.dispatchEvent(evt);

		var evt1 = document.createEvent('TouchEvent');
		evt1.initTouchEvent('touchend', true, true);
		evt1.view = element;
		evt1.altKey = false;
		evt1.ctrlKey = false;
		evt1.shiftKey = false;
		evt1.metaKey = false;
		element.dispatchEvent(evt1); 
	}

	function youku() {
		removeElements(document.querySelectorAll(".yk-header"));
		removeElements(document.querySelectorAll(".app-download"));
		removeElements(document.querySelectorAll(".yk-vcontent"));
		removeElements(document.querySelectorAll(".sidebar"));
		removeElements(document.querySelectorAll(".yk-footer"));
		
		fireTouchEvent(document.querySelector(".x-video-button"));
		document.querySelector(".x-video-button").click();
		document.querySelector(".x-fullscreen").click();
	}

	function web56() {
		removeElements(document.querySelectorAll("div.header_bg"));
		removeElements(document.querySelectorAll("header nav"));
		removeElements(document.querySelectorAll("header input"));
		removeElements(document.querySelectorAll("header"));
		
		removeElements(document.querySelectorAll(".topBanner"));
		removeElements(document.querySelectorAll(".video_desc"));
		removeElements(document.querySelectorAll("div#uploadedBy"));
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
		
		document.querySelector("div#cover_m_player").click();
		document.querySelector("div#bar_m_player_fsbtn").click();

	}

	function iqiyi() {
		removeElements(document.querySelectorAll(".header-index"));
		removeElements(document.querySelectorAll("div#appBanner.mod-load"));
		
		removeElements(document.querySelectorAll(".videoExtendBar"));
		removeElements(document.querySelectorAll(".mt5.bd-pd-play.mb20"));
		removeElements(document.querySelectorAll(".h5_footer"));
		removeElements(document.querySelectorAll(".backToTop"));
		document.querySelector("#player-bigBtn").click();
	}

	function qq() {
		removeElements(document.querySelectorAll(".site_header"));
		
		document.querySelector(".tvp_overlay_play").click();
		document.querySelector(".tvp_fullscreen_button").click();
	}
	
	function tudou() {
		removeElements(document.querySelectorAll("#gTop"));
		removeElements(document.querySelectorAll("#appRecom"));
		
		removeElements(document.querySelectorAll(".act-view"));
		removeElements(document.querySelectorAll(".tab-view"));
		removeElements(document.querySelectorAll(".cont-view"));
		removeElements(document.querySelectorAll("#appRecom"));
		removeElements(document.querySelectorAll("#gBot"));
		
		document.querySelector(".tvp_overlay_play").click();
		document.querySelector(".tvp_fullscreen_button").click();
	}

}());
