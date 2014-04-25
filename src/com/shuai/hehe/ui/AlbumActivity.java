package com.shuai.hehe.ui;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
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
    private TextView mTvPageNum;
    private ViewPager mViewPager;
    
    /**
     * 图片描述
     */
    private ExpandableTextView mEtvDesc;
    AlbumAdapter mAlbumAdapter;
    private int mFeedId;
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
        setContentView(R.layout.album_activity);
        mRequestQueue=HeHeApplication.getRequestQueue();
        
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
                //TODO:单击隐藏pagenum和图片描述
                Log.d("ggg", "msg");
                
            }
        });
        Intent intent=getIntent();
        mFeedId=intent.getIntExtra(Constants.FEED_ID, -1);
        
        getData();
    }
    
    private void getData(){
        GetAlbumPicsRequest request=new GetAlbumPicsRequest(mFeedId, new Listener<ArrayList<PicInfo>>() {

            @Override
            public void onResponse(ArrayList<PicInfo> response) {
                mPicInfos = response;
                if (mPicInfos.size() > 0) {
                    mTvPageNum.setVisibility(View.VISIBLE);
                    mEtvDesc.setVisibility(View.VISIBLE);
                    mAlbumAdapter = new AlbumAdapter(AlbumActivity.this, mPicInfos);
                    mViewPager.setAdapter(mAlbumAdapter);
                    onPageSelected(0);
                }else{
                    Toast.makeText(mContext, R.string.error_data, Toast.LENGTH_SHORT).show();
                }
            }
        }, new ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
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
        if(mAlbumAdapter!=null && mAlbumAdapter.getCount()>0){
            mTvPageNum.setText(String.format("%d/%d", position+1,mAlbumAdapter.getCount()));
            
            PicInfo info=mPicInfos.get(position);
            mEtvDesc.setText(Html.fromHtml(info.getPicDescription()));
        }
    }

}
