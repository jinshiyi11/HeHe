package com.shuai.utils;

import android.app.Activity;

import com.shuai.hehe.data.Constants;
import com.shuai.hehe.data.Stat;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.controller.RequestType;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMVideo;
import com.umeng.socialize.sso.QZoneSsoHandler;
import com.umeng.socialize.sso.SinaSsoHandler;
import com.umeng.socialize.sso.UMWXHandler;

public class SocialUtils {

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

        final UMSocialService mController = UMServiceFactory.getUMSocialService("com.umeng.share", RequestType.SOCIAL);
        // 微信图文分享必须设置一个url 
        String contentUrl = imageUrl;
        // 添加微信平台，参数1为当前Activity, 参数2为用户申请的AppID, 参数3为点击分享内容跳转到的目标url
        UMWXHandler wxHandler = mController.getConfig().supportWXPlatform(context,Constants.APP_ID_WEIXIN, contentUrl);
        wxHandler.setWXTitle(content);
        // 支持微信朋友圈
        UMWXHandler circleHandler = mController.getConfig().supportWXCirclePlatform(context,Constants.APP_ID_WEIXIN, contentUrl) ;
        circleHandler.setCircleTitle(content);
        
        //设置分享内容
        mController.setShareContent(content);
        //设置分享图片, 参数2为图片的url地址
        mController.setShareMedia(new UMImage(context, imageUrl));

        mController.getConfig().setSsoHandler(new QZoneSsoHandler(context, Constants.APP_ID_QQ));
        mController.getConfig().setSsoHandler(new SinaSsoHandler());

        //mController.getConfig().removePlatform(SHARE_MEDIA.DOUBAN,SHARE_MEDIA.EMAIL,SHARE_MEDIA.SMS);

        mController.getConfig().setPlatforms(SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE, SHARE_MEDIA.QZONE,
                SHARE_MEDIA.QQ, SHARE_MEDIA.SINA);
        mController.getConfig().setPlatformOrder(SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE, SHARE_MEDIA.QZONE,
                SHARE_MEDIA.QQ, SHARE_MEDIA.SINA);

        //        mController.getConfig().setPlatforms(SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE,
        //                SHARE_MEDIA.QZONE,SHARE_MEDIA.QQ, SHARE_MEDIA.SINA,SHARE_MEDIA.RENREN);
        //        mController.getConfig().setPlatformOrder(SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE,
        //                SHARE_MEDIA.QZONE,SHARE_MEDIA.QQ, SHARE_MEDIA.SINA,SHARE_MEDIA.RENREN);
        mController.openShare(context, false);
    }

    public static void shareVideo(Activity context, String title, String thumbUrl, String videoUrl) {
        final UMSocialService mController = UMServiceFactory.getUMSocialService("com.umeng.share", RequestType.SOCIAL);
        
        // 微信图文分享必须设置一个url 
        String contentUrl = videoUrl;
        // 添加微信平台，参数1为当前Activity, 参数2为用户申请的AppID, 参数3为点击分享内容跳转到的目标url
        UMWXHandler wxHandler = mController.getConfig().supportWXPlatform(context,Constants.APP_ID_WEIXIN, contentUrl);
        wxHandler.setWXTitle(title);
        // 支持微信朋友圈
        UMWXHandler circleHandler = mController.getConfig().supportWXCirclePlatform(context,Constants.APP_ID_WEIXIN, contentUrl) ;
        circleHandler.setCircleTitle(title);
        
        //设置分享内容
        mController.setShareContent(title);

        // 设置分享视频
        UMVideo umVideo = new UMVideo(videoUrl);
        // 设置视频缩略图
        umVideo.setThumb(thumbUrl);
        umVideo.setTitle(title);
        mController.setShareMedia(umVideo);

        mController.getConfig().setSsoHandler(new QZoneSsoHandler(context, Constants.APP_ID_QQ));
        mController.getConfig().setSsoHandler(new SinaSsoHandler());

        //mController.getConfig().removePlatform(SHARE_MEDIA.DOUBAN,SHARE_MEDIA.EMAIL,SHARE_MEDIA.SMS);

        mController.getConfig().setPlatforms(SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE, SHARE_MEDIA.QZONE,
                SHARE_MEDIA.QQ, SHARE_MEDIA.SINA);
        mController.getConfig().setPlatformOrder(SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE, SHARE_MEDIA.QZONE,
                SHARE_MEDIA.QQ, SHARE_MEDIA.SINA);

        //        mController.getConfig().setPlatforms(SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE,
        //                SHARE_MEDIA.QZONE,SHARE_MEDIA.QQ, SHARE_MEDIA.SINA,SHARE_MEDIA.RENREN);
        //        mController.getConfig().setPlatformOrder(SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE,
        //                SHARE_MEDIA.QZONE,SHARE_MEDIA.QQ, SHARE_MEDIA.SINA,SHARE_MEDIA.RENREN);
        mController.openShare(context, false);
    }

}
