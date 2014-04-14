package com.shuai.hehe.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView.ScaleType;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.assist.ImageLoadingProgressListener;
import com.shuai.base.view.NetworkPhotoView;
import com.shuai.hehe.R;
import com.shuai.hehe.data.PicInfo;

public class AlbumAdapter extends PagerAdapter {
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private ArrayList<PicInfo> mPicInfos;
    
    private DisplayImageOptions mDisplayImageOptions;

    public AlbumAdapter(Context context, ArrayList<PicInfo> picInfos) {
        mContext = context;
        mPicInfos = picInfos;
        mLayoutInflater=(LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
    
    private DisplayImageOptions getDisplayImageOptions(){
        if(mDisplayImageOptions!=null)
            return mDisplayImageOptions;
        
        mDisplayImageOptions=new DisplayImageOptions.Builder()
            .cacheInMemory(true)
            .cacheOnDisc(true)
            //.showImageOnLoading(R.drawable.ic_image_stub)
            .showImageOnFail(R.drawable.ic_image_load_failed)
            .build();
        
        return mDisplayImageOptions;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        PicInfo info=mPicInfos.get(position);
        
        NetworkPhotoView photoView=new NetworkPhotoView(mContext);
        photoView.setBackgroundColor(android.R.color.black);
        container.addView(photoView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        photoView.setScaleType(ScaleType.CENTER_INSIDE);
        ImageLoader.getInstance().displayImage(info.getBigPicUrl(), photoView,getDisplayImageOptions(),new ImageLoadingListener() {
            
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                // TODO Auto-generated method stub
                
            }
            
            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                if(view!=null){
                    NetworkPhotoView photoView=(NetworkPhotoView) view;
                    photoView.setScaleType(ScaleType.CENTER_INSIDE);
                }
                
            }
            
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                if(view!=null){
                    NetworkPhotoView photoView=(NetworkPhotoView) view;
                    photoView.setScaleType(ScaleType.FIT_CENTER);
                }
                
            }
            
            @Override
            public void onLoadingCancelled(String imageUri, View view) {
                
            }
        },new ImageLoadingProgressListener(){

            @Override
            public void onProgressUpdate(String imageUri, View view, int current, int total) {
                if(view!=null){
                    NetworkPhotoView photoView=(NetworkPhotoView) view;
                    
                    if(total>0)
                        photoView.setProgress((int) (current*100.0/total));
                }
            }
            
        });
        
        return photoView;
    }
}
