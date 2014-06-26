tidy();
//alert("sadfasd");

function tidy()
{
	hostname=location.hostname;
	if(hostname.search(/youku.com/i)!=-1){
		youku();
	}
}

function youku()
{
	//搜索框
	removeElements(document.querySelectorAll(".yk-header"));
	//app下载提示
	removeElements(document.querySelectorAll(".app-download"));
	//评论
	removeElements(document.querySelectorAll(".yk-vcontent"));
	removeElements(document.querySelectorAll(".sidebar"));
	removeElements(document.querySelectorAll(".yk-footer"));
}

function removeElements(elements)
{
	for (var i = 0; i < elements.length; i++) 
	{
		var node=elements[i];
	    node.parentNode.removeChild(node);
	}
}



