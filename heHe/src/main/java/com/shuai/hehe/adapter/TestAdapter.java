package com.shuai.hehe.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.shuai.base.view.FlipImageView;
import com.shuai.hehe.GlideApp;
import com.shuai.hehe.HeHeApplication;
import com.shuai.hehe.R;
import com.shuai.hehe.data.AlbumFeed;
import com.shuai.hehe.data.BlogFeed;
import com.shuai.hehe.data.Constants;
import com.shuai.hehe.data.DataManager;
import com.shuai.hehe.data.Feed;
import com.shuai.hehe.data.FeedType;
import com.shuai.hehe.data.VideoDetail;
import com.shuai.hehe.data.VideoFeed;
import com.shuai.hehe.protocol.GetBlogInfoRequest;
import com.shuai.hehe.protocol.GetVideoDetailRequest;
import com.shuai.hehe.protocol.HideFeedRequest;
import com.shuai.hehe.protocol.ProtocolUtils;
import com.shuai.hehe.ui.AlbumActivity;
import com.shuai.hehe.ui.BlogActivity;
import com.shuai.utils.FeedUtil;
import com.shuai.utils.SocialUtils;
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;

import java.io.File;
import java.util.List;

/**
 *
 */
public class TestAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = TestAdapter.class.getSimpleName();
    private Context mContext;
    private List<Feed> mDatas;
    private LayoutInflater mInflater;
    private DataManager mDataManager = DataManager.getInstance();
    private RequestQueue mRequestQueue;

    private VideoViewHolder mPlayingVideoHolder;
    private int mPlayingVideoPosition = -1;

    public TestAdapter(Context context, List<Feed> feedList) {
        mContext = context;
        this.mDatas = feedList;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mRequestQueue = HeHeApplication.getRequestQueue();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        switch (viewType) {
            case FeedType.TYPE_VIDEO: {
                View view = mInflater.inflate(R.layout.feed_video, parent, false);
                viewHolder = new VideoViewHolder(view);
                break;
            }
            case FeedType.TYPE_ALBUM: {
                View view = mInflater.inflate(R.layout.feed_album, parent, false);
                viewHolder = new AlbumViewHolder(view);
                break;
            }
            case FeedType.TYPE_BLOG: {
                View view = mInflater.inflate(R.layout.feed_blog, parent, false);
                viewHolder = new BlogViewHolder(view);
                break;
            }
            default:
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Feed feed = mDatas.get(position);
        int type = feed.getType();
        switch (type) {
            case FeedType.TYPE_VIDEO:
                onBindVideoViewHolder((VideoViewHolder) holder, (VideoFeed) feed,position);
                break;
            case FeedType.TYPE_ALBUM:
                onBindAlbumViewHolder((AlbumViewHolder) holder, (AlbumFeed) feed);
                break;
            case FeedType.TYPE_BLOG:
                onBindBlogViewHolder((BlogViewHolder) holder, (BlogFeed) feed);
                break;
            default:
                break;
        }
    }

    private void onBindVideoViewHolder(final VideoViewHolder holder, final VideoFeed feed, final int position) {
        holder.feed = feed;
        holder.mTvTitle.setText(feed.getTitle());
        GlideApp.with(mContext)
                .load(feed.getThumbImgUrl())
                .error(R.drawable.ic_image_load_failed)
                .placeholder(new ColorDrawable(0xffcccccc))
                .into(holder.mIvThumb);

        holder.mIvThumb.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                resetPlayingVideo();
                mPlayingVideoHolder = holder;
                mPlayingVideoPosition=position;
                mInflater.inflate(R.layout.feed_video_player, holder.mRlFeedVideo, true);
                holder.mLoadingBar = (ProgressBar) holder.mRlFeedVideo.findViewById(R.id.pb_loading_video);
                holder.mVideoView = (StandardGSYVideoPlayer) holder.mRlFeedVideo.findViewById(R.id.vv_feed_video);
                //非全屏下，不显示title
                holder.mVideoView.getTitleTextView().setVisibility(View.GONE);
                //非全屏下不显示返回键
//                holder.mVideoView.getBackButton().setVisibility(View.GONE);
                //holder.mIvThumb.setVisibility(View.INVISIBLE);

                holder.mVideoView.setShowFullAnimation(false);
                holder.mVideoView.setRotateViewAuto(true);
                holder.mVideoView.setLockLand(true);
                holder.mVideoView.getFullscreenButton().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        holder.mVideoView.startWindowFullscreen(mContext, false, true);
                    }
                });
                getVideoDetail(holder, feed);
