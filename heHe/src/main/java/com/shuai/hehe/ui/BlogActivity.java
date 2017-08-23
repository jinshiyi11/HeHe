package com.shuai.hehe.ui;

import com.shuai.base.view.BaseActivity;
import com.shuai.base.view.WebViewWrapper;
import com.shuai.hehe.R;
import com.shuai.hehe.data.Constants;
import com.shuai.hehe.data.BlogFeed;
import com.shuai.hehe.protocol.UrlHelper;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * 日志展示页面
 */
public class BlogActivity extends BaseActivity {
    private BlogFeed mFeed;
    
    private View mIvBack;
    private View mTitleContainer;
    private TextView mTvTitle;
    
    private WebViewWrapper mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_blog);
        
        Intent intent = getIntent();
        mFeed=intent.getParcelableExtra(Constants.FEED_BLOG);
        
        mIvBack=findViewById(R.id.iv_back);
        mIvBack.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        
        mTvTitle=(TextView) findViewById(R.id.tv_title);
        mTvTitle.setText(mFeed.getTitle());
        mTitleContainer = findViewById(R.id.rl_title);
        mTitleContainer.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
              //TODO:单击标题栏时返回顶部

            }
        });
        
        mWebView=(WebViewWrapper) findViewById(R.id.wv_web);
        mWebView.loadUrl(UrlHelper.getBlogUrl(mContext,mFeed.getId(),true));

    } 

}
