package com.shuai.hehe.ui;

import java.io.File;
import java.io.IOException;

import org.apache.http.Header;
import org.apache.http.message.BasicHeader;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.FileAsyncHttpResponseHandler;
import com.shuai.base.view.BaseActivity;
import com.shuai.base.view.WebViewEx;
import com.shuai.base.view.WebViewWrapper;
import com.shuai.hehe.R;
import com.shuai.hehe.data.Constants;
import com.shuai.utils.StorageUtils;
import com.shuai.utils.Utils;

/**
 * 播放网页中的视频，并通过js脚本屏蔽非视频相关的html元素
 */
public class WebVideoActivity extends BaseActivity {
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
    private WebViewWrapper mWebViewWrapper;
    private WebViewEx mWebView;
    
    /**
     * 从服务端同步的用来去除视频网页中非视频元素的javascript文件路径
     */
    private String mVideoJsPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.KITKAT){
            WebView.setWebContentsDebuggingEnabled(true);
        }
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_video);

        Intent intent = getIntent();
        mVideoUrl = intent.getStringExtra(Constants.WEB_VIDEO_URL);
        
        mNoNetworkContainer=(ViewGroup) findViewById(R.id.no_network_container);
        mLoadingContainer=(ViewGroup) findViewById(R.id.loading_container);
        mMainContainer=(ViewGroup) findViewById(R.id.main_container);
        mWebViewWrapper=(WebViewWrapper) findViewById(R.id.webView1);
        mWebView = mWebViewWrapper.getWebView();
        
        String jsDirPath=mContext.getFilesDir().getAbsolutePath()+File.separator+"js";
        File jsDir=new File(jsDirPath);
        if(!jsDir.exists()){
            jsDir.mkdirs();
        }
        mVideoJsPath=jsDirPath+File.separator+Constants.VIDEO_JS_FILENAME;
        syncVideoJsFile();
        
        mNoNetworkContainer.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                mWebView.loadUrl(mVideoUrl);
            }
        });

        //TODO:mWebView.new MyWebViewClient() 这样写不好
        mWebView.setWebViewClient( mWebViewWrapper.new MyWebViewClient() {
            private boolean mReceivedError=false;
            
            
//            @Override
//            public boolean shouldOverrideUrlLoading(WebView view, String url) {
//                //return super.shouldOverrideUrlLoading(view, url);
//                mWebView.loadUrl(url);
//                return true;
//            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                
                mReceivedError=false;
                setStatus(Status.STATUS_LOADING);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                //TODO:检查http status code不是2xx的情况
                //mWebView.loadUrl("file:///android_asset/html/error.html");
                setStatus(Status.STATUS_NO_NETWORK_OR_DATA);
                mReceivedError=true;
                
            }
            
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                String js = getVideoJsData();
                view.loadUrl("javascript:" + js);
                    
                   // view.loadUrl("javascript:(function() { test(); function test() {alert('sss');}}());");
                    
//                    view.loadUrl("javascript:(function() { "
//                            + "document.getElementsByTagName('body')[0].style.color = 'red'; " + "})()");

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
        //土豆
        //mWebView.loadUrl("http://www.tudou.com/programs/view/DEKMOIHBM_8/?bid\u003d03\u0026pid\u003d3\u0026resourceId\u003d0_03_05_03");
        
        mWebView.loadUrl(mVideoUrl);
    }

    @Override
    public void onPause() {
        super.onPause();
        mWebView.onPause();
    }
    
//    @Override
//    public void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//        mWebView.saveState(outState);
//    }
    
    @Override
    public void onStop() {
        super.onStop();
        mWebView.stopLoading();
    }

    @Override
    public void onResume() {
        super.onResume();
        mWebView.onResume();
    }
    
    @Override
    protected void onDestroy() {
        mWebView.destroy();
        super.onDestroy();
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
    
    /**
     * 同步js文件
     */
    private void syncVideoJsFile(){
        AsyncHttpClient client = new AsyncHttpClient();
        Header[] headers=null;
        SharedPreferences pref = mContext.getSharedPreferences(Constants.PREF_NAME, 0);
        String eTag=pref.getString("ETag", null);
        if(eTag!=null){
            headers=new Header[1];
            headers[0]=new BasicHeader("If-None-Match", eTag);
        }
        
        client.get(null,Constants.VIDEO_JS_URL,headers,null, new FileAsyncHttpResponseHandler(mContext) {

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, File file) {
                
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, File file) {
                if(statusCode==200){
                    try {
                        File dest=new File(mVideoJsPath);
                        StorageUtils.copyFile(file, dest);
                        
                        for(Header head:headers){
                            if(head.getName().equals("ETag")){
                                SharedPreferences pref = mContext.getSharedPreferences(Constants.PREF_NAME, 0);
                                Editor edit = pref.edit();
                                edit.putString("ETag", head.getValue());
                                edit.commit();
                            }
                        }
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
            
        });
    }

    /**
     * 读取最新的js文件
     * @return
     */
    private String getVideoJsData() {
        String data=null;

        try {
//            if (StorageUtils.fileExists(mVideoJsPath)) {
//                data = StorageUtils.getFileData(mContext, mVideoJsPath);
//            } else {
//                data = StorageUtils.getAssetFileData(mContext, Constants.VIDEO_JS_FILENAME);
//            }
            data = StorageUtils.getAssetUTF8FileData(mContext, Constants.VIDEO_JS_FILENAME);
            data=Utils.removeComment(data);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return data;
    }

}