//                Intent intent=new Intent(mContext, VideoActivity.class);
//                intent.putExtra(Constants.VIDEO_TITLE, info.getTitle());
//                intent.putExtra(Constants.WEB_VIDEO_URL, info.getWebVideoUrl());
//                mContext.startActivity(intent);
            }
        });

        boolean isStarred = mDataManager.isStarFeed(feed.getId());
        holder.mFivStar.setFlipped(isStarred, false);
        holder.mFivStar.setOnFlipListener(new StarChangeListener(feed));

        holder.mIvShare.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                GlideApp.with(mContext)
                        .asFile()
                        .load(feed.getThumbImgUrl()).into(new SimpleTarget<File>() {
                    @Override
                    public void onResourceReady(File resource, Transition<? super File> transition) {
                        Log.d(TAG, "xx");
                    }
                });
                SocialUtils.shareVideo((Activity) mContext, feed.getTitle(), feed.getThumbImgUrl(), feed.getWebVideoUrl());
            }

        });

//        if (mDataManager.isAdmin()) {
//            holder.mIvThumb.setOnLongClickListener(new HideFeedListener(feed));
//        }
    }

    private void onBindAlbumViewHolder(AlbumViewHolder holder, final AlbumFeed feed) {
        holder.feed = feed;
        holder.mTvTitle.setText(feed.getTitle());
        GlideApp.with(mContext)
                .load(feed.getBigImgUrl())
                .error(R.drawable.ic_image_load_failed)
                .placeholder(new ColorDrawable(0xffcccccc))
                .into(holder.mIvThumb);

        holder.mIvThumb.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, AlbumActivity.class);
                intent.putExtra(Constants.FEED_ALBUM, feed);
                mContext.startActivity(intent);
            }
        });

        boolean isStarred = mDataManager.isStarFeed(feed.getId());
        holder.mFivStar.setFlipped(isStarred, false);
        holder.mFivStar.setOnFlipListener(new StarChangeListener(feed));

        holder.mIvShare.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                SocialUtils.sharePic((Activity) mContext, feed.getTitle(), "", feed.getBigImgUrl());
            }
        });

//        if (mDataManager.isAdmin()) {
//            holder.mIvThumb.setOnLongClickListener(new HideFeedListener(feed));
//        }
    }

    private void onBindBlogViewHolder(BlogViewHolder holder, final BlogFeed feed) {
        holder.feed = feed;
        holder.mTvTitle.setText(feed.getTitle());
        holder.mTvSummary.setText(feed.getSummary());

        holder.mLlFeedContainer.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, BlogActivity.class);
                intent.putExtra(Constants.FEED_BLOG, feed);
                mContext.startActivity(intent);
            }
        });

        boolean isStarred = mDataManager.isStarFeed(feed.getId());
        holder.mFivStar.setFlipped(isStarred, false);
        holder.mFivStar.setOnFlipListener(new StarChangeListener(feed));

        holder.mIvShare.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                SocialUtils.shareBlog((Activity) mContext, feed.getTitle(), feed.getSummary(),
                        GetBlogInfoRequest.getUrl(mContext, feed.getId(), true));
            }
        });

//        if (mDataManager.isAdmin()) {
//            holder.mLlFeedContainer.setOnLongClickListener(new HideFeedListener(feed));
//        }
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    @Override
    public int getItemViewType(int position) {
        return mDatas.get(position).getType();
    }

    public void onScrolled(int firstVisibleItemPosition, int lastVisibleItemPosition) {
        if (mPlayingVideoPosition < firstVisibleItemPosition
                || mPlayingVideoPosition > lastVisibleItemPosition) {
            resetPlayingVideo();
        }
    }

    private void resetPlayingVideo() {
        if (mPlayingVideoHolder == null) {
            return;
        }

        VideoViewHolder holder = mPlayingVideoHolder;
        mPlayingVideoHolder = null;
        mPlayingVideoPosition = -1;
        mRequestQueue.cancelAll(holder.feed);
        if (holder.mLoadingBar != null) {
            ((ViewGroup) holder.mLoadingBar.getParent()).removeView(holder.mLoadingBar);
            holder.mLoadingBar = null;
        }

        if (holder.mVideoView != null) {
            StandardGSYVideoPlayer.releaseAllVideos();
            //holder.mVideoView.stopPlayback();
            ((ViewGroup) holder.mVideoView.getParent()).removeView(holder.mVideoView);
            holder.mVideoView = null;
        }
    }

    /**
     * 新鲜事的收藏状态发生了改变，更新界面
     *
     * @param parentView 父控件(比如ListView，遍历其子控件并更新相应子控件的收藏状态)
     */
//    public void updateStarFeedState(ViewGroup parentView, long feedId) {
//        for (int i = 0; i < parentView.getChildCount(); i++) {
//            View view = parentView.getChildAt(i);
//            Object tag = view.getTag();
//            if (tag != null && tag instanceof BaseHolder) {
//                BaseHolder holder = (BaseHolder) tag;
//                if (holder.feed.getId() == feedId) {
//                    updateStarFeedState(holder.mFivStar, holder.feed);
//                    break;
//                }
//            }
//        }
//    }

