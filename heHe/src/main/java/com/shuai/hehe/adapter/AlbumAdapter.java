package com.shuai.hehe.adapter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import pl.droidsonroids.gif.GifDrawable;
import uk.co.senab.photoview.PhotoViewAttacher;
import uk.co.senab.photoview.PhotoViewAttacher.OnViewTapListener;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView.ScaleType;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.shuai.base.view.DoubleTapListener;
import com.shuai.base.view.NetworkPhotoView;
import com.shuai.hehe.R;
import com.shuai.hehe.data.PicInfo;
import com.shuai.utils.DisplayUtils;

import pl.droidsonroids.gif.GifDrawable;;

public class AlbumAdapter extends PagerAdapter {
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private ArrayList<PicInfo> mPicInfos;

    public AlbumAdapter(Context context, ArrayList<PicInfo> picInfos) {
        mContext = context;
        mPicInfos = picInfos;
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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

//    private DisplayImageOptions getDisplayImageOptions(){
//        if(mDisplayImageOptions!=null)
//            return mDisplayImageOptions;
//
//        mDisplayImageOptions=new DisplayImageOptions.Builder()
//            .cacheInMemory(true)
//            .cacheOnDisc(true)
//            //.showImageOnLoading(R.drawable.ic_image_stub)
//            .showImageOnFail(R.drawable.ic_image_load_failed)
//            .build();
//
//        return mDisplayImageOptions;
//    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        PicInfo info = mPicInfos.get(position);

        final NetworkPhotoView photoView = new NetworkPhotoView(mContext);
        photoView.setBackgroundColor(mContext.getResources().getColor(R.color.black_color));
        photoView.setProgressRadius(DisplayUtils.dp2px(mContext, 25));
        photoView.setProgressCircleWidth(DisplayUtils.dp2px(mContext, 2));
        //设置双击图片时的缩放策略
        photoView.setMinimumScale(0.75f);
        photoView.setMediumScale(2.0f);
        photoView.setMaximumScale(3f);
        photoView.setOnDoubleTapListener(new DoubleTapListener((PhotoViewAttacher) photoView.getIPhotoViewImplementation()));

        container.addView(photoView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        photoView.setScaleType(ScaleType.CENTER_INSIDE);
        Glide.with(mContext).load(info.getBigPicUrl())
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        photoView.onLoadingFailed();
                        photoView.setScaleType(ScaleType.CENTER_INSIDE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        photoView.onLoadingComplete();
                        photoView.setScaleType(ScaleType.FIT_CENTER);
                        return false;
                    }
                })
                .into(photoView);
//        ImageLoader.getInstance().displayImage(info.getBigPicUrl(), photoView,getDisplayImageOptions(),new ImageLoadingListener() {
//
//            @Override
//            public void onLoadingStarted(String imageUri, View view) {
//                // TODO Auto-generated method stub
//
//            }
//
//            @Override
//            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
//                if(view!=null){
//                    NetworkPhotoView photoView=(NetworkPhotoView) view;
//                    photoView.onLoadingFailed();
//                    photoView.setScaleType(ScaleType.CENTER_INSIDE);
//                }
//
//            }
//
//            @Override
//            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
//                if(view!=null){
//                    NetworkPhotoView photoView=(NetworkPhotoView) view;
//                    //判断是否是gif图片
//                    if(isGif(imageUri)){
//                        DiscCacheAware discCache = ImageLoader.getInstance().getDiscCache();
//                        File file = discCache.get(imageUri);
//                        if(file!=null && file.exists()){
//                            try {
//                                photoView.setImageDrawable(new GifDrawable(file));
//                            } catch (IOException e) {
//                                // TODO Auto-generated catch block
//                                e.printStackTrace();
//                            }
//                        }
//                    }
//                    photoView.onLoadingComplete();
//                    photoView.setScaleType(ScaleType.FIT_CENTER);
//                }
//
//            }
//
//            @Override
//            public void onLoadingCancelled(String imageUri, View view) {
//
//            }
//        },new ImageLoadingProgressListener(){
//
//            @Override
//            public void onProgressUpdate(String imageUri, View view, int current, int total) {
//                if(view!=null){
//                    NetworkPhotoView photoView=(NetworkPhotoView) view;
//
//                    if(total>0)
//                        photoView.setProgress((int) (current*100.0/total));
//                }
//            }
//
//        });

        photoView.setOnViewTapListener(new OnViewTapListener() {

            @SuppressLint("NewApi")
            @Override
            public void onViewTap(View view, float x, float y) {
                ViewGroup parentViewPager = (ViewGroup) view.getParent();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                    parentViewPager.callOnClick();
                } else {
                    parentViewPager.performClick();
                }
            }
        });

        return photoView;
    }

    //该url对应的是否是gif图
//    private boolean isGif(String imageUri) {
//        return imageUri.toLowerCase().endsWith(".gif");
//    }
}
