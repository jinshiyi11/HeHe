package com.shuai.hehe.adapter;

import java.util.ArrayList;

import uk.co.senab.photoview.PhotoView;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.shuai.hehe.data.PicInfo;

public class AlbumAdapter extends PagerAdapter {
    private Context mContext;
    private ArrayList<PicInfo> mPicInfos;

    public AlbumAdapter(Context context, ArrayList<PicInfo> picInfos) {
        mContext = context;
        mPicInfos = picInfos;
    }

    @Override
    public int getCount() {
        return mPicInfos.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        PicInfo info=mPicInfos.get(position);
        PhotoView photoView=new PhotoView(mContext);
        photoView.setBackgroundColor(android.R.color.black);
        container.addView(photoView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        ImageLoader.getInstance().displayImage(info.getBigPicUrl(), photoView,new ImageLoadingListener(){

            @Override
            public void onLoadingStarted(String imageUri, View view) {
                // TODO Auto-generated method stub
                
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                // TODO Auto-generated method stub
                
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                // TODO Auto-generated method stub
                
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {
                
            }
            
        });
        
        return photoView;
    }
}
