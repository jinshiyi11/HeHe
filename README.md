《呵呵》android客户端
收集展示网上各种有意思的图片，视频，日志

收集并分享人人网，新浪微博，微信，糗百，facebook，google+等网站上有意思的相册，视频，日志，语音...


DONE:
.gif图片展示
.下载整个相册
.播放视频没有全屏

TODO:
.分享多张图片
.全屏播放问题
.WebViewWrapper加载进度，loading状态展示
.进入视频全屏播放时正在加载状态的优化
.webview播放视频白屏，log显示nativeOnDraw failed; clearing to background color.
.视频暂停时自动缓冲
.缓冲播放过的视频以及清理视频缓存
.视频播放库大概增加apk大小8M，减小视频播放库的大小
.同步收藏

WebViewWrapper完善

ChangeLog:

1.2.0.0011
    1.支持gif图片
    2.修复滑动新鲜事列表时出现闪动的问题

1.2.0.0012
    1.修复播放视频只有声音没有图像的问题
    2.视频播放支持全屏
    
1.3.0.1
    1.增加热门日志
    2.修复播放视频不稳定的bug。不在网页中播放，直接获取视频mp4或flv地址播放
