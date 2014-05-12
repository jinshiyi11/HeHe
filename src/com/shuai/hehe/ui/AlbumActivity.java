package com.shuai.hehe.ui;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.shuai.base.view.BaseActivity;
import com.shuai.base.view.ExpandableTextView;
import com.shuai.hehe.HeHeApplication;
import com.shuai.hehe.R;
import com.shuai.hehe.adapter.AlbumAdapter;
import com.shuai.hehe.data.Constants;
import com.shuai.hehe.data.PicInfo;
import com.shuai.hehe.protocol.GetAlbumPicsRequest;
import com.shuai.hehe.protocol.ProtocolError;

public class AlbumActivity extends BaseActivity {
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
    /**
     * 当前显示的是第几张图片(如：2/9)
     */
    private TextView mTvPageNum;
    private LinearLayout mLlPageNum;
    private ViewPager mViewPager;
    
    /**
     * 图片描述
     */
    private ExpandableTextView mEtvDesc;
    
    /**
     * 是否显示当前是第几张图片和图片描述(可通过单击ViewPager显示和隐藏该信息)
     */
    private boolean mShowPicInfo=true;
    private AlbumAdapter mAlbumAdapter;
    private long mFeedId;
    private ArrayList<PicInfo> mPicInfos;
    
    /**
     * 异步请求队列
     */
    private RequestQueue mRequestQueue;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mContext=this;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);
        mRequestQueue=HeHeApplication.getRequestQueue();
        
        mNoNetworkContainer=(ViewGroup) findViewById(R.id.no_network_container);
        mLoadingContainer=(ViewGroup) findViewById(R.id.loading_container);
        mMainContainer=(ViewGroup) findViewById(R.id.main_container);
        mLlPageNum=(LinearLayout) findViewById(R.id.ll_pagenum);
        mTvPageNum=(TextView) findViewById(R.id.tv_pagenum);
        mTvPageNum.setVisibility(View.INVISIBLE);
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mEtvDesc=(ExpandableTextView) findViewById(R.id.etv_desc);
        mEtvDesc.setVisibility(View.INVISIBLE);
        mViewPager.setOffscreenPageLimit(2);
        mViewPager.setOnPageChangeListener(new OnPageChangeListener(){

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                AlbumActivity.this.onPageSelected(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                
            }
            
        });
        
        mViewPager.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                //单击显示或隐藏当前是第几张图片和图片描述
                mShowPicInfo=!mShowPicInfo;
                if(mShowPicInfo){
                    Animation fromTopAnimation = AnimationUtils.loadAnimation(mContext, R.anim.slide_in_from_top);
                    fromTopAnimation.setFillAfter(true);
                    mLlPageNum.startAnimation(fromTopAnimation);
                    
                    Animation fromBottomAnimation = AnimationUtils.loadAnimation(mContext, R.anim.slide_in_from_bottom);
                    fromBottomAnimation.setFillAfter(true);
                    mEtvDesc.startAnimation(fromBottomAnimation);
                }else{
                    Animation fromTopAnimation = AnimationUtils.loadAnimation(mContext, R.anim.slide_out_from_top);
                    fromTopAnimation.setFillAfter(true);
                    mLlPageNum.startAnimation(fromTopAnimation);
                    
                    Animation fromBottomAnimation = AnimationUtils.loadAnimation(mContext, R.anim.slide_out_from_bottom);
                    fromBottomAnimation.setFillAfter(true);
                    mEtvDesc.startAnimation(fromBottomAnimation);
                }
            }
        });
        
        mNoNetworkContainer.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                getData();
            }
        });
        Intent intent=getIntent();
        mFeedId=intent.getLongExtra(Constants.FEED_ID, -1);
        
        getData();
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
    
    private void getData(){
        if(mAlbumAdapter==null || mAlbumAdapter.getCount()==0){
            setStatus(Status.STATUS_LOADING);
        }
        
        GetAlbumPicsRequest request=new GetAlbumPicsRequest(mFeedId, new Listener<ArrayList<PicInfo>>() {

            @Override
            public void onResponse(ArrayList<PicInfo> response) {
                mPicInfos = response;
                if (mPicInfos.size() > 0) {
                    setStatus(Status.STATUS_GOT_DATA);
                    mTvPageNum.setVisibility(View.VISIBLE);
                    //mEtvDesc.setVisibility(View.VISIBLE);
                    mAlbumAdapter = new AlbumAdapter(mContext, mPicInfos);
                    mViewPager.setAdapter(mAlbumAdapter);
                    onPageSelected(0);
                }else{
                    setStatus(Status.STATUS_NO_NETWORK_OR_DATA);
                    Toast.makeText(mContext, R.string.error_data, Toast.LENGTH_SHORT).show();
                }
            }
        }, new ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                setStatus(Status.STATUS_NO_NETWORK_OR_DATA);
                Toast.makeText(AlbumActivity.this, ProtocolError.getErrorMessage(AlbumActivity.this, error), Toast.LENGTH_SHORT).show();
            }
        });
        
        request.setTag(this);       
        mRequestQueue.add(request);
    }

    @Override
    protected void onDestroy() {
        mRequestQueue.cancelAll(this);
        super.onDestroy();
    }
    
    private void onPageSelected(int position) {
        if (mAlbumAdapter != null && mAlbumAdapter.getCount() > 0) {
            mTvPageNum.setText(String.format("%d/%d", position + 1, mAlbumAdapter.getCount()));

            PicInfo info = mPicInfos.get(position);
            String desc = info.getPicDescription();
            if (!TextUtils.isEmpty(desc)) {
                mEtvDesc.setText(Html.fromHtml(info.getPicDescription()));
                mEtvDesc.setVisibility(View.VISIBLE);
            } else {
                mEtvDesc.setVisibility(View.INVISIBLE);
            }
        }
    }

}
