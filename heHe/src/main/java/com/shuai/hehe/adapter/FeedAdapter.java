package com.shuai.hehe.adapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.shuai.base.view.FlipImageView;
import com.shuai.base.view.FlipImageView.OnFlipListener;
import com.shuai.hehe.HeHeApplication;
import com.shuai.hehe.R;
import com.shuai.hehe.data.AlbumFeed;
import com.shuai.hehe.data.BlogFeed;
import com.shuai.hehe.data.Constants;
import com.shuai.hehe.data.DataManager;
import com.shuai.hehe.data.Feed;
import com.shuai.hehe.data.FeedType;
import com.shuai.hehe.data.VideoFeed;
import com.shuai.hehe.protocol.HideFeedRequest;
import com.shuai.hehe.protocol.ProtocolError;
import com.shuai.hehe.protocol.UrlHelper;
import com.shuai.hehe.ui.AlbumActivity;
import com.shuai.hehe.ui.BlogActivity;
import com.shuai.utils.SocialUtils;

public class FeedAdapter extends ArrayAdapter<Feed> {
    private Context mContext;
    private FeedList mFeeds;
    private LayoutInflater mInflater;
    private DisplayImageOptions mDisplayImageOptions;
    private ImageLoadingListener mImageLoadingListener;
    private int mLastPosition=-1;
    private DataManager mDataManager=DataManager.getInstance();
    //缓存item高度，防止上下滑动的时候出现闪动
    private Map<String, Integer> mHeightCache=new HashMap<String,Integer>();
    /**
     * 异步请求队列
     */
    private RequestQueue mRequestQueue;

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
    
    private class HideFeedListener implements OnLongClickListener{
        Feed mFeed;
        
        HideFeedListener(Feed info){
            mFeed=info;
        }

        @Override
        public boolean onLongClick(View v) {
            AlertDialog dialog = new AlertDialog.Builder(mContext).setMessage(R.string.hide_feed).setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    HideFeedRequest request=new HideFeedRequest(mContext,mFeed.getId(), new Listener<String>() {

                        @Override
                        public void onResponse(String response) {
                            Toast.makeText(mContext, R.string.hide_feed_success, Toast.LENGTH_SHORT).show();
                            remove(mFeed);
                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(mContext, ProtocolError.getErrorMessage(mContext, error), Toast.LENGTH_SHORT).show();
                        }
                    });
                    
                    mRequestQueue.add(request);
                }
            }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }).create();
            dialog.setCanceledOnTouchOutside(true);
            dialog.show();
            return true;
        }
    }

    public FeedAdapter(Context context, FeedList objects) {
        super(context, 0, objects);
        mContext = context;
        mFeeds = objects;
        mInflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mRequestQueue=HeHeApplication.getRequestQueue();
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
                //如果记录过item的展示高度，直接设置其高度，防止加载完成后高度变化导致闪动
                Integer height=mHeightCache.get(imageUri);
                if(height!=null){
                    LayoutParams layoutParams = view.getLayoutParams();
                    layoutParams.height=height;
                    view.setLayoutParams(layoutParams);
                }
            }
            
            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                
            }
            
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                //修改imageview的高度，使上下不出现空白
                final float RATIO=1.0f;
                LayoutParams layoutParams = view.getLayoutParams();
                int viewWidth = layoutParams.width;
                int oldHeight = layoutParams.height;            

                int newHeight;
                if (((double)loadedImage.getWidth()) / loadedImage.getHeight() > RATIO) {
                    newHeight=(int) (loadedImage.getHeight() *viewWidth/ (double)loadedImage.getWidth());
                }else{
                    newHeight=(int) (viewWidth/RATIO);
                }
                
                if(oldHeight!=newHeight){
                    layoutParams.height=newHeight;
                    view.setLayoutParams(layoutParams);
                }
                
                if(!mHeightCache.containsKey(imageUri)){
                    mHeightCache.put(imageUri, newHeight);
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
        case FeedType.TYPE_BLOG:
            type=2;
            break;
        }

        return type;
    }

    @Override
    public int getViewTypeCount() {
        return 3;
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
        case FeedType.TYPE_BLOG:
            view=getBlogView(feed,position,convertView,parent);
            break;
        default:
            break;
        }
        
