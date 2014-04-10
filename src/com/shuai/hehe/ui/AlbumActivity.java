package com.shuai.hehe.ui;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.shuai.hehe.HeHeApplication;
import com.shuai.hehe.R;
import com.shuai.hehe.adapter.AlbumAdapter;
import com.shuai.hehe.data.Constants;
import com.shuai.hehe.data.PicInfo;
import com.shuai.hehe.protocol.GetAlbumPicsRequest;

public class AlbumActivity extends Activity {
    private TextView mTvPageNum;
    private ViewPager mViewPager;
    AlbumAdapter mAlbumAdapter;
    private int mFeedId;
    private ArrayList<PicInfo> mPicInfos;
    
    /**
     * 异步请求队列
     */
    private RequestQueue mRequestQueue;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.album_activity);
        mRequestQueue=HeHeApplication.getRequestQueue();
        
        mTvPageNum=(TextView) findViewById(R.id.tv_pagenum);
        mTvPageNum.setVisibility(View.INVISIBLE);
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
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
        Intent intent=getIntent();
        mFeedId=intent.getIntExtra(Constants.FEED_ID, -1);
        
        getData();
    }
    
    private void getData(){
        GetAlbumPicsRequest request=new GetAlbumPicsRequest(mFeedId, new Listener<ArrayList<PicInfo>>() {

            @Override
            public void onResponse(ArrayList<PicInfo> response) {
                mTvPageNum.setVisibility(View.VISIBLE);
                mAlbumAdapter=new AlbumAdapter(AlbumActivity.this, response);
                mViewPager.setAdapter(mAlbumAdapter);
                onPageSelected(0);
            }
        }, new ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(AlbumActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
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
        }
    }

}
