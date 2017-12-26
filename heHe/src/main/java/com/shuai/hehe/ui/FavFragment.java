package com.shuai.hehe.ui;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadmoreListener;
import com.shuai.hehe.R;
import com.shuai.hehe.adapter.FeedAdapter.FeedList;
import com.shuai.hehe.adapter.TestAdapter;
import com.shuai.hehe.base.ParallelAsyncTask;
import com.shuai.hehe.data.DataManager;
import com.shuai.hehe.data.DataManager.OnStarFeedChangedListener;
import com.shuai.hehe.data.Feed;
import com.shuai.hehe.ui.base.BaseTabFragment;
import com.shuyu.gsyvideoplayer.GSYVideoManager;

import java.util.ArrayList;
import java.util.Iterator;

import static com.shuai.hehe.ui.FeedFragment.KEY_TITLE;

/**
 * 我的收藏页面
 */
public class FavFragment extends BaseTabFragment implements OnStarFeedChangedListener {
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
    
    /**
     * 每次获取的feed个数
     */
    private static final int PAGE_COUNT = 30;

    private Toolbar mToolbar;
    private TextView mTvTitle;

    private RefreshLayout mRefreshLayout;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private FeedList mFeedList = new FeedList();
    private TestAdapter mFeedAdapter;
    private DataManager mDataManager;
    private GetStarFeedsTask mDbTask;

    public FavFragment() {
        super(R.layout.fragment_fav);
    }

    @Override
    protected void onInit(View root, Bundle savedInstanceState) {
        mDataManager=DataManager.getInstance();

        mToolbar = root.findViewById(R.id.toolbar);
        mTvTitle=root.findViewById(R.id.tv_title);
        mTvTitle.setText(getArguments().getString(KEY_TITLE));
        mToolbar.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onTitleClicked();
            }
        });
        
        mNoNetworkContainer=(ViewGroup) root.findViewById(R.id.no_data_container);
        mLoadingContainer=(ViewGroup) root.findViewById(R.id.loading_container);
        mMainContainer=(ViewGroup) root.findViewById(R.id.main_container);

        mRefreshLayout = root.findViewById(R.id.refresh_layout);
        mRecyclerView = root.findViewById(R.id.recyclerview);
        mFeedAdapter = new TestAdapter(mContext, mFeedList);
        mLayoutManager = new LinearLayoutManager(mContext);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mFeedAdapter);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                mFeedAdapter.onScrolled(mLayoutManager.findFirstVisibleItemPosition(),
                        mLayoutManager.findLastVisibleItemPosition());
            }
        });

        mRefreshLayout.setOnRefreshLoadmoreListener(new OnRefreshLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                getData(false);
            }

            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                getData(true);
            }
        });

        mDataManager.addStarFeedChangedListener(this);
        mRefreshLayout.autoRefresh();
    }

    @Override
    public void onDestroyView() {
        mDataManager.removeStarFeedChangedListener(this);
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
    
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        final UMSocialService mController = UMServiceFactory.getUMSocialService("com.umeng.share");
//        /**使用SSO授权必须添加如下代码 */
//        UMSsoHandler ssoHandler = mController.getConfig().getSsoHandler(requestCode) ;
//        if(ssoHandler != null){
//           ssoHandler.authorizeCallBack(requestCode, resultCode, data);
//        }
//    }

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
        if(mFeedList.size()==0){
            setStatus(Status.STATUS_LOADING);
        }else{
            setStatus(Status.STATUS_GOT_DATA);
        }
        
        long starTime = System.currentTimeMillis();
        int count = PAGE_COUNT * -1;
        if (mFeedList.size() > 0) {
            if (isPullDown) {
                starTime = mFeedList.get(0).getStarTime();
                count = PAGE_COUNT;
            } else {
                starTime = mFeedList.get(mFeedList.size() - 1).getStarTime();
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
            if(mIsPullDown){
                mRefreshLayout.finishRefresh();
                mFeedList.addAll(0, feedList);
                mFeedAdapter.notifyItemRangeInserted(0,feedList.size());
            }else{
                mRefreshLayout.finishLoadmore();
                int start=feedList.size();
                mFeedList.addAll(feedList);
                mFeedAdapter.notifyItemRangeInserted(start,feedList.size());
            }
            
            updateStatus();
        }
    }

    /**
     * 响应单击标题栏
     */
    public void onTitleClicked() {
        //单击标题栏时返回顶部
        if(mFeedList.size()>0) {
            mRecyclerView.scrollToPosition(0);
            //mRecyclerView.smoothScrollToPosition(0);
        }
    }
    
    private void updateStatus(){
        if (mFeedList.size() == 0) {
            setStatus(FavFragment.Status.STATUS_NO_NETWORK_OR_DATA);
        }else{
            setStatus(FavFragment.Status.STATUS_GOT_DATA);
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
