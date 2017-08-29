package com.shuai.hehe.ui;

import java.util.ArrayList;
import java.util.Iterator;

import android.content.Context;
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
import com.shuai.hehe.data.DataManager;
import com.shuai.hehe.data.Stat;
import com.shuai.hehe.data.DataManager.OnStarFeedChangedListener;
import com.shuai.hehe.data.Feed;
import com.shuai.hehe.protocol.GetFeedsRequest;
import com.shuai.hehe.protocol.ProtocolError;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.video.base.GSYVideoPlayer;
import com.umeng.analytics.MobclickAgent;

public class FeedFragment extends Fragment implements OnStarFeedChangedListener {
	private Context mContext;
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
    private DataManager mDataManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_feed, container,false);
        mContext=getActivity();
        mRequestQueue=HeHeApplication.getRequestQueue();
        mDataManager=DataManager.getInstance();
        
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
        
        mDataManager.addStarFeedChangedListener(this);
        mListView.setRefreshing(false);
        return view;
    }
    
    @Override
	public void onDestroyView() {
        GSYVideoPlayer.releaseAllVideos();
        mDataManager.removeStarFeedChangedListener(this);
    	mRequestQueue.cancelAll(this);
		super.onDestroyView();
	}

    @Override
    public void onResume() {
        super.onResume();
        GSYVideoManager.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        GSYVideoManager.onPause();
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
	    //如果是首次加载，先读cache
	    if (mIsStartRequest && mFeedAdapter.getCount()==0){
	        ArrayList<Feed> feedList = GetFeedsRequest.loadCache(mContext);
	        if(feedList!=null &&feedList.size()>0){
	            mFeedList.addAll(0, feedList);
	            mFeedAdapter.notifyDataSetChanged();
	        }
	    }
	    
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
		
		GetFeedsRequest request=new GetFeedsRequest(mContext,id,count, new Listener<ArrayList<Feed>>(){

			@Override
			public void onResponse(ArrayList<Feed> feedList) {
				mListView.onRefreshComplete();
				if(mIsStartRequest){
				    mIsStartRequest=false;
					//成功完成首次请求，clear启动时加载的cache数据
					mFeedAdapter.clear();
				}
				
                if (isPullDown) {
                    if (feedList.size() > 0) {
                        mFeedList.addAll(0, feedList);
                        //保存最新的n条新鲜事
                        GetFeedsRequest.saveCache(mContext, mFeedList);
                    } else {
                        Toast.makeText(mContext, R.string.no_new_data, Toast.LENGTH_SHORT).show();
                    }
                } else {
					mFeedList.addAll(feedList);
				}
				
                if (feedList.size() > 0)
                    mFeedAdapter.notifyDataSetChanged();	

                setStatus(Status.STATUS_GOT_DATA);
			}
    		
    	}, new ErrorListener(){

			@Override
			public void onErrorResponse(VolleyError error) {
			    MobclickAgent.onEvent(mContext, Stat.EVENT_GET_FEED_ERROR);
				mListView.onRefreshComplete();
				
                if (mFeedAdapter.getCount() == 0) {
                    setStatus(Status.STATUS_NO_NETWORK_OR_DATA);
                }

				Toast.makeText(getActivity(), ProtocolError.getErrorMessage(mContext, error), Toast.LENGTH_LONG).show();
			}
			
    	});
    	
    	request.setTag(this);    	
    	mRequestQueue.add(request);
    }

	/**
	 * 响应单击标题栏
	 */
    public void onTitleClicked() {
        //单击标题栏时返回顶部
        ListView listView=mListView.getRefreshableView();
        if(listView.getCount()>0){
            listView.setSelection(0);
        }
  
    }

    @Override
    public void onStarFeedAdded(Feed feed) {
        mFeedAdapter.updateStarFeedState(mListView.getRefreshableView(), feed.getId());
    }

    @Override
    public void onStarFeedRemoved(long feedId) {
        mFeedAdapter.updateStarFeedState(mListView.getRefreshableView(), feedId);
    }

}
