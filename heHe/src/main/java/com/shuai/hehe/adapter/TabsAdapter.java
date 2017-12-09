package com.shuai.hehe.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.shuai.hehe.R;

import java.util.List;

/**
 *
 */
public class TabsAdapter extends FragmentPagerAdapter {
    private Context mContext;
    private int mLayoutId;
    private List<TabInfo> mTabs;
    private Fragment[] mFragments;
    private LayoutInflater mInflater;

    public static class TabInfo {
        private String mTitle;
        private int mIconId = View.NO_ID;
        private Class<? extends Fragment> mClass;
        private Bundle mArgs;

        public TabInfo(String title, Class<? extends Fragment> clazz, Bundle args) {
            mTitle = title;
            mClass = clazz;
            mArgs = args;
        }

        public TabInfo(String title, int iconId, Class<? extends Fragment> clazz, Bundle args) {
            mTitle = title;
            mIconId = iconId;
            mClass = clazz;
            mArgs = args;
        }
    }

    public TabsAdapter(FragmentActivity activity, int layoutId, List<TabInfo> tabInfos) {
        super(activity.getSupportFragmentManager());
        mContext = activity;
        mInflater = LayoutInflater.from(mContext);
        mLayoutId = layoutId;
        mTabs = tabInfos;
        mFragments = new Fragment[mTabs.size()];
    }

    @Override
    public int getCount() {
        return mTabs.size();
    }

    @Override
    public Fragment getItem(int position) {
        TabInfo info = mTabs.get(position);
        if (mFragments[position] == null) {
            mFragments[position] = Fragment.instantiate(mContext, info.mClass.getName(), info.mArgs);
        }
        return mFragments[position];
    }

    public View getTabView(int position) {
        View view = mInflater.inflate(mLayoutId, null);
        TabInfo tabInfo = mTabs.get(position);

        TextView tv = view.findViewById(R.id.tab_title);
        if (tv != null) {
            tv.setText(tabInfo.mTitle);
        }
        ImageView imageView = view.findViewById(R.id.tab_icon);
        if (imageView != null) {
            imageView.setImageResource(tabInfo.mIconId);
        }
        return view;
    }
}
