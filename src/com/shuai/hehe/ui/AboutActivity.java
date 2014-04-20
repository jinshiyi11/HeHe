package com.shuai.hehe.ui;

import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;

import com.shuai.base.view.BaseActivity;
import com.shuai.hehe.R;
import com.shuai.utils.AppUtils;

public class AboutActivity extends BaseActivity {
	private TextView mTvTitle;
	private TextView mTvVersion;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.about_activity);
		
		mTvTitle=(TextView) findViewById(R.id.tv_title);
		mTvTitle.setText(R.string.about);
		mTvVersion=(TextView) findViewById(R.id.tv_appver);
		mTvVersion.setText(String.format(getString(R.string.current_version), AppUtils.getVersionName(this)));
	}
	
	

}
