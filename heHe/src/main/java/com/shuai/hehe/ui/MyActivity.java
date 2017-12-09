package com.shuai.hehe.ui;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;

import com.shuai.base.view.BaseFragmentActivity;
import com.shuai.hehe.R;
import com.shuai.hehe.adapter.TabsAdapter;

import java.util.ArrayList;
import java.util.List;

public class MyActivity extends BaseFragmentActivity {
    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private TabsAdapter mTabsAdapter;

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
    }

    private List<TabsAdapter.TabInfo> getTabInfos(){
        List<TabsAdapter.TabInfo> list=new ArrayList<>();
        list.add(new TabsAdapter.TabInfo("视频",R.drawable.tab_followed,FeedFragment.class,null));
        list.add(new TabsAdapter.TabInfo("图片",R.drawable.tab_followed,FeedFragment.class,null));
        list.add(new TabsAdapter.TabInfo("收藏",R.drawable.tab_followed,FeedFragment.class,null));
        list.add(new TabsAdapter.TabInfo("我的",R.drawable.tab_followed,FeedFragment.class,null));
        return list;
    }
}
