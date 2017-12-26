package com.shuai.hehe.ui;

import android.os.Bundle;
import android.view.Window;

import com.shuai.hehe.R;
import com.shuai.hehe.ui.base.BaseFragmentActivity;


/**
 * 设置界面
 */
public class SettingsActivity extends BaseFragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_settings);
	}
	
	

}
