package com.shuai.hehe.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.shuai.hehe.R;
import com.shuai.hehe.adapter.FeedAdapter.FeedList;

public class FeedFragment extends Fragment {
    
    private PullToRefreshListView mListView;
    private FeedList mFeedList=new FeedList();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.feed_fragment, container,false);
        mListView=(PullToRefreshListView) view.findViewById(R.id.listview);
        mListView.setMode(Mode.BOTH);
        
        mListView.setOnRefreshListener(new OnRefreshListener2<ListView>() {

            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                
            }
        });
        return view;
    }

    
    
    

}
