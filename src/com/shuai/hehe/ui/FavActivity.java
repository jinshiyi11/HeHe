package com.shuai.hehe.ui;

import java.util.ArrayList;
import java.util.Iterator;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ListView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.shuai.base.view.BaseActivity;
import com.shuai.hehe.R;
import com.shuai.hehe.adapter.FeedAdapter;
import com.shuai.hehe.adapter.FeedAdapter.FeedList;
import com.shuai.hehe.base.ParallelAsyncTask;
import com.shuai.hehe.data.DataManager;
import com.shuai.hehe.data.DataManager.OnStarFeedChangedListener;
import com.shuai.hehe.data.Feed;

/**
 * 我的收藏页面
 */
public class FavActivity extends BaseActivity implements OnStarFeedChangedListener {
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
    private TextView mTvTitle;
    
    private DataManager mDataManager;
    private GetStarFeedsTask mDbTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        
        setContentView(R.layout.activity_star);
        mContext=this;
        mDataManager=DataManager.getInstance();
        mIvBack=findViewById(R.id.iv_back);
        mIvBack.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        
        mTvTitle=(TextView) findViewById(R.id.tv_title);
        mTvTitle.setText(R.string.my_star);
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
        
        mDataManager.addStarFeedChangedListener(this);
        mListView.setRefreshing(false);
    }

    @Override
    protected void onDestroy() {
        mDataManager.removeStarFeedChangedListener(this);
        super.onDestroy();
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
                starTime = mFeedAdapter.getItem(0).getStarTime();
                count = PAGE_COUNT;
            } else {
                starTime = mFeedAdapter.getItem(mFeedAdapter.getCount() - 1).getStarTime();
                count = PAGE_COUNT * -1;
            }
        }
        
        mDbTask=new GetStarFeedsTask(starTime,count,isPullDown);
        
        DataManager.getInstance().executeDbTask(mDbTask);
        
    }
    
    class GetStarFeedsTask extends ParallelAsyncTask<Object, Object, ArrayList<Feed>>{
        private long mStarTime;
        private int mCount;
        private boolean mIsPullDown;
        
        public GetStarFeedsTask(long starTime,int count,boolean isPullDown){
            mStarTime=starTime;
            mCount=count;
            mIsPullDown=isPullDown;
        }
        
        @Override
        protected ArrayList<Feed> doInBackground(Object... params) {
            return DataManager.getInstance().getStarFeeds(mStarTime,mCount);
        }

        @Override
        protected void onPostExecute(ArrayList<Feed> feedList) {
            super.onPostExecute(feedList);
            
            mListView.onRefreshComplete();
            
            if(mIsPullDown){
                mFeedList.addAll(0, feedList);
            }else{
                mFeedList.addAll(feedList);
            }
            
            updateStatus();
        }
    }
    
    private void updateStatus(){
        if (mFeedAdapter.getCount() == 0) {
            setStatus(FavActivity.Status.STATUS_NO_NETWORK_OR_DATA);
        }else{
            setStatus(FavActivity.Status.STATUS_GOT_DATA);
        }
    }

    @Override
    public void onStarFeedAdded(Feed feed) {
        mFeedList.add(0, feed);
        mFeedAdapter.notifyDataSetChanged();
        updateStatus();
    }

    @Override
    public void onStarFeedRemoved(long feedId) {
        for(Iterator<Feed> it=mFeedList.iterator();it.hasNext();){
            Feed item=it.next();
            if(item.getId()==feedId){
                it.remove();
                mFeedAdapter.notifyDataSetChanged();
                updateStatus();
                break;
            }
        }
    }

}
