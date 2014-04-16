package com.shuai.hehe.ui;

import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;

import com.shuai.base.view.BaseFragmentActivity;
import com.shuai.hehe.R;
import com.umeng.update.UmengUpdateAgent;

public class MainActivity extends BaseFragmentActivity {
    private View mTitleContainer;
    private FeedFragment mFeedFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//检查升级
		UmengUpdateAgent.update(this);
		getWindow().requestFeature(Window.FEATURE_NO_TITLE);		
		setContentView(R.layout.activity_main);
		
		mTitleContainer=findViewById(R.id.rl_title);
		mFeedFragment=(FeedFragment)getSupportFragmentManager().findFragmentById(R.id.feed_fragment);
		mTitleContainer.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                mFeedFragment.onTitleClicked();
                
            }
        });
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
