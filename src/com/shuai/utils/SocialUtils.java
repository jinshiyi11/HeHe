package com.shuai.utils;

import android.app.Activity;
import android.text.TextUtils;

import com.shuai.hehe.data.Constants;
import com.shuai.hehe.data.Stat;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.media.QQShareContent;
import com.umeng.socialize.media.QZoneShareContent;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMVideo;
import com.umeng.socialize.media.UMWebPage;
import com.umeng.socialize.sso.QZoneSsoHandler;
import com.umeng.socialize.sso.RenrenSsoHandler;
import com.umeng.socialize.sso.SinaSsoHandler;
import com.umeng.socialize.sso.UMQQSsoHandler;
import com.umeng.socialize.weixin.controller.UMWXHandler;
import com.umeng.socialize.weixin.media.CircleShareContent;

public class SocialUtils {
    /**
     * 分享网页
     * @param context
     * @param title
     * @param description
     * @param link
     * @param imageUrl
     */
    public static void shareLink(Activity context, String title, String description, String link,
            String imageUrl) {
        
        if(TextUtils.isEmpty(title)||TextUtils.isEmpty(link)){
            Utils.showShortToast(context, "分享参数异常");
            return;
        }
        
        final UMSocialService mController = UMServiceFactory.getUMSocialService("com.umeng.share"); 
        
        // 添加微信平台
        UMWXHandler wxHandler = new UMWXHandler(context,Constants.APP_ID_WEIXIN,Constants.APP_SECRET_WEIXIN);
        wxHandler.addToSocialSDK();
        // 支持微信朋友圈
        UMWXHandler wxCircleHandler = new UMWXHandler(context,Constants.APP_ID_WEIXIN,Constants.APP_SECRET_WEIXIN);
        wxCircleHandler.setToCircle(true);
        wxCircleHandler.addToSocialSDK();  
        
        //添加QQ在分享列表页中
        UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler(context, Constants.APP_ID_QQ,Constants.APP_KEY_QQ);
        qqSsoHandler.addToSocialSDK();  
        
        //添加Qzone
        QZoneSsoHandler qZoneSsoHandler = new QZoneSsoHandler(context, Constants.APP_ID_QQ, Constants.APP_KEY_QQ);
        qZoneSsoHandler.addToSocialSDK();        
        mController.getConfig().setSsoHandler(qZoneSsoHandler);
        
        //添加新浪SSO
        mController.getConfig().setSsoHandler(new SinaSsoHandler());
        //添加人人网SSO授权功能       
        RenrenSsoHandler renrenSsoHandler = new RenrenSsoHandler(context, Constants.APP_ID_RENREN,
                Constants.APP_KEY_RENREN, Constants.APP_SECRET_RENREN);
        mController.getConfig().setSsoHandler(renrenSsoHandler);
        
        CircleShareContent circleMedia = new CircleShareContent();
        circleMedia.setTitle(title);
        circleMedia.setShareContent(description);
        circleMedia.setShareImage(new UMImage(context, imageUrl));
        circleMedia.setTargetUrl(link);
        mController.setShareMedia(circleMedia);
//        mController.directShare(context, SHARE_MEDIA.WEIXIN_CIRCLE,null);
        
        QZoneShareContent qZoneShareContent=new QZoneShareContent(title);
        qZoneShareContent.setShareContent(description);
        qZoneShareContent.setShareImage(new UMImage(context, imageUrl));
        qZoneShareContent.setTargetUrl(link);
        mController.setShareMedia(qZoneShareContent);        
        
        mController.getConfig().setPlatforms(SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE,
                SHARE_MEDIA.QZONE,SHARE_MEDIA.QQ, SHARE_MEDIA.SINA,SHARE_MEDIA.RENREN);
        mController.getConfig().setPlatformOrder(SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE,
                SHARE_MEDIA.QZONE,SHARE_MEDIA.QQ, SHARE_MEDIA.SINA,SHARE_MEDIA.RENREN);
        mController.openShare(context, false);
        
//        UMWebPage content=new UMWebPage(title);
//        content.setTitle(title);
//        content.setDescription(description);
//        content.setTargetUrl(link);
//        content.setThumb(imageUrl);
//        mController.setShareMedia(content);
//        mController.directShare(context, SHARE_MEDIA.WEIXIN_CIRCLE, new SnsPostListener() {
//          
//          @Override
//          public void onStart() {
//          }
//          
//          @Override
//          public void onComplete(SHARE_MEDIA arg0, int arg1, SocializeEntity arg2) {
//          }
//      });
    }

