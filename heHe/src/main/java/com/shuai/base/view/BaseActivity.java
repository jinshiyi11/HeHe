package com.shuai.base.view;

import android.app.Activity;
import android.content.Context;

import com.umeng.analytics.MobclickAgent;

public class BaseActivity extends Activity {
    protected Context mContext;

    public BaseActivity() {
        mContext=this;
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    
}