//        if(view!=null && position>mLastPosition){
//            AnimatorSet animator = (AnimatorSet) AnimatorInflater.loadAnimator(mContext, R.animator.feed_item_in);
//            animator.setTarget(view);
//            animator.start();
//        }
        
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
                mDataManager.addStarFeed(info);
                Toast.makeText(mContext, R.string.add_star_tip, Toast.LENGTH_SHORT).show();
            }else{
                mDataManager.removeStarFeed(info.getId());
                Toast.makeText(mContext, R.string.remove_star_tip, Toast.LENGTH_SHORT).show();
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
        
        LinearLayout mLlFeedContainer;
 
    }
    
    class BlogViewHolder extends BaseHolder{
        /**
         * 标题
         */
        TextView mTvTitle;
        
        /**
         * 日志摘要
         */
        TextView mTvSummary;
        
        LinearLayout mLlFeedContainer;
    }

    private View getVideoView(Feed feed, int position, View convertView, ViewGroup parent) {
        final VideoFeed info=(VideoFeed) feed;
        View view=convertView;
        VideoViewHolder holder;
        if(view==null){
            view=mInflater.inflate(R.layout.feed_video, parent, false);
            
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
//                Intent intent=new Intent(mContext, VideoActivity.class);
//                intent.putExtra(Constants.VIDEO_TITLE, info.getTitle());
//                intent.putExtra(Constants.WEB_VIDEO_URL, info.getWebVideoUrl());
//                mContext.startActivity(intent);
            }
        });
        
        boolean isStarred=mDataManager.isStarFeed(feed.getId());
        holder.mFivStar.setFlipped(isStarred, false);
        holder.mFivStar.setOnFlipListener(new StarChangeListener(info));
        
        holder.mIvShare.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                SocialUtils.shareVideo((Activity) mContext,info.getTitle(),info.getThumbImgUrl(),info.getWebVideoUrl());
            }
            
        });
        
        if (mDataManager.isAdmin()) {
            holder.mIvThumb.setOnLongClickListener(new HideFeedListener(info));
        }     
        return view;
    }

    private View getAlbumView(Feed feed, int position, View convertView, ViewGroup parent) {
        final AlbumFeed info=(AlbumFeed) feed;
        View view=convertView;
        AlbumViewHolder holder;
        if(view==null){
            view=mInflater.inflate(R.layout.feed_album, parent, false);
            
            holder=new AlbumViewHolder();
            holder.mTvTitle=(TextView) view.findViewById(R.id.tv_title);
            holder.mIvThumb=(ImageView) view.findViewById(R.id.iv_thumb);
            holder.mFivStar=(FlipImageView) view.findViewById(R.id.fiv_star);
            holder.mIvShare=(ImageView) view.findViewById(R.id.iv_share);
            holder.mLlFeedContainer=(LinearLayout) view.findViewById(R.id.ll_feed_container);
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
                intent.putExtra(Constants.FEED_ALBUM, info);
                mContext.startActivity(intent);
            }
        });
        
        boolean isStarred=mDataManager.isStarFeed(feed.getId());
        holder.mFivStar.setFlipped(isStarred, false);
        holder.mFivStar.setOnFlipListener(new StarChangeListener(info));
        
        holder.mIvShare.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                SocialUtils.sharePic((Activity) mContext, info.getTitle(), "", info.getBigImgUrl());
            }
        });
        
        if (mDataManager.isAdmin()) {
            holder.mIvThumb.setOnLongClickListener(new HideFeedListener(info));
        }
        return view;
    }
    
    private View getBlogView(Feed feed, int position, View convertView, ViewGroup parent) {
        final BlogFeed info=(BlogFeed) feed;
        View view=convertView;
        BlogViewHolder holder;
        if(view==null){
            view=mInflater.inflate(R.layout.feed_blog, parent, false);
            
            holder=new BlogViewHolder();
            holder.mTvTitle=(TextView) view.findViewById(R.id.tv_title);
            holder.mTvSummary=(TextView) view.findViewById(R.id.tv_summary);
            holder.mFivStar=(FlipImageView) view.findViewById(R.id.fiv_star);
            holder.mIvShare=(ImageView) view.findViewById(R.id.iv_share);
            holder.mLlFeedContainer=(LinearLayout) view.findViewById(R.id.ll_feed_container);
            view.setTag(holder);
        }else{
            holder=(BlogViewHolder) view.getTag();
        }
        
        holder.feed=feed;
        holder.mTvTitle.setText(info.getTitle());
        holder.mTvSummary.setText(info.getSummary());
        
        holder.mLlFeedContainer.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(mContext, BlogActivity.class);
                intent.putExtra(Constants.FEED_BLOG, info);
                mContext.startActivity(intent);
            }
        });
        
        boolean isStarred=mDataManager.isStarFeed(feed.getId());
        holder.mFivStar.setFlipped(isStarred, false);
        holder.mFivStar.setOnFlipListener(new StarChangeListener(info));
        
        holder.mIvShare.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                SocialUtils.shareBlog((Activity) mContext, info.getTitle(), info.getSummary(), UrlHelper.getBlogUrl(mContext,info.getId(), true));
            }
        });
        
        if (mDataManager.isAdmin()) {
            holder.mLlFeedContainer.setOnLongClickListener(new HideFeedListener(info));
        }
        return view;
    }

}