    /**
     * 分享图片
     * 
     * @param context
     * @param title
     * @param picDescription
     *            图片描述
     * @param imageUrl
     */
    public static void sharePic(Activity context, String title, String picDescription, String imageUrl) {
        MobclickAgent.onEvent(context, Stat.EVENT_SHARE);

        String content = title;
        if (!title.equalsIgnoreCase(picDescription))
            content = title + "-" + picDescription;

        final UMSocialService mController = UMServiceFactory.getUMSocialService("com.umeng.share");
        // 微信图文分享必须设置一个url 
        String contentUrl = imageUrl;
     
        // 添加微信平台
        UMWXHandler wxHandler = new UMWXHandler(context,Constants.APP_ID_WEIXIN,Constants.APP_SECRET_WEIXIN);
        wxHandler.addToSocialSDK();
        // 支持微信朋友圈
        UMWXHandler wxCircleHandler = new UMWXHandler(context,Constants.APP_ID_WEIXIN,Constants.APP_SECRET_WEIXIN);
        wxCircleHandler.setToCircle(true);
        wxCircleHandler.addToSocialSDK();
        
        //添加QQ在分享列表页中
        UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler(context, Constants.APP_ID_QQ,Constants.APP_KEY_QQ);
        qqSsoHandler.addToSocialSDK();  
        
        //添加Qzone
        QZoneSsoHandler qZoneSsoHandler = new QZoneSsoHandler(context, Constants.APP_ID_QQ, Constants.APP_KEY_QQ);
        qZoneSsoHandler.addToSocialSDK();        
        mController.getConfig().setSsoHandler(qZoneSsoHandler);
        
        //添加新浪SSO
        mController.getConfig().setSsoHandler(new SinaSsoHandler());
        //添加人人网SSO授权功能       
        RenrenSsoHandler renrenSsoHandler = new RenrenSsoHandler(context, Constants.APP_ID_RENREN,
                Constants.APP_KEY_RENREN, Constants.APP_SECRET_RENREN);
        mController.getConfig().setSsoHandler(renrenSsoHandler);
        
        //设置分享内容
        mController.setShareContent(content);
        //设置分享图片, 参数2为图片的url地址
        mController.setShareMedia(new UMImage(context, imageUrl));

        //人人网分享时，如果不设置website，点击¨应用名称¨或者¨图片¨将跳转到人人主页；如果设置website将跳转到此website的页面
        mController.setAppWebSite(SHARE_MEDIA.RENREN, "http://hehedream.duapp.com/"); 

        //mController.getConfig().removePlatform(SHARE_MEDIA.DOUBAN,SHARE_MEDIA.EMAIL,SHARE_MEDIA.SMS);

        mController.getConfig().setPlatforms(SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE,
                SHARE_MEDIA.QZONE,SHARE_MEDIA.QQ, SHARE_MEDIA.SINA,SHARE_MEDIA.RENREN);
        mController.getConfig().setPlatformOrder(SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE,
                SHARE_MEDIA.QZONE,SHARE_MEDIA.QQ, SHARE_MEDIA.SINA,SHARE_MEDIA.RENREN);
        mController.openShare(context, false);
    }

