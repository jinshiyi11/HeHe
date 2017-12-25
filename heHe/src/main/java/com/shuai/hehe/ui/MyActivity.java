package com.shuai.hehe.ui;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.widget.Toast;

import com.shuai.hehe.R;
import com.shuai.hehe.adapter.TabsAdapter;
import com.shuai.hehe.logic.UserManager;
import com.shuai.hehe.ui.base.BaseFragmentActivity;
import com.shuai.utils.ConnectionChangeMonitor;
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

public class MyActivity extends BaseFragmentActivity {
    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private TabsAdapter mTabsAdapter;

    /**
     * 上次按下back按钮的时间
     */
    private long mLastBackPressedTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        mViewPager=findViewById(R.id.viewpager);
        mTabLayout=findViewById(R.id.tablayout);
        mTabsAdapter=new TabsAdapter(this,R.layout.tab_with_icon,getTabInfos());
        mViewPager.setAdapter(mTabsAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
        for (int i = 0; i < mTabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = mTabLayout.getTabAt(i);
            tab.setCustomView(mTabsAdapter.getTabView(i));
        }

        //防止回收page
        mViewPager.setOffscreenPageLimit(mTabsAdapter.getCount());
    }

    private List<TabsAdapter.TabInfo> getTabInfos(){
        Bundle videoFragmentBundle=new Bundle();
        videoFragmentBundle.putInt(TestFragment.KEY_TYPE,TestFragment.TYPE_VIDEO);
        videoFragmentBundle.putString(TestFragment.KEY_TITLE,"视频");
        Bundle albumFragmentBundle=new Bundle();
        albumFragmentBundle.putInt(TestFragment.KEY_TYPE,FeedFragment.TYPE_ALBUM);
        albumFragmentBundle.putString(TestFragment.KEY_TITLE,"图片");
        Bundle favFragmentBundle=new Bundle();
        favFragmentBundle.putInt(TestFragment.KEY_TYPE,FeedFragment.TYPE_FAVORITE);
        favFragmentBundle.putString(TestFragment.KEY_TITLE,"收藏");

        List<TabsAdapter.TabInfo> list=new ArrayList<>();
        list.add(new TabsAdapter.TabInfo("视频",R.drawable.tab_followed,TestFragment.class,videoFragmentBundle));
        list.add(new TabsAdapter.TabInfo("图片",R.drawable.tab_market,TestFragment.class,albumFragmentBundle));
        list.add(new TabsAdapter.TabInfo("收藏",R.drawable.tab_news,TestFragment.class,favFragmentBundle));
        list.add(new TabsAdapter.TabInfo("我的",R.drawable.tab_user,UserCenterFragment.class,null));
        return list;
    }

    @Override
    public void onBackPressed() {
        if (StandardGSYVideoPlayer.backFromWindowFull(this)) {
            return;
        }

        if(System.currentTimeMillis()-mLastBackPressedTime>2000){
            mLastBackPressedTime=System.currentTimeMillis();
            Toast.makeText(mContext, R.string.one_more_exit, Toast.LENGTH_SHORT).show();
        }else{
            super.onBackPressed();
        }
    }

    @Subscribe
    public void onEvent(ConnectionChangeMonitor.EventConnectionChange event){
        if(event.isConnected()){
            //发现连上网，自动登录
            if(!UserManager.getInstance().isLogined())
                UserManager.getInstance().autoLogin();

            //updateOnlineConfig();
        }
    }
}
