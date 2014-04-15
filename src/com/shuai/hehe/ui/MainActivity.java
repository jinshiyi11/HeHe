package com.shuai.hehe.ui;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;

import com.shuai.hehe.R;

public class MainActivity extends FragmentActivity {
    private View mTitleContainer;
    private FeedFragment mFeedFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
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
