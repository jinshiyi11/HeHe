package com.shuai.hehe.ui;

import io.vov.vitamio.LibsChecker;
import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.MediaPlayer.OnBufferingUpdateListener;
import io.vov.vitamio.MediaPlayer.OnPreparedListener;
import io.vov.vitamio.widget.MediaController.OnHiddenListener;
import io.vov.vitamio.widget.MediaController.OnShownListener;
import io.vov.vitamio.widget.VideoView;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.shuai.base.view.BaseActivity;
import com.shuai.base.view.MediaControllerEx;
import com.shuai.hehe.HeHeApplication;
import com.shuai.hehe.R;
import com.shuai.hehe.data.Constants;
import com.shuai.hehe.data.VideoInfo;
import com.shuai.hehe.protocol.GetVideoUrlRequest;
import com.shuai.utils.AnimUtils;
import com.shuai.utils.DisplayUtils;

public class VideoActivity extends BaseActivity implements OnClickListener {
    public static final String TAG=VideoActivity.class.getSimpleName();
    private ViewGroup mNoNetworkContainer;
    private ViewGroup mLoadingContainer;
    private ViewGroup mMainContainer;
    enum Status {
        /**
         * 正在加载并且还没有任何数据
         */
        STATUS_LOADING,
        /**
         * 有数据
         */
        STATUS_GOT_DATA,
        /**
         * 加载失败并且没有任何数据
         */
        STATUS_NO_NETWORK_OR_DATA
    }
    
    private Status mStatus;
    private RequestQueue mRequestQueue;
    private String mWebVideoUrl;
    private ViewGroup mVgTitle;
    private View mIvBack;
    private TextView mTvTitle;
    private VideoView mVideoView;
    private MediaControllerEx mMediaController;
    //缓冲进度提示
    private ViewGroup mMediaBufferingIndicator;
    private TextView mTvMediaBufferingIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        mRequestQueue=HeHeApplication.getRequestQueue();
        
        if (!LibsChecker.checkVitamioLibs(this)){
            Log.e(TAG, "Vitamio init failed!");
            return;
        }
        setContentView(R.layout.activity_video);
        mNoNetworkContainer=(ViewGroup) findViewById(R.id.no_network_container);
        mLoadingContainer=(ViewGroup) findViewById(R.id.loading_container);
        mMainContainer=(ViewGroup) findViewById(R.id.main_container);
        
