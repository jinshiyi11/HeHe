package com.shuai.hehe.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadmoreListener;
import com.shuai.hehe.HeHeApplication;
import com.shuai.hehe.R;
import com.shuai.hehe.adapter.FeedAdapter.FeedList;
import com.shuai.hehe.adapter.TestAdapter;
import com.shuai.hehe.data.DataManager;
import com.shuai.hehe.data.DataManager.OnStarFeedChangedListener;
import com.shuai.hehe.data.Feed;
import com.shuai.hehe.data.Stat;
import com.shuai.hehe.protocol.GetFeedsRequest;
import com.shuai.hehe.protocol.ProtocolUtils;
import com.shuai.hehe.ui.base.BaseTabFragment;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.video.base.GSYVideoPlayer;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;

public class FeedFragment extends BaseTabFragment implements OnStarFeedChangedListener {
    public static final String KEY_TYPE = "FeedFragmentType";
    public static final String KEY_TITLE = "Title";
    public static final int TYPE_VIDEO = 0;
    public static final int TYPE_ALBUM = 1;
    public static final int TYPE_FAVORITE = 2;

    private Context mContext;
    private Toolbar mToolbar;
    private TextView mTvTitle;
    private int mType;
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

    private RefreshLayout mRefreshLayout;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private FeedList mFeedList = new FeedList();
    private TestAdapter mFeedAdapter;

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
    private boolean mIsStartRequest = true;

    /**
     * 异步请求队列
     */
    private RequestQueue mRequestQueue;
    private DataManager mDataManager;

    public FeedFragment() {
        super(R.layout.fragment_feed);
    }

    @Override
    protected void onInit(View root, Bundle savedInstanceState) {
        mContext = getActivity();
        mRequestQueue = HeHeApplication.getRequestQueue();
        mDataManager = DataManager.getInstance();

        mType = getArguments().getInt(KEY_TYPE);
        mToolbar = root.findViewById(R.id.toolbar);
        mTvTitle = root.findViewById(R.id.tv_title);
        mTvTitle.setText(getArguments().getString(KEY_TITLE));
        mToolbar.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onTitleClicked();
            }
        });
//        ((AppCompatActivity)getActivity()).setSupportActionBar(mToolbar);
//        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mNoNetworkContainer = (ViewGroup) root.findViewById(R.id.no_network_container);
        mLoadingContainer = (ViewGroup) root.findViewById(R.id.loading_container);
        mMainContainer = (ViewGroup) root.findViewById(R.id.main_container);

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

        mNoNetworkContainer.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                mRefreshLayout.autoRefresh();
            }
        });

        mDataManager.addStarFeedChangedListener(this);
        mRefreshLayout.autoRefresh();
    }

    @Override
    public void onDestroyView() {
        GSYVideoPlayer.releaseAllVideos();
        if (mDataManager != null) {
            mDataManager.removeStarFeedChangedListener(this);
        }

        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(this);
        }
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

    private void getData(final boolean isPullDown) {
        if (mFeedList.size() == 0) {
            setStatus(Status.STATUS_LOADING);
        } else {
            setStatus(Status.STATUS_GOT_DATA);
        }

        long id = START_ID;
        int count = PAGE_COUNT * -1;
        if (!mIsStartRequest && mFeedList.size() > 0) {
            if (isPullDown) {
                id = mFeedList.get(0).getShowTime();
                count = PAGE_COUNT;
            } else {
                id = mFeedList.get(mFeedList.size() - 1).getShowTime();
                count = PAGE_COUNT * -1;
            }
        }

        GetFeedsRequest request = new GetFeedsRequest(mContext, mType, id, count, new Listener<ArrayList<Feed>>() {

            @Override
            public void onResponse(ArrayList<Feed> feedList) {
                if (isPullDown) {
                    mRefreshLayout.finishRefresh();
                } else {
                    mRefreshLayout.finishLoadmore();
                }
                if (mIsStartRequest) {
                    mIsStartRequest = false;
                    //成功完成首次请求，clear启动时加载的cache数据
                    mFeedList.clear();
                    mFeedAdapter.notifyDataSetChanged();
                }

                if (isPullDown) {
                    if (feedList.size() > 0) {
                        mFeedList.addAll(0, feedList);
                        //保存最新的n条新鲜事
                        //GetFeedsRequest.saveCache(mContext, mFeedList);
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

        }, new ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                MobclickAgent.onEvent(mContext, Stat.EVENT_GET_FEED_ERROR);
                if (isPullDown) {
                    mRefreshLayout.finishRefresh();
                } else {
                    mRefreshLayout.finishLoadmore();
                }

                if (mFeedList.size() == 0) {
                    setStatus(Status.STATUS_NO_NETWORK_OR_DATA);
                }

                Toast.makeText(getActivity(), ProtocolUtils.getErrorInfo(error).getErrorMessage(), Toast.LENGTH_LONG).show();
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
        if (mFeedList.size() > 0) {
            mRecyclerView.scrollToPosition(0);
            //mRecyclerView.smoothScrollToPosition(0);
        }
    }

    @Override
    public void onStarFeedAdded(Feed feed) {
        updateStarFeedState(feed.getId());
    }

    @Override
    public void onStarFeedRemoved(long feedId) {
        updateStarFeedState(feedId);
    }

    private void updateStarFeedState(long feedId) {
        for (int i = 0; i < mFeedList.size(); i++) {
            if (mFeedList.get(i).getId() == feedId) {
                mFeedAdapter.notifyItemChanged(i);
                break;
            }
        }
    }

}