    public static void shareVideo(Activity context, String title, String thumbUrl, String videoUrl) {
        final UMSocialService mController = UMServiceFactory.getUMSocialService("com.umeng.share");
        
        // 微信图文分享必须设置一个url 
        String contentUrl = videoUrl;

        // 添加微信平台
        UMWXHandler wxHandler = new UMWXHandler(context,Constants.APP_ID_WEIXIN,Constants.APP_SECRET_WEIXIN);
        wxHandler.addToSocialSDK();
        // 支持微信朋友圈
        UMWXHandler wxCircleHandler = new UMWXHandler(context,Constants.APP_ID_WEIXIN,Constants.APP_SECRET_WEIXIN);
        wxCircleHandler.setToCircle(true);
        wxCircleHandler.addToSocialSDK();
        
        //添加QQ在分享列表页中
        UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler(context, Constants.APP_ID_QQ,Constants.APP_KEY_QQ);
        qqSsoHandler.addToSocialSDK();  
        
        //添加Qzone
        QZoneSsoHandler qZoneSsoHandler = new QZoneSsoHandler(context, Constants.APP_ID_QQ, Constants.APP_KEY_QQ);
        qZoneSsoHandler.addToSocialSDK();        
        mController.getConfig().setSsoHandler(qZoneSsoHandler);
        
        //添加新浪SSO
        mController.getConfig().setSsoHandler(new SinaSsoHandler());
        //添加人人网SSO授权功能       
        RenrenSsoHandler renrenSsoHandler = new RenrenSsoHandler(context, Constants.APP_ID_RENREN,
                Constants.APP_KEY_RENREN, Constants.APP_SECRET_RENREN);
        mController.getConfig().setSsoHandler(renrenSsoHandler);
        
        //设置分享内容
        mController.setShareContent(title);

        // 设置分享视频
        UMVideo umVideo = new UMVideo(videoUrl);
        // 设置视频缩略图
        umVideo.setThumb(thumbUrl);
        umVideo.setTitle(title);
        mController.setShareMedia(umVideo);

        //人人网分享时，如果不设置website，点击¨应用名称¨或者¨图片¨将跳转到人人主页；如果设置website将跳转到此website的页面
        mController.setAppWebSite(SHARE_MEDIA.RENREN, "http://hehedream.duapp.com/");

        //mController.getConfig().removePlatform(SHARE_MEDIA.DOUBAN,SHARE_MEDIA.EMAIL,SHARE_MEDIA.SMS);

        mController.getConfig().setPlatforms(SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE,
                SHARE_MEDIA.QZONE,SHARE_MEDIA.QQ, SHARE_MEDIA.SINA,SHARE_MEDIA.RENREN);
        mController.getConfig().setPlatformOrder(SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE,
                SHARE_MEDIA.QZONE,SHARE_MEDIA.QQ, SHARE_MEDIA.SINA,SHARE_MEDIA.RENREN);
        
        mController.openShare(context, false);
    }

    public static void shareBlog(Activity context, String title, String summary, String webUrl) {
        final UMSocialService mController = UMServiceFactory.getUMSocialService("com.umeng.share");
        
        // 微信图文分享必须设置一个url 
        String contentUrl = webUrl;
     
        // 添加微信平台
        UMWXHandler wxHandler = new UMWXHandler(context,Constants.APP_ID_WEIXIN,Constants.APP_SECRET_WEIXIN);
        wxHandler.addToSocialSDK();
        // 支持微信朋友圈
        UMWXHandler wxCircleHandler = new UMWXHandler(context,Constants.APP_ID_WEIXIN,Constants.APP_SECRET_WEIXIN);
        wxCircleHandler.setToCircle(true);
        wxCircleHandler.addToSocialSDK();
        
        //添加QQ在分享列表页中
        UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler(context, Constants.APP_ID_QQ,Constants.APP_KEY_QQ);
        qqSsoHandler.addToSocialSDK();  
        
        //添加Qzone
        QZoneSsoHandler qZoneSsoHandler = new QZoneSsoHandler(context, Constants.APP_ID_QQ, Constants.APP_KEY_QQ);
        qZoneSsoHandler.addToSocialSDK();        
        mController.getConfig().setSsoHandler(qZoneSsoHandler);
        
        //添加新浪SSO
        mController.getConfig().setSsoHandler(new SinaSsoHandler());
        //添加人人网SSO授权功能       
        RenrenSsoHandler renrenSsoHandler = new RenrenSsoHandler(context, Constants.APP_ID_RENREN,
                Constants.APP_KEY_RENREN, Constants.APP_SECRET_RENREN);
        mController.getConfig().setSsoHandler(renrenSsoHandler);
        
        //设置分享内容
        mController.setShareContent(title);

        // 设置分享视频
        UMWebPage umObj = new UMWebPage(webUrl);
        //umObj.setTargetUrl(arg0)
        mController.setShareMedia(umObj);

        //人人网分享时，如果不设置website，点击¨应用名称¨或者¨图片¨将跳转到人人主页；如果设置website将跳转到此website的页面
        mController.setAppWebSite(SHARE_MEDIA.RENREN, "http://hehedream.duapp.com/");

        //mController.getConfig().removePlatform(SHARE_MEDIA.DOUBAN,SHARE_MEDIA.EMAIL,SHARE_MEDIA.SMS);

        mController.getConfig().setPlatforms(SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE,
                SHARE_MEDIA.QZONE,SHARE_MEDIA.QQ, SHARE_MEDIA.SINA,SHARE_MEDIA.RENREN);
        mController.getConfig().setPlatformOrder(SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE,
                SHARE_MEDIA.QZONE,SHARE_MEDIA.QQ, SHARE_MEDIA.SINA,SHARE_MEDIA.RENREN);
        
        mController.openShare(context, false);
    }

}
