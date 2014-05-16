package com.shuai.hehe.adapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nineoldandroids.animation.AnimatorInflater;
import com.nineoldandroids.animation.AnimatorSet;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.shuai.base.view.FlipImageView;
import com.shuai.base.view.FlipImageView.OnFlipListener;
import com.shuai.hehe.R;
import com.shuai.hehe.data.AlbumFeed;
import com.shuai.hehe.data.Constants;
import com.shuai.hehe.data.DataManager;
import com.shuai.hehe.data.Feed;
import com.shuai.hehe.data.FeedType;
import com.shuai.hehe.data.Stat;
import com.shuai.hehe.data.VideoFeed;
import com.shuai.hehe.ui.AlbumActivity;
import com.shuai.hehe.ui.WebViewActivity;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.bean.CustomPlatform;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.controller.RequestType;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners.OnCustomPlatformClickListener;
import com.umeng.socialize.controller.listener.SocializeListeners.SnsPostListener;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.sso.QZoneSsoHandler;
import com.umeng.socialize.sso.SinaSsoHandler;
import com.umeng.socialize.sso.UMWXHandler;

public class FeedAdapter extends ArrayAdapter<Feed> {
    private Context mContext;
    private FeedList mFeeds;
    private LayoutInflater mInflater;
    private DisplayImageOptions mDisplayImageOptions;
    private ImageLoadingListener mImageLoadingListener;
    private int mLastPosition=-1;
    private DataManager mDataManager=DataManager.getInstance();

    public static class FeedList extends ArrayList<Feed> {
        //用来快速检测对象是否已存在
        private HashMap<Long, Feed> mKeys = new HashMap<Long, Feed>();

        private boolean exist(Feed object) {
            if (mKeys.get(object.getId()) == null)
                return false;
            else
                return true;
        }

        @Override
        public boolean add(Feed object) {
            if (exist(object)) {
                return false;
            }

            mKeys.put(object.getId(), object);
            return super.add(object);
        }

        @Override
        public void add(int index, Feed object) {
            if (exist(object)) {
                return;
            }

            mKeys.put(object.getId(), object);
            super.add(index, object);
        }

        @Override
        public boolean addAll(Collection<? extends Feed> collection) {
            return addAll(size(), collection);

        }

        @Override
        public boolean addAll(int index, Collection<? extends Feed> collection) {
            Feed[] datas = collection.toArray(new Feed[0]);
            //倒序添加，保证结果顺序和collection一致
            for (int i = datas.length - 1; i >= 0; i--) {
                Feed info = datas[i];
                add(index, info);
            }

            return true;
        }

        @Override
        public void clear() {
            super.clear();
            mKeys.clear();
        }

        @Override
        public Feed remove(int index) {
            Feed info = super.remove(index);
            mKeys.remove(info.getId());
            return info;
        }

        @Override
        public boolean remove(Object object) {
            mKeys.remove(((Feed) object).getId());
            return super.remove(object);
        }

    }

    public FeedAdapter(Context context, FeedList objects) {
        super(context, 0, objects);
        mContext = context;
        mFeeds = objects;
        mInflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        init();
    }
    
    private void init(){
        mDisplayImageOptions=new DisplayImageOptions.Builder()
            .cacheInMemory(true)
            .cacheOnDisc(true)
            .showImageOnLoading(new ColorDrawable(0xffcccccc))
            .showImageOnFail(R.drawable.ic_image_load_failed)
            .build();
        
        mImageLoadingListener=new ImageLoadingListener() {
            
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                
            }
            
            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                // TODO Auto-generated method stub
                
            }
            
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                // TODO Auto-generated method stub
                LayoutParams layoutParams = view.getLayoutParams();
                int viewWidth = MeasureSpec.getSize(layoutParams.width);
                int viewHeight = MeasureSpec.getSize(layoutParams.height);
                
