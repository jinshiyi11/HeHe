package com.shuai.hehe.ui;

import java.io.IOException;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.shuai.base.view.BaseActivity;
import com.shuai.hehe.R;
import com.shuai.hehe.data.Constants;
import com.shuai.utils.StorageUtils;

public class VideoActivity extends BaseActivity {

    private Context mContext;
    private String mVideoUrl;
    private WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mContext=this;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_activity);

        Intent intent = getIntent();
        mVideoUrl = intent.getStringExtra(Constants.VIDEO_URL);
        mWebView = (WebView) findViewById(R.id.webView1);

        mWebView.getSettings().setJavaScriptEnabled(true);
        //mWebView.getSettings().setAllowFileAccess(true);
        //mWebView.getSettings().setAppCacheEnabled(true);
        mWebView.getSettings().setBuiltInZoomControls(true);
        mWebView.getSettings().setSaveFormData(true);
        //mWebView.getSettings().setPluginState(PluginState.ON);
        //mWebView.getSettings().setLoadWithOverviewMode(true);
        //mWebView.getSettings().setUseWideViewPort(true);

//        mWebView.setWebChromeClient(new WebChromeClient() {
//
//            @Override
//            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
//                return super.onJsAlert(view, url, message, result);
//            }
//         
//
//        });

        mWebView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageFinished(WebView view, String url) {
                view.getSettings().setJavaScriptEnabled(true);
                super.onPageFinished(view, url);

                try {
                    String js = StorageUtils.loadAssetFileData(mContext, "tidy.js");
                    view.loadUrl("javascript:" + js);
                    
                    //view.loadUrl("javascript:alert('sss');");
                    
//                    view.loadUrl("javascript:(function() { "
//                            + "document.getElementsByTagName('body')[0].style.color = 'red'; " + "})()");
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                
            }

        });

        //mWebView.loadUrl("http://10.0.2.2/test.html");
        //mWebView.loadUrl("http://v.youku.com/v_show/id_XNzMwODQ5MjUy.html");
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

}