        mNoNetworkContainer.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                getVideoUrl();
            }
        });       
        
        Intent intent = getIntent();
        mWebVideoUrl = intent.getStringExtra(Constants.WEB_VIDEO_URL);
        String title=intent.getStringExtra(Constants.VIDEO_TITLE);
        
        mVgTitle=(ViewGroup) findViewById(R.id.ll_title);
        mIvBack=findViewById(R.id.iv_back);
        mIvBack.setOnClickListener(this);
        mTvTitle=(TextView) findViewById(R.id.tv_title);
        mTvTitle.setText(title);
        
        mVideoView=(VideoView) findViewById(R.id.videoView);
        mMediaBufferingIndicator=(ViewGroup) findViewById(R.id.ll_buffering_indicator);
        mTvMediaBufferingIndicator=(TextView) findViewById(R.id.tv_buffering_indicator);
        
        //mWebVideoUrl="http://124.126.126.141/103/8/93/letv-uts/14/ver_00_22-303073903-avc-960567-aac-64005-269000-34753068-e12f25fa8c6c43899ca598abbae2c6a2-1421791274793.letv?crypt=63aa7f2e350&b=1033&nlh=3072&nlt=45&bf=27&p2p=1&video_type=mp4&termid=1&tss=no&geo=CN-1-0-1&tm=1422250800&key=1c2168c4ac6e73eed245af3c185df693&platid=1&splatid=101&its=0&keyitem=platid,splatid,its&ntm=1422250800&nkey=1c2168c4ac6e73eed245af3c185df693&proxy=1032384066,1780933154&mmsid=26269843&playid=0&vtype=22&cvid=1079171371978&hwtype=un&ostype=Windows7&tag=letv&sign=letv&pay=0&rateid=1300&errc=0&gn=603&buss=100&qos=4&cips=218.30.116.7";
        //mWebVideoUrl="http://k.youku.com/player/getFlvPath/sid/742181013102912ad57bb_00/st/flv/fileid/030001030054BDFCAA142E06A9C1DB82D8FC79-02BE-2437-A72E-7F35F020A8F9?K=3109d3689d6b94bf241215b0&ctype=12&ev=1&oip=2095616453&token=8869&ep=dSaWG0GMVs8D5CPdgT8bNnjncyVdXP4J9h%2BFgNJhALshTJrL702gtpy0TIpCEIgQASNyYuPz39iRGUcdYfBB2x0Q3UqrPvqR%2BfKW5aolx5V2Zhs2A8WhvVSWSzb4";
        if(TextUtils.isEmpty(mWebVideoUrl)){
            Log.e(TAG, "videoUrl is empty!");
            return;
        }
        
        
        mVideoView.setVideoLayout(VideoView.VIDEO_LAYOUT_STRETCH, 0);//全屏展示
        mVideoView.setOnPreparedListener(new OnPreparedListener() {

            @Override
            public void onPrepared(MediaPlayer mp) {
                //setVideoFullScreen();
                mVideoView.start();
            }
            
        });
        //mVideoView.setVideoURI(Uri.parse(mWebVideoUrl));
        mMediaController=new MediaControllerEx(this);
        mMediaController.setAnimationStyle(R.style.MediaControlAnimation);
        mVideoView.setMediaController(mMediaController);
        mVideoView.setMediaBufferingIndicator(mMediaBufferingIndicator);
        mVideoView.setOnBufferingUpdateListener(new OnBufferingUpdateListener() {
            
            @Override
            public void onBufferingUpdate(MediaPlayer mp, int percent) {
                //显示缓冲进度
                mTvMediaBufferingIndicator.setText(getResources().getString(R.string.buffering_percent, percent));
            }
        });
        mVideoView.requestFocus();
        getVideoUrl();
        
        mMediaController.setOnHiddenListener(new OnHiddenListener() {
            
            @Override
            public void onHidden() {
                Animation animation = AnimUtils.slideAnimation(mContext, AnimUtils.SlideType.SLIDE_OUT_FROM_TOP,mVgTitle);
                mVgTitle.startAnimation(animation);
            }
        });
        
        mMediaController.setOnShownListener(new OnShownListener() {
            
            @Override
            public void onShown() {
                Animation animation = AnimUtils.slideAnimation(mContext, AnimUtils.SlideType.SLIDE_IN_FROM_TOP,mVgTitle);
                mVgTitle.startAnimation(animation);
            }
        });
    }
    
    @Override
    public void onDestroy() {
        mRequestQueue.cancelAll(this);
        super.onDestroy();
    }
    
    /**
     * 获取视频文件的地址
     */
    private void getVideoUrl(){
        setStatus(Status.STATUS_LOADING);
        GetVideoUrlRequest request=new GetVideoUrlRequest(mContext,mWebVideoUrl, new Listener<VideoInfo>(){

            @Override
            public void onResponse(VideoInfo response) {
                if(response.getParts().size()>0){
                    setStatus(Status.STATUS_GOT_DATA);
                    mVideoView.setVideoURI(Uri.parse(response.getParts().get(0).getVideoUrl()));
                    return;
                }
                
                setStatus(Status.STATUS_NO_NETWORK_OR_DATA);
            }
            
        }, new ErrorListener(){

            @Override
            public void onErrorResponse(VolleyError error) {
                setStatus(Status.STATUS_NO_NETWORK_OR_DATA);
                Toast.makeText(mContext, R.string.error_getting_video_url, Toast.LENGTH_SHORT);
                return;
            }
            
        });
        request.setTag(this);       
        mRequestQueue.add(request);
    }
    
    private void setVideoFullScreen(){
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) mVideoView.getLayoutParams();
        DisplayMetrics displayMetrics = DisplayUtils.getDisplayMetrics(mContext);
        params.width =  displayMetrics.widthPixels;
        params.height = displayMetrics.heightPixels;
        Log.d(TAG, String.format("screenWidth:%d,screenHeight:%d",params.width,params.height));
        params.setMargins(0, 0, 0, 0);
        mVideoView.setLayoutParams(params);
    }

//    @Override
//    public void onConfigurationChanged(Configuration newConfig) {
//        super.onConfigurationChanged(newConfig);
//        
//        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
//
//            mVideoView.setVideoLayout(VideoView.VIDEO_LAYOUT_STRETCH, 0);
//            Log.e("On Config Change", "LANDSCAPE");
//
//        } else {
//            mVideoView.setVideoLayout(VideoView.VIDEO_LAYOUT_STRETCH, 0);
//            Log.e("On Config Change", "PORTRAIT");
//        }
//    }
    
    private void setStatus(Status status) {
        mStatus = status;
        switch (status) {
        case STATUS_LOADING:
            mLoadingContainer.setVisibility(View.VISIBLE);
            mMainContainer.setVisibility(View.GONE);
            mNoNetworkContainer.setVisibility(View.GONE);
            break;
        case STATUS_GOT_DATA:
            mLoadingContainer.setVisibility(View.GONE);
            mMainContainer.setVisibility(View.VISIBLE);
            mNoNetworkContainer.setVisibility(View.GONE);
            break;
        case STATUS_NO_NETWORK_OR_DATA:
            mLoadingContainer.setVisibility(View.GONE);
            mMainContainer.setVisibility(View.GONE);
            mNoNetworkContainer.setVisibility(View.VISIBLE);
            break;
        default:
            break;
        }

    }

    @Override
    public void onClick(View v) {
        int id=v.getId();
        switch(id){
        case R.id.iv_back:
            finish();
            break;
        }
    }
    
    
}