                if (((double)loadedImage.getWidth()) / loadedImage.getHeight() > ((double)viewWidth) / viewHeight) {
                    int newHeight=loadedImage.getHeight() *viewWidth/ loadedImage.getWidth();
                    int mode=MeasureSpec.getMode(viewHeight);
                    layoutParams.height=MeasureSpec.makeMeasureSpec(newHeight, mode);
                    view.setLayoutParams(layoutParams);
                    view.requestLayout();
                }              

            }
            
            @Override
            public void onLoadingCancelled(String imageUri, View view) {
                // TODO Auto-generated method stub
                
            }
        };
        
    }

    @Override
    public int getItemViewType(int position) {
        int type = IGNORE_ITEM_VIEW_TYPE;
        Feed item = getItem(position);
        switch (item.getType()) {
        case FeedType.TYPE_ALBUM:
            type = 0;
            break;
        case FeedType.TYPE_VIDEO:
            type = 1;
            break;
        }

        return type;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view=null;
        
        Feed feed=getItem(position);
        int type=feed.getType();
        switch (type) {
        case FeedType.TYPE_ALBUM:
            view=getAlbumView(feed,position,convertView,parent);
            break;
        case FeedType.TYPE_VIDEO:
            view=getVideoView(feed,position,convertView,parent);
            break;
        default:
            break;
        }
        
        if(view!=null && position>mLastPosition){
            AnimatorSet animator = (AnimatorSet) AnimatorInflater.loadAnimator(mContext, R.animator.feed_item_in);
            animator.setTarget(view);
            animator.start();
        }
        
        mLastPosition=position;
        return view;
    }
    
    /**
     * 新鲜事的收藏状态发生了改变，更新界面
     * @param parentView 父控件(比如ListView，遍历其子控件并更新相应子控件的收藏状态)
     */
    public void updateStarFeedState(ViewGroup parentView, long feedId) {
        for (int i = 0; i < parentView.getChildCount(); i++) {
            View view = parentView.getChildAt(i);
            Object tag = view.getTag();
            if (tag != null && tag instanceof BaseHolder) {
                BaseHolder holder = (BaseHolder) tag;
                if (holder.feed.getId() == feedId) {
                    updateStarFeedState(holder.mFivStar, holder.feed);
                    break;
                }
            }
        }
    }

    private void updateStarFeedState(FlipImageView starView, Feed feed) {
        if (mDataManager.isStarFeed(feed.getId())) {
            starView.setFlipped(true, false);
        } else {
            starView.setFlipped(false, false);
        }
    }
    
    class StarChangeListener implements OnFlipListener{
        Feed info;
        
        StarChangeListener(Feed feed){
            info=feed;
        }        

        @Override
        public void onClick(FlipImageView view) {
            boolean isStarred=view.isFlipped();
            if(isStarred){
                MobclickAgent.onEvent(mContext, Stat.EVENT_STAR);
                mDataManager.addStarFeed(info);
            }else{
                MobclickAgent.onEvent(mContext, Stat.EVENT_UNSTAR);
                mDataManager.removeStarFeed(info.getId());
            }
        }

        @Override
        public void onFlipStart(FlipImageView view) {
        }

        @Override
        public void onFlipEnd(FlipImageView view) {
            
        }
        
    }
    
    class BaseHolder{
        Feed feed;
        /**
         * 收藏按钮
         */
        FlipImageView mFivStar;
        /**
         * 转发按钮
         */
        ImageView mIvShare;
    }
    
    class VideoViewHolder extends BaseHolder{
        /**
         * 标题
         */
        TextView mTvTitle;
        /**
         * 缩略图
         */
        ImageView mIvThumb;
    }
    
    class AlbumViewHolder extends BaseHolder{
        /**
         * 标题
         */
        TextView mTvTitle;
        /**
         * 缩略图
         */
        ImageView mIvThumb;
 
    }

    private View getVideoView(Feed feed, int position, View convertView, ViewGroup parent) {
        final VideoFeed info=(VideoFeed) feed;
        View view=convertView;
        VideoViewHolder holder;
        if(view==null){
            view=mInflater.inflate(R.layout.video_feed_item, parent, false);
            
            holder=new VideoViewHolder();
            holder.mTvTitle=(TextView) view.findViewById(R.id.tv_title);
            holder.mIvThumb=(ImageView) view.findViewById(R.id.iv_thumb);
            holder.mFivStar=(FlipImageView) view.findViewById(R.id.fiv_star);
            holder.mIvShare=(ImageView) view.findViewById(R.id.iv_share);
            view.setTag(holder);
        }else{
            holder=(VideoViewHolder) view.getTag();
        }
        
        holder.feed=feed;
        holder.mTvTitle.setText(info.getTitle());
        ImageLoader.getInstance().displayImage(info.getThumbImgUrl(), holder.mIvThumb,mDisplayImageOptions,mImageLoadingListener);
        
        holder.mIvThumb.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(mContext, WebViewActivity.class);
                //Intent intent=new Intent(mContext, VideoActivity.class);
                intent.putExtra(Constants.VIDEO_URL, info.getWebVideoUrl());
                mContext.startActivity(intent);
            }
        });
        
        boolean isStarred=mDataManager.isStarFeed(feed.getId());
        holder.mFivStar.setFlipped(isStarred, false);
        holder.mFivStar.setOnFlipListener(new StarChangeListener(info));
        return view;
    }

    private View getAlbumView(Feed feed, int position, View convertView, ViewGroup parent) {
        final AlbumFeed info=(AlbumFeed) feed;
        View view=convertView;
        AlbumViewHolder holder;
        if(view==null){
            view=mInflater.inflate(R.layout.album_feed_item, parent, false);
            
            holder=new AlbumViewHolder();
            holder.mTvTitle=(TextView) view.findViewById(R.id.tv_title);
            holder.mIvThumb=(ImageView) view.findViewById(R.id.iv_thumb);
            holder.mFivStar=(FlipImageView) view.findViewById(R.id.fiv_star);
            holder.mIvShare=(ImageView) view.findViewById(R.id.iv_share);
            view.setTag(holder);
        }else{
            holder=(AlbumViewHolder) view.getTag();
        }
        
        holder.feed=feed;
        holder.mTvTitle.setText(info.getTitle());
        ImageLoader.getInstance().displayImage(info.getBigImgUrl(), holder.mIvThumb,mDisplayImageOptions,mImageLoadingListener);
        
        holder.mIvThumb.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(mContext, AlbumActivity.class);
                intent.putExtra(Constants.FEED_ID, info.getId());
                mContext.startActivity(intent);
            }
        });
        
        boolean isStarred=mDataManager.isStarFeed(feed.getId());
        holder.mFivStar.setFlipped(isStarred, false);
        holder.mFivStar.setOnFlipListener(new StarChangeListener(info));
        
        holder.mIvShare.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                MobclickAgent.onEvent(mContext, Stat.EVENT_SHARE);
                
                final UMSocialService mController = UMServiceFactory.getUMSocialService("com.umeng.share",
                        RequestType.SOCIAL);
                
                // 微信图文分享必须设置一个url 
                String contentUrl = info.getBigImgUrl();
                // 添加微信平台，参数1为当前Activity, 参数2为用户申请的AppID, 参数3为点击分享内容跳转到的目标url
                UMWXHandler wxHandler = mController.getConfig().supportWXPlatform((Activity) mContext,Constants.APP_ID_WEIXIN, contentUrl);
                wxHandler.setWXTitle(info.getTitle());
                // 支持微信朋友圈
                UMWXHandler circleHandler = mController.getConfig().supportWXCirclePlatform((Activity) mContext,Constants.APP_ID_WEIXIN, contentUrl) ;
                circleHandler.setCircleTitle(info.getTitle());
                circleHandler.setListener(new OnCustomPlatformClickListener() {
                    
                    @Override
                    public void onClick(CustomPlatform arg0, SocializeEntity arg1, SnsPostListener arg2) {
                    }
                });
                
                //设置分享内容
                mController.setShareContent(info.getTitle());
                //设置分享图片, 参数2为图片的url地址
                mController.setShareMedia(new UMImage(mContext,info.getBigImgUrl()));
                
                mController.getConfig().setSsoHandler( new QZoneSsoHandler((Activity) mContext,Constants.APP_ID_QQ) );
                mController.getConfig().setSsoHandler(new SinaSsoHandler());
                
                //mController.getConfig().removePlatform(SHARE_MEDIA.DOUBAN,SHARE_MEDIA.EMAIL,SHARE_MEDIA.SMS);
                
                mController.getConfig().setPlatforms(SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE,
                        SHARE_MEDIA.QZONE,SHARE_MEDIA.QQ, SHARE_MEDIA.SINA);
                mController.getConfig().setPlatformOrder(SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE,
                        SHARE_MEDIA.QZONE,SHARE_MEDIA.QQ, SHARE_MEDIA.SINA);
                
//                mController.getConfig().setPlatforms(SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE,
//                        SHARE_MEDIA.QZONE,SHARE_MEDIA.QQ, SHARE_MEDIA.SINA,SHARE_MEDIA.RENREN);
//                mController.getConfig().setPlatformOrder(SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE,
//                        SHARE_MEDIA.QZONE,SHARE_MEDIA.QQ, SHARE_MEDIA.SINA,SHARE_MEDIA.RENREN);
                mController.openShare((Activity) mContext, false);
            }
        });
        return view;
    }
    
    

}
