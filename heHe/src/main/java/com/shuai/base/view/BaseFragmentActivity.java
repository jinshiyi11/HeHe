package com.shuai.base.view;

import android.content.Context;
import android.support.v4.app.FragmentActivity;

import com.umeng.analytics.MobclickAgent;

public class BaseFragmentActivity extends FragmentActivity {
    protected Context mContext;

    public BaseFragmentActivity() {
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
