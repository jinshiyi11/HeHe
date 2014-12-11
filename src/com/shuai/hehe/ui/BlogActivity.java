package com.shuai.hehe.ui;

import com.shuai.base.view.WebViewWrapper;
import com.shuai.hehe.R;
import com.shuai.hehe.data.Constants;
import com.shuai.hehe.data.BlogFeed;
import com.shuai.hehe.protocol.UrlHelper;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;

/**
 * 日志展示页面
 */
public class BlogActivity extends Activity {
    private BlogFeed mFeed;
    private WebViewWrapper mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog);
        
        Intent intent = getIntent();
        mFeed=intent.getParcelableExtra(Constants.FEED_BLOG);
        
        mWebView=(WebViewWrapper) findViewById(R.id.wv_web);
        mWebView.loadUrl(UrlHelper.getBlogUrl(mFeed.getId(),true));
    } 

}