//    private void updateStarFeedState(FlipImageView starView, Feed feed) {
//        if (mDataManager.isStarFeed(feed.getId())) {
//            starView.setFlipped(true, false);
//        } else {
//            starView.setFlipped(false, false);
//        }
//    }

    private static class BaseHolder extends RecyclerView.ViewHolder {
        Feed feed;
        /**
         * 收藏按钮
         */
        FlipImageView mFivStar;
        /**
         * 转发按钮
         */
        ImageView mIvShare;

        public BaseHolder(View itemView) {
            super(itemView);
            mFivStar = (FlipImageView) itemView.findViewById(R.id.fiv_star);
            mIvShare = (ImageView) itemView.findViewById(R.id.iv_share);
        }
    }

    private static class VideoViewHolder extends BaseHolder {
        RelativeLayout mRlFeedVideo;
        /**
         * 标题
         */
        TextView mTvTitle;
        /**
         * 缩略图
         */
        ImageView mIvThumb;

        StandardGSYVideoPlayer mVideoView;

        ProgressBar mLoadingBar;

        public VideoViewHolder(View itemView) {
            super(itemView);

            mRlFeedVideo = (RelativeLayout) itemView.findViewById(R.id.rl_feed_video);
            mTvTitle = (TextView) itemView.findViewById(R.id.tv_title);
            mIvThumb = (ImageView) itemView.findViewById(R.id.iv_thumb);
        }
    }

    private static class AlbumViewHolder extends BaseHolder {
        /**
         * 标题
         */
        TextView mTvTitle;
        /**
         * 缩略图
         */
        ImageView mIvThumb;

        LinearLayout mLlFeedContainer;

        public AlbumViewHolder(View itemView) {
            super(itemView);
            mTvTitle = (TextView) itemView.findViewById(R.id.tv_title);
            mIvThumb = (ImageView) itemView.findViewById(R.id.iv_thumb);
            mLlFeedContainer = (LinearLayout) itemView.findViewById(R.id.ll_feed_container);
        }
    }

    class BlogViewHolder extends BaseHolder {
        /**
         * 标题
         */
        TextView mTvTitle;

        /**
         * 日志摘要
         */
        TextView mTvSummary;

        LinearLayout mLlFeedContainer;

        public BlogViewHolder(View itemView) {
            super(itemView);
            mTvTitle = (TextView) itemView.findViewById(R.id.tv_title);
            mTvSummary = (TextView) itemView.findViewById(R.id.tv_summary);
            mLlFeedContainer = (LinearLayout) itemView.findViewById(R.id.ll_feed_container);
        }
    }

    private class HideFeedListener implements View.OnLongClickListener {
        Feed mFeed;

        HideFeedListener(Feed info) {
            mFeed = info;
        }

        @Override
        public boolean onLongClick(View v) {
            AlertDialog dialog = new AlertDialog.Builder(mContext).setMessage(R.string.hide_feed).setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    HideFeedRequest request = new HideFeedRequest(mContext, mFeed.getId(), new Response.Listener<String>() {

                        @Override
                        public void onResponse(String response) {
                            Toast.makeText(mContext, R.string.hide_feed_success, Toast.LENGTH_SHORT).show();
                            mDatas.remove(mFeed);
                            notifyDataSetChanged();
                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(mContext, ProtocolUtils.getErrorInfo(error).getErrorMessage(), Toast.LENGTH_SHORT).show();
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

    class StarChangeListener implements FlipImageView.OnFlipListener {
        Feed info;

        StarChangeListener(Feed feed) {
            info = feed;
        }

        @Override
        public void onClick(FlipImageView view) {
            boolean isStarred = view.isFlipped();
            if (isStarred) {
                mDataManager.addStarFeed(info);
                Toast.makeText(mContext, R.string.add_star_tip, Toast.LENGTH_SHORT).show();
            } else {
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

    private void getVideoDetail(final VideoViewHolder holder, final Feed feed) {
        String videoId = FeedUtil.getFeedVideoId(feed);
        if (TextUtils.isEmpty(videoId)) {
            Toast.makeText(mContext, "无效的视频id", Toast.LENGTH_LONG).show();
            return;
        }

        GetVideoDetailRequest request = new GetVideoDetailRequest(mContext, videoId,
                new Response.Listener<VideoDetail>() {

                    @Override
                    public void onResponse(VideoDetail response) {
                        holder.mLoadingBar.setVisibility(View.INVISIBLE);
//                        VideoView videoView = holder.mVideoView;
//                        //videoView.setVideoPath(response.mAddressUrl);
//
//                        MediaController mediaController = new MediaController(mContext);
//                        mediaController.setAnchorView(videoView);
//                        Uri video = Uri.parse(response.mAddressUrl);
//                        videoView.setMediaController(mediaController);
//                        videoView.setVideoURI(video);
//                        videoView.start();
                        holder.mVideoView.setUp(response.mAddressUrl, true, null, feed.getTitle());
                        //自动开始播放
                        holder.mVideoView.startPlayLogic();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                resetPlayingVideo();
                Toast.makeText(mContext, ProtocolUtils.getErrorInfo(error).getErrorMessage(), Toast.LENGTH_LONG).show();
            }
        });

        request.setTag(feed);
        mRequestQueue.add(request);
    }
}
