package com.shuai.hehe.ui;

import java.io.IOException;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.shuai.base.view.BaseActivity;
import com.shuai.hehe.R;
import com.shuai.hehe.data.Constants;
import com.shuai.utils.StorageUtils;

public class VideoActivity extends BaseActivity {

    private Context mContext;
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
    private String mVideoUrl;
    private WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.KITKAT){
//            WebView.setWebContentsDebuggingEnabled(true);
//        }
        
        mContext=this;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_activity);

        Intent intent = getIntent();
        mVideoUrl = intent.getStringExtra(Constants.VIDEO_URL);
        
        mNoNetworkContainer=(ViewGroup) findViewById(R.id.no_network_container);
        mLoadingContainer=(ViewGroup) findViewById(R.id.loading_container);
        mMainContainer=(ViewGroup) findViewById(R.id.main_container);
        mWebView = (WebView) findViewById(R.id.webView1);
        
        mNoNetworkContainer.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                mWebView.loadUrl(mVideoUrl);
            }
        });

        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setAllowFileAccess(true);
        mWebView.getSettings().setAppCacheEnabled(true); 
        String appCachePath = getCacheDir().getAbsolutePath();  
        mWebView.getSettings().setAppCachePath(appCachePath); 
        mWebView.getSettings().setDatabaseEnabled(true);
     
        mWebView.getSettings().setDomStorageEnabled(true);
        //mWebView.getSettings().setBuiltInZoomControls(true);
        mWebView.getSettings().setSaveFormData(true);
        //mWebView.getSettings().setPluginState(PluginState.ON);
        //mWebView.getSettings().setLoadWithOverviewMode(true);
        //mWebView.getSettings().setUseWideViewPort(true);

        mWebView.setWebChromeClient(new WebChromeClient());

        mWebView.setWebViewClient(new WebViewClient() {
            private boolean mReceivedError=false;
            
            
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                //return super.shouldOverrideUrlLoading(view, url);
                mWebView.loadUrl(url);
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                
                mReceivedError=false;
                setStatus(Status.STATUS_LOADING);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                //TODO:检查http status code不是2xx的情况
                //mWebView.loadUrl("file:///android_asset/html/error.html");
                setStatus(Status.STATUS_NO_NETWORK_OR_DATA);
                mReceivedError=true;
            }
            
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                try {
                    String js = StorageUtils.loadAssetFileData(mContext, "tidy.js");
                    view.loadUrl("javascript:" + js);
                    
                    //view.loadUrl("javascript:alert('sss');");
                    
//                    view.loadUrl("javascript:(function() { "
//                            + "document.getElementsByTagName('body')[0].style.color = 'red'; " + "})()");
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if(!mReceivedError)
                    setStatus(Status.STATUS_GOT_DATA);
            }

        });

        //mWebView.loadUrl("http://10.0.2.2/test.html");
        //优酷
        //mWebView.loadUrl("http://v.youku.com/v_show/id_XNzMwODQ5MjUy.html");
        //56
        //mWebView.loadUrl("http://m.56.com/view/id-MTE2ODYyNTI2.html");
        //爱奇艺
        //mWebView.loadUrl("http://www.iqiyi.com/v_19rrhl73qo.html");
        //sina
        //mWebView.loadUrl("http://video.sina.com.cn/v/b/122612988-3655800732.html?bsh_bid\u003d330154687");
        //qq
        //mWebView.loadUrl("http://v.qq.com/page/a/x/p/a0013dleoxp.html?_out\u003d2");
        
        mWebView.loadUrl(mVideoUrl);
    }

    @Override
    public void onPause() {
        super.onPause();
        mWebView.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        mWebView.onResume();
    }
    
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

}
