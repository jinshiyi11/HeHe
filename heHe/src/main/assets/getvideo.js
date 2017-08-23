(function() {
    //防止该脚本被重复加载
    if(typeof(selfLoaded)!="undefined"){
        console.log("current script alread loaded!");
        return;
    }    
    
    if(location.href=="about:blank"){
        console.log("current page is about:blank");
        return;
    } 
    
    selfLoaded=true;
    console.log("script start!");
    
    var tryCount=0;//尝试次数
	var tid=setInterval(getvideo,500);

	function getvideo() {
        tidy();
        
        tryCount++;
        if(tryCount>20){
            console.log("fail to find video element");
            clearInterval(tid);
            return;
        }
		var hostname = location.hostname;
        
        var video=document.querySelector("video");
        if(video == undefined || video.src == undefined){
            console.log("can not find video element.try time:"+tryCount);
            return;
        }
        console.log("video.src:"+video.src);
        if(!video.src){
            console.log("video.src is empty.try time:"+tryCount);
            return;
        }
        
        my.onGotVideoUrl(video.src);
        
        clearInterval(tid);
	}
    
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
		} else if (hostname.search(/sohu.com/i) != -1) {
			sohu();
		} else if (hostname.search(/yinyuetai.com/i) != -1) {
			yinyuetai();
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
		//搜索框
		removeElements(document.querySelectorAll(".yk-header"));
		//app下载提示
		removeElements(document.querySelectorAll(".app-download"));
		//评论
		removeElements(document.querySelectorAll(".yk-vcontent"));
		removeElements(document.querySelectorAll(".sidebar"));
		removeElements(document.querySelectorAll(".yk-footer"));
		
		fireTouchEvent(document.querySelector(".x-video-button"));
		document.querySelector(".x-video-button").click();
		document.querySelector(".x-fullscreen").click();
	}

	function web56() {
		//http://www.56.com/u75/v_OTQ3NDE4MzI.html
		removeElements(document.querySelectorAll("div.header_bg"));
		removeElements(document.querySelectorAll("header nav"));
		removeElements(document.querySelectorAll("header input"));
		removeElements(document.querySelectorAll("header div"));
		removeElements(document.querySelectorAll("header"));
		
		removeElements(document.querySelectorAll("div#gotop"));
		removeElements(document.querySelectorAll(".topBanner"));
		removeElements(document.querySelectorAll(".topBanner2"));
		removeElements(document.querySelectorAll(".switch_tab"));
		removeElements(document.querySelectorAll(".video_desc"));
		removeElements(document.querySelectorAll("div#uploadedBy"));
		removeElements(document.querySelectorAll(".share_layer"));
		removeElements(document.querySelectorAll("#herderOutside h1"));
		removeElements(document.querySelectorAll("div #footerSign"));
		removeElements(document.querySelectorAll("div #footerFixed"));
		removeElements(document.querySelectorAll("div .popWin"));

		removeElements(document.querySelectorAll("div#bottomNvaBtn"));
		removeElements(document.querySelectorAll("div#bottomNva"));
		removeElements(document.querySelectorAll("#player_bottom"));
		removeElements(document.querySelectorAll("div#variety"));
		removeElements(document.querySelectorAll("div#video_list"));
		removeElements(document.querySelectorAll("div#cmt"));
		removeElements(document.querySelectorAll("#down_app_centre"));
		removeElements(document.querySelectorAll("footer"));
		
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
	
	function sohu() {
		removeElements(document.querySelectorAll(".fixed_pinner"));
		removeElements(document.querySelectorAll("header"));
		removeElements(document.querySelectorAll(".js_sh_app_dl"));
		removeElements(document.querySelectorAll(".back2top"));
		
		removeElements(document.querySelectorAll("section.js_appbar"));
		removeElements(document.querySelectorAll("section.detail_section"));
		removeElements(document.querySelectorAll(".user_favor"));
		removeElements(document.querySelectorAll("footer"));
	}
	
	function yinyuetai() {
		//http://m.yinyuetai.com/video/839320
		removeElements(document.querySelectorAll("div.topbar"));
		removeElements(document.querySelectorAll("section.app-install"));
		removeElements(document.querySelectorAll("section.mv-info aside"));
		removeElements(document.querySelectorAll("div.content"));
		removeElements(document.querySelectorAll("div.video_box"));
		removeElements(document.querySelectorAll("div.footer"));
		
		document.querySelectorAll("div.wrapper")[0].style.paddingTop="0px";
		removeElements(document.querySelectorAll("body img[height='1']"));
	}

}());
