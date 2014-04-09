package com.shuai.hehe.ui;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.shuai.hehe.HeHeApplication;
import com.shuai.hehe.R;
import com.shuai.hehe.adapter.FeedAdapter;
import com.shuai.hehe.adapter.FeedAdapter.FeedList;
import com.shuai.hehe.data.Feed;
import com.shuai.hehe.protocol.GetFeedsRequest;

public class FeedFragment extends Fragment {
    private ViewGroup mNoNetworkContainer;
    private ViewGroup mLoadingContainer;
    private ViewGroup mMainContainer;
    enum Status {
        /**
         * 正在加载并且还没有任何数据
         */
        STATUS_LOADING,
        /**
         * 有数据
         */
        STATUS_GOT_DATA,
        /**
         * 加载失败并且没有任何数据
         */
        STATUS_NO_NETWORK_OR_DATA
    }
    
    private Status mStatus;
    
    private PullToRefreshListView mListView;
    private FeedList mFeedList=new FeedList(); 
    private FeedAdapter mFeedAdapter;
    
    /**
     * 每次获取的feed个数
     */
	private static final int PAGE_COUNT = 30;
	
	/**
	 * 起始feed的id
	 */
	private static final int START_ID = -1;
	
    /**
     * 是否是启动之后的首次请求
     */ 
    private boolean mIsStartRequest=true;
    
    /**
     * 异步请求队列
     */
    private RequestQueue mRequestQueue;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.feed_fragment, container,false);
        mRequestQueue=HeHeApplication.getRequestQueue();
        
        mNoNetworkContainer=(ViewGroup) view.findViewById(R.id.no_network_container);
        mLoadingContainer=(ViewGroup) view.findViewById(R.id.loading_container);
        mMainContainer=(ViewGroup) view.findViewById(R.id.main_container);
        
        mListView=(PullToRefreshListView) view.findViewById(R.id.listview);
        mListView.setMode(Mode.BOTH);
        mFeedAdapter=new FeedAdapter(getActivity(), mFeedList);
        mListView.setAdapter(mFeedAdapter);
        
        mListView.setOnRefreshListener(new OnRefreshListener2<ListView>() {

            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                getData(true);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                getData(false);
            }
        });
        
        mNoNetworkContainer.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                mListView.setRefreshing(false);
            }
        });
        
        mListView.setRefreshing();
        return view;
    }
    
    @Override
	public void onDestroyView() {
    	mRequestQueue.cancelAll(this);
		super.onDestroyView();
	}
    
    private void setStatus(Status status) {
        mStatus = status;
        switch (status) {
        case STATUS_LOADING:
            mLoadingContainer.setVisibility(View.VISIBLE);
            mMainContainer.setVisibility(View.GONE);
            mNoNetworkContainer.setVisibility(View.GONE);
            break;
        case STATUS_GOT_DATA:
            mLoadingContainer.setVisibility(View.GONE);
            mMainContainer.setVisibility(View.VISIBLE);
            mNoNetworkContainer.setVisibility(View.GONE);
            break;
        case STATUS_NO_NETWORK_OR_DATA:
            mLoadingContainer.setVisibility(View.GONE);
            mMainContainer.setVisibility(View.GONE);
            mNoNetworkContainer.setVisibility(View.VISIBLE);
            break;
        default:
            break;
        }

    }
    
	private void getData(final boolean isPullDown){
	    if(mFeedAdapter.getCount()==0){
	        setStatus(Status.STATUS_LOADING);
	    }else{
	        setStatus(Status.STATUS_GOT_DATA);
	    }
	    
        long id = START_ID;
        int count = PAGE_COUNT * -1;
        if (!mIsStartRequest && mFeedAdapter.getCount() > 0) {
            if (isPullDown) {
                id = mFeedAdapter.getItem(0).getShowTime();
                count = PAGE_COUNT;
            } else {
                id = mFeedAdapter.getItem(mFeedAdapter.getCount() - 1).getShowTime();
                count = PAGE_COUNT * -1;
            }
        }
		
		GetFeedsRequest request=new GetFeedsRequest(id,count, new Listener<ArrayList<Feed>>(){

			@Override
			public void onResponse(ArrayList<Feed> feedList) {
				mListView.onRefreshComplete();
				setStatus(Status.STATUS_GOT_DATA);
				if(mIsStartRequest){
				    mIsStartRequest=false;
					//成功完成首次请求，clear启动时加载的cache数据
					mFeedAdapter.clear();
				}
				
				if(isPullDown){
					mFeedList.addAll(0, feedList);
				}else{
					mFeedList.addAll(feedList);
				}
				
			}
    		
    	}, new ErrorListener(){

			@Override
			public void onErrorResponse(VolleyError error) {
				mListView.onRefreshComplete();
				
                if (mFeedAdapter.getCount() == 0) {
                    setStatus(Status.STATUS_NO_NETWORK_OR_DATA);
                }

				Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_LONG).show();
			}
    		
    	});
    	
    	request.setTag(this);    	
    	mRequestQueue.add(request);
    }

}
