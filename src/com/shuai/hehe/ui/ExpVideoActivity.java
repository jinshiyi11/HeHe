package com.shuai.hehe.ui;

import java.io.IOException;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.android.volley.RequestQueue;
import com.shuai.base.view.BaseActivity;
import com.shuai.base.view.WebViewEx;
import com.shuai.base.view.WebViewWrapper;
import com.shuai.hehe.HeHeApplication;
import com.shuai.hehe.R;
import com.shuai.hehe.data.Constants;
import com.shuai.utils.DisplayUtils;
import com.shuai.utils.StorageUtils;
import com.shuai.utils.Utils;

/**
 * 实验版播放视频，从web页面中提取视频地址，然后在VideoView中播放
 */
public class ExpVideoActivity extends BaseActivity {
    private static final String TAG=ExpVideoActivity.class.getSimpleName();
    
    private Context mContext;
    
    /**
     * 异步请求队列
     */
    private RequestQueue mRequestQueue;
    
    private String mWebVideoUrl;
    private VideoView mVideoView;
    private WebViewWrapper mWebViewWrapper;
    private WebViewEx mWebView;
    private JsObject jsObj;
    private Handler mUiHandler=new Handler();
    
    class JsObject {
        @JavascriptInterface
        public void onGotVideoUrl(final String videoUrl) {
            if (TextUtils.isEmpty(videoUrl)) {
                Log.e(TAG, "video Url is empty!");
                return;
            }
            
            //在主线程中执行ui操作
            mUiHandler.post(new Runnable() {
                
                @Override
                public void run() {
                    Log.i(TAG, videoUrl);
                    
                    mWebView.loadUrl("about:blank");
//                    mWebView.stopLoading();
//                    mWebView.destroy();
                    mVideoView.setVideoURI(Uri.parse(videoUrl));
                    setVideoFullScreen();
                }
            });
                    
        }
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mContext=this;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        mRequestQueue=HeHeApplication.getRequestQueue();
        setContentView(R.layout.activity_exp_video);
        
        mWebViewWrapper=(WebViewWrapper) findViewById(R.id.webView);
        mVideoView=(VideoView) findViewById(R.id.videoView);
        mWebView = mWebViewWrapper.getWebView();
        
        
        Intent intent = getIntent();        
        MediaController mediaController=new MediaController(this);
        mediaController.setAnchorView(mVideoView);
        mVideoView.setMediaController(mediaController);
        
        mVideoView.setOnErrorListener(new OnErrorListener() {
            
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                Toast.makeText(ExpVideoActivity.this, "error", Toast.LENGTH_LONG);
                return false;
            }
        });
        
        mVideoView.setOnPreparedListener(new OnPreparedListener() {
            
            @Override
            public void onPrepared(MediaPlayer mp) {
                mVideoView.start();
            }
        });
        
        
        mWebVideoUrl = intent.getStringExtra(Constants.WEB_VIDEO_URL);
        jsObj=new JsObject();
        mWebView.addJavascriptInterface(jsObj, "my");
//        mWebView.setWebChromeClient(mWebViewWrapper.new MyWebChromeClient(){
//
//            @Override
//            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
//                Log.i("console", consoleMessage.message());
//                return super.onConsoleMessage(consoleMessage);
//            }
//            
//        });
        
        mWebView.setWebViewClient( mWebViewWrapper.new MyWebViewClient() {
            private void loadGetVideoJs(WebView view){
                String js;
                try {
                    js = StorageUtils.getAssetUTF8FileData(mContext, "getvideo.js");
                    js=Utils.removeComment(js);
                    view.loadUrl("javascript:" + js);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                loadGetVideoJs(view);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                loadGetVideoJs(view);
            }
            
        });
        
//        mVideoView.setVideoURI(Uri.parse("http://f8.r.56.com/f8.c117.56.com/flvdownload/28/9/sc_138020676368hd.flv.mp4?v=1&t=eTkcEX8YFuWSEBpwDNk7Jw&r=73328&e=1421291083&tt=14&sz=859601&vid=97581128"));
        Log.d(TAG, "loading:"+mWebVideoUrl);
        mWebViewWrapper.loadUrl(mWebVideoUrl);
        
        /*
        GetVideoUrlRequest request=new GetVideoUrlRequest(mWebVideoUrl, new Listener<VideoInfo>(){

            @Override
            public void onResponse(VideoInfo response) {
                mVideoView.setVideoURI(Uri.parse(response.getVideoUrl()));
                mVideoView.start();
            }
            
        }, new ErrorListener(){

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(mContext, R.string.error_getting_video_url, Toast.LENGTH_SHORT);
                return;
            }
            
        });
        request.setTag(this);       
        mRequestQueue.add(request);
        */
    }
    
    @Override
    public void onDestroy() {
        
        mWebView.removeJavascriptInterface("my");
//        mWebView.removeAllViews();
//        mWebView.destroy();
        mRequestQueue.cancelAll(this);
        super.onDestroy();
    }

}
