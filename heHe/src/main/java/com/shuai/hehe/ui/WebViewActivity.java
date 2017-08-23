package com.shuai.hehe.ui;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

import com.shuai.base.view.BaseActivity;
import com.shuai.hehe.R;
import com.shuai.hehe.data.Constants;

public class WebViewActivity extends BaseActivity {
	private static final String TAG="WebViewActivity";
	private WebView mWebView;
	private String mVideoUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview_activity);
        
        mWebView=(WebView) findViewById(R.id.webView1);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setAllowFileAccess(true);
        mWebView.getSettings().setAppCacheEnabled(true);
        mWebView.getSettings().setBuiltInZoomControls(true);
        mWebView.getSettings().setSaveFormData(true);
        mWebView.getSettings().setPluginState(PluginState.ON);
        //mWebView.getSettings().setPluginsEnabled(true);
        mWebView.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
        
        mWebView.setWebViewClient(new WebViewClient(){

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}

			@Override
			public void onReceivedError(WebView view, int errorCode,
					String description, String failingUrl) {
				// TODO Auto-generated method stub
				super.onReceivedError(view, errorCode, description, failingUrl);
			}
			
        	
        });
        mWebView.setWebChromeClient(new WebChromeClient());
        
        boolean flashInstalled=isFlashPluginInstalled();
        
        
        Intent intent=getIntent();
        mVideoUrl=intent.getStringExtra(Constants.WEB_VIDEO_URL);
        
        mWebView.loadUrl(mVideoUrl);
    }
    
    private boolean isFlashPluginInstalled() {
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo("com.adobe.flashplayer", 0);
            return packageInfo != null;
        } catch (PackageManager.NameNotFoundException x) {
            return false;
        }
    }
    
    @Override
    protected void onPause(){
        super.onPause();
        
        callHiddenWebViewMethod("onPause");

        mWebView.pauseTimers();
        if(isFinishing()){
        	mWebView.loadUrl("about:blank");
            setContentView(new FrameLayout(this));
        }
    }

    @Override
    protected void onResume(){
        super.onResume();

        callHiddenWebViewMethod("onResume");

        mWebView.resumeTimers();
    }
    
    private void callHiddenWebViewMethod(String name){
    	// credits: http://stackoverflow.com/questions/3431351/how-do-i-pause-flash-content-in-an-android-webview-when-my-activity-isnt-visible
        if( mWebView != null ){
            try {
                Method method = WebView.class.getMethod(name);
                method.invoke(mWebView);
            } catch (NoSuchMethodException e) {
                Log.e(TAG,"No such method: " + name + e);
            } catch (IllegalAccessException e) {
            	Log.e(TAG,"Illegal Access: " + name + e);
            } catch (InvocationTargetException e) {
            	Log.e(TAG,"Invocation Target Exception: " + name + e);
            }
        }
    }

}
