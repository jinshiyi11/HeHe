package com.shuai.hehe;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

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
