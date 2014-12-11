/**
 * from https://code.google.com/p/html5webview
 * https://github.com/mikewang0326/Html5Webview
 */
package com.shuai.base.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.GeolocationPermissions;
import android.webkit.JsPromptResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

import com.shuai.hehe.R;

public class WebViewWrapper extends FrameLayout {
    private static final String TAG = WebViewWrapper.class.getSimpleName();
	
	private Context 							mContext;
	private MyWebChromeClient					mWebChromeClient;
	private View								mCustomView;
	private FrameLayout							mCustomViewContainer;
	private WebChromeClient.CustomViewCallback 	mCustomViewCallback;
	
	private FrameLayout							mBrowserFrameLayout;
	
	private WebViewEx mWebView;
	    
	private void init(Context context) {
		mContext = context;		
		
		mBrowserFrameLayout = (FrameLayout) LayoutInflater.from(mContext).inflate(R.layout.custom_screen, null);
		mCustomViewContainer = (FrameLayout) mBrowserFrameLayout.findViewById(R.id.fullscreen_custom_content);
		addView(mBrowserFrameLayout, COVER_SCREEN_PARAMS);
		
		mWebView=(WebViewEx) findViewById(R.id.webview);		
		mWebChromeClient = new MyWebChromeClient();
		mWebView.setWebChromeClient(mWebChromeClient);
	    
		mWebView.setWebViewClient(new MyWebViewClient());
	       
	    // Configure the webview
	    WebSettings s = mWebView.getSettings();
	    s.setJavaScriptEnabled(true);
	    s.setAllowFileAccess(true);
        s.setAppCacheEnabled(true); 
        String appCachePath = mContext.getCacheDir().getAbsolutePath();  
        s.setAppCachePath(appCachePath); 
        s.setDatabaseEnabled(true);
     
        s.setDomStorageEnabled(true);
        //s.setBuiltInZoomControls(true);
        s.setSaveFormData(true);
        s.setSavePassword(true);
        //s.setPluginState(PluginState.ON);
        s.setLoadWithOverviewMode(true);
        s.setUseWideViewPort(true);
	   
	    //s.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
	    // enable navigator.geolocation 
	    //s.setGeolocationEnabled(true);
	    //s.setGeolocationDatabasePath("/data/data/org.itri.html5webview/databases/");
   
	}
	
    public WebViewWrapper(Context context) {
		super(context);
		init(context);
	}

	public WebViewWrapper(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public WebViewWrapper(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}
	
	public WebView getWebView() {
		return mWebView;
	}
	
	public void loadUrl(String url) {
        mWebView.loadUrl(url);
    }
	
    public boolean inCustomView() {
		return (mCustomView != null);
	}
    
    public void hideCustomView() {
		mWebChromeClient.onHideCustomView();
	}
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
    	if (keyCode == KeyEvent.KEYCODE_BACK) {
    		if ((mCustomView == null) && mWebView.canGoBack()){
    		    mWebView.goBack();
    			return true;
    		}
    	}
    	return super.onKeyDown(keyCode, event);
    }

    private class MyWebChromeClient extends WebChromeClient {
		private Bitmap 		mDefaultVideoPoster;
		private View 		mVideoProgressView;
    	
    	@Override
		public void onShowCustomView(View view, WebChromeClient.CustomViewCallback callback)
		{
			//Log.i(LOGTAG, "here in on ShowCustomView");
    	    mWebView.setVisibility(View.GONE);
	        
	        // if a view already exists then immediately terminate the new one
	        if (mCustomView != null) {
	            callback.onCustomViewHidden();
	            return;
	        }
	        
	        mCustomViewContainer.addView(view);
	        mCustomView = view;
	        mCustomViewCallback = callback;
	        mCustomViewContainer.setVisibility(View.VISIBLE);
		}
		
		@Override
		public void onHideCustomView() {
			
			if (mCustomView == null)
				return;	       
			
			// Hide the custom view.
			mCustomView.setVisibility(View.GONE);
			
			// Remove the custom view from its container.
			mCustomViewContainer.removeView(mCustomView);
			mCustomView = null;
			mCustomViewContainer.setVisibility(View.GONE);
			mCustomViewCallback.onCustomViewHidden();
			
			mWebView.setVisibility(View.VISIBLE);
			
	        //Log.i(LOGTAG, "set it to webVew");
		}
		
		@Override
		public Bitmap getDefaultVideoPoster() {
			//Log.i(LOGTAG, "here in on getDefaultVideoPoster");	
			if (mDefaultVideoPoster == null) {
				mDefaultVideoPoster = BitmapFactory.decodeResource(
						getResources(), R.drawable.default_video_poster);
		    }
			return mDefaultVideoPoster;
		}
		
		@Override
		public View getVideoLoadingProgressView() {
			//Log.i(LOGTAG, "here in on getVideoLoadingPregressView");
			
	        if (mVideoProgressView == null) {
	            LayoutInflater inflater = LayoutInflater.from(mContext);
	            mVideoProgressView = inflater.inflate(R.layout.video_loading_progress, null);
	        }
	        return mVideoProgressView; 
		}
    	
    	 @Override
         public void onReceivedTitle(WebView view, String title) {
            ((Activity) mContext).setTitle(title);
         }

         @Override
         public void onProgressChanged(WebView view, int newProgress) {
        	 ((Activity) mContext).getWindow().setFeatureInt(Window.FEATURE_PROGRESS, newProgress*100);
         }
         
        @Override
        public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
            callback.invoke(origin, true, false);
        }

        //通过prompt实现安全回调
        @Override
        public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
            if (view instanceof WebViewEx) {
                WebViewEx webview = (WebViewEx) view;
                if (webview.handleJsInterface(view, url, message, defaultValue, result)) {
                    return true;
                }
            }

            return super.onJsPrompt(view, url, message, defaultValue, result);
        }
         
    }
	
    //TODO:onReceivedError,onPageFinished,onPageStarted
	public class MyWebViewClient extends WebViewClient {
	    @Override
	    public boolean shouldOverrideUrlLoading(WebView view, String url) {
	    	Log.i(TAG, "shouldOverrideUrlLoading: "+url);
	    	// don't override URL so that stuff within iframe can work properly
	        // view.loadUrl(url);
	    	
	    	if (url.startsWith("tel:")) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(url));
                mContext.startActivity(intent);
                return true;
            }
	    	
	        return false;
	    }
	}
	
	static final FrameLayout.LayoutParams COVER_SCREEN_PARAMS =
        new FrameLayout.LayoutParams( ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
}