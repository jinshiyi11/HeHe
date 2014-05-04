package com.shuai.hehe.ui;

import java.util.ArrayList;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.shuai.base.view.BaseActivity;
import com.shuai.hehe.R;
import com.shuai.hehe.adapter.FeedAdapter;
import com.shuai.hehe.adapter.FeedAdapter.FeedList;
import com.shuai.hehe.base.ParallelAsyncTask;
import com.shuai.hehe.data.DataManager;
import com.shuai.hehe.data.Feed;
import com.shuai.hehe.data.StarFeed;
import com.shuai.hehe.protocol.GetFeedsRequest;
import com.shuai.hehe.protocol.ProtocolError;

/**
 * 我的收藏页面
 */
public class StarActivity extends BaseActivity {
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

    private View mIvBack;
    private View mTitleContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        
        setContentView(R.layout.activity_star);
        mContext=this;
        mIvBack=findViewById(R.id.iv_back);
        mIvBack.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        
        mTitleContainer = findViewById(R.id.rl_title);
        mTitleContainer.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
              //单击标题栏时返回顶部
                ListView listView=mListView.getRefreshableView();
                if(listView.getCount()>0){
                    listView.setSelection(0);
                }

            }
        });
        
        mNoNetworkContainer=(ViewGroup) findViewById(R.id.no_data_container);
        mLoadingContainer=(ViewGroup) findViewById(R.id.loading_container);
        mMainContainer=(ViewGroup) findViewById(R.id.main_container);
        
        mListView=(PullToRefreshListView) findViewById(R.id.listview);
        mListView.setMode(Mode.BOTH);
        mFeedAdapter=new FeedAdapter(mContext, mFeedList);
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
        
        long starTime = System.currentTimeMillis();
        int count = PAGE_COUNT * -1;
        if (mFeedAdapter.getCount() > 0) {
            if (isPullDown) {
                starTime = ((StarFeed)mFeedAdapter.getItem(0)).getStarredTime();
                count = PAGE_COUNT;
            } else {
                starTime = ((StarFeed)mFeedAdapter.getItem(mFeedAdapter.getCount() - 1)).getStarredTime();
                count = PAGE_COUNT * -1;
            }
        }
        
        GetStarFeedsTask task=new GetStarFeedsTask(starTime,count,isPullDown);
        
        DataManager.getInstance().executeDbTask(task);
        
    }
    
    class GetStarFeedsTask extends ParallelAsyncTask<Void, Void, ArrayList<StarFeed>>{
        private long mStarTime;
        private int mCount;
        private boolean mIsPullDown;
        
        public GetStarFeedsTask(long starTime,int count,boolean isPullDown){
            mStarTime=starTime;
            mCount=count;
            mIsPullDown=isPullDown;
        }
        
        @Override
        protected ArrayList<StarFeed> doInBackground(Void... params) {
            return DataManager.getInstance().getStarFeeds(mStarTime,mCount);
        }

        @Override
        protected void onPostExecute(ArrayList<StarFeed> feedList) {
            super.onPostExecute(feedList);
            
            mListView.onRefreshComplete();
            
            if(mIsPullDown){
                mFeedList.addAll(0, feedList);
            }else{
                mFeedList.addAll(feedList);
            }
            
            if (mFeedAdapter.getCount() == 0) {
                setStatus(StarActivity.Status.STATUS_NO_NETWORK_OR_DATA);
            }else{
                setStatus(StarActivity.Status.STATUS_GOT_DATA);
            }
        }
    }

}
