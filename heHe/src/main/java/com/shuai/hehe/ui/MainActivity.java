package com.shuai.hehe.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.widget.Toast;

import com.shuai.hehe.R;
import com.shuai.hehe.adapter.TabsAdapter;
import com.shuai.hehe.data.Constants;
import com.shuai.hehe.logic.UserManager;
import com.shuai.hehe.ui.base.BaseFragmentActivity;
import com.shuai.utils.ConnectionChangeMonitor;
import com.shuai.utils.NavigateUtils;
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.sso.UMSsoHandler;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseFragmentActivity {
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
        setContentView(R.layout.activity_main);

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
        videoFragmentBundle.putInt(FeedFragment.KEY_TYPE, FeedFragment.TYPE_VIDEO);
        videoFragmentBundle.putString(FeedFragment.KEY_TITLE,"视频");
        Bundle albumFragmentBundle=new Bundle();
        albumFragmentBundle.putInt(FeedFragment.KEY_TYPE, FeedFragment.TYPE_ALBUM);
        albumFragmentBundle.putString(FeedFragment.KEY_TITLE,"图片");
        Bundle favFragmentBundle=new Bundle();
        favFragmentBundle.putInt(FeedFragment.KEY_TYPE, FeedFragment.TYPE_FAVORITE);
        favFragmentBundle.putString(FeedFragment.KEY_TITLE,"收藏");

        List<TabsAdapter.TabInfo> list=new ArrayList<>();
        list.add(new TabsAdapter.TabInfo("视频",R.drawable.tab_followed,FeedFragment.class,videoFragmentBundle));
        list.add(new TabsAdapter.TabInfo("图片",R.drawable.tab_market,FeedFragment.class,albumFragmentBundle));
        list.add(new TabsAdapter.TabInfo("收藏",R.drawable.tab_news,FavFragment.class,favFragmentBundle));
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        final UMSocialService mController = UMServiceFactory.getUMSocialService("com.umeng.share");
        /**使用SSO授权必须添加如下代码 */
        UMSsoHandler ssoHandler = mController.getConfig().getSsoHandler(requestCode) ;
        if(ssoHandler != null){
            ssoHandler.authorizeCallBack(requestCode, resultCode, data);
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

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        showTab(intent.getIntExtra(Constants.EXTRA_TAB, NavigateUtils.TAB_VIDEO));
    }

    private void showTab(int tab) {
        switch (tab) {
            case NavigateUtils.TAB_VIDEO: {
                mViewPager.setCurrentItem(0);
                break;
            }
            case NavigateUtils.TAB_ALBUM: {
                mViewPager.setCurrentItem(1);
                break;
            }
            case NavigateUtils.TAB_FAV: {
                mViewPager.setCurrentItem(2);
                break;
            }
            case NavigateUtils.TAB_USER: {
//                if (!isLogined()) {
//                    //NavigateUtils.showTab(mContext, NavigateUtils.TAB_MESSAGE);
//                    return;
//                }
                mViewPager.setCurrentItem(3);
                break;
            }
        }
    }

    private boolean isLogined() {
        return UserManager.getInstance().isLogined();
    }
}
