package com.shuai.hehe;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import android.app.Application;

public class HeHeApplication extends Application {

    /**
     * 网络异步请求对象,用于协议数据的获取
     */
    private static RequestQueue mRequestQueue;

    public HeHeApplication() {
    }

    public static RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            //不应该为null
            throw new NullPointerException("mRequestQueue is null!");
        }
        return mRequestQueue;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        //初始化网络异步请求对象
        mRequestQueue = Volley.newRequestQueue(this);
        
        initImageLoader();
    }
    
    private void initImageLoader() {
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisc(true)
                .build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
                .defaultDisplayImageOptions(defaultOptions).threadPriority(Thread.NORM_PRIORITY - 2)
                .memoryCacheSize(4 * 1024 * 1024).denyCacheImageMultipleSizesInMemory().discCacheSize(10 * 1024 * 1024)
                .discCacheFileCount(100).tasksProcessingOrder(QueueProcessingType.FIFO).build();
        ImageLoader.getInstance().init(config);
    }

//    @Override
//    public void onTerminate() {
//        if (mRequestQueue != null) {
//            mRequestQueue.stop();
//        }
//
//        super.onTerminate();
//    }

}