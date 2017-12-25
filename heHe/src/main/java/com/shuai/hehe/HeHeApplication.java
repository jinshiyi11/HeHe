package com.shuai.hehe;

import android.content.Context;
import android.os.Process;
import android.support.annotation.NonNull;
import android.support.multidex.MultiDexApplication;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.DefaultRefreshHeaderCreater;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.shuai.hehe.data.Config;
import com.shuai.hehe.data.DataManager;
import com.shuai.hehe.logic.UserManager;
import com.shuai.utils.AppUtils;
import com.shuai.utils.ConnectionChangeMonitor;
import com.shuai.utils.CustomImageDownloader;
import com.shuai.utils.ProcessUtils;
import com.umeng.analytics.MobclickAgent;

import me.jessyan.progressmanager.ProgressManager;
import okhttp3.OkHttpClient;

public class HeHeApplication extends MultiDexApplication {

    private static Context mContext;
    private static RequestQueue mRequestQueue;
    private static OkHttpClient mOkHttpClient;
    private ConnectionChangeMonitor mConnectionChangeMonitor;

    public HeHeApplication() {
    }

    public static RequestQueue getRequestQueue() {
        if(mRequestQueue!=null)
            return mRequestQueue;
        else {
            //不应该为null
            throw new NullPointerException("mRequestQueue is null!");
        }
    }

    public static Context getAppContext() {
        return mContext;
    }

    public static OkHttpClient getOkHttpClient(){
        return mOkHttpClient;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;

        int myPid = Process.myPid();
        Log.d("myPid", String.valueOf(myPid));
        String packageName = AppUtils.getApplicationInfo(mContext).packageName;
        String processName = ProcessUtils.getProcessNameByPid(mContext, myPid);

        //不是service子进程才执行初始化，防止多次初始化
        if (processName != null && processName.equals(packageName)) {
            init();
        }
    }

    private void init() {
        // 初始化网络异步请求对象
        mRequestQueue = Volley.newRequestQueue(this);
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        ProgressManager.getInstance().with(builder);
        mOkHttpClient=builder.build();

        Config.getInstance().init(this);
        UserManager userManager = UserManager.getInstance();
        userManager.init(this);
        initImageLoader();

        DataManager.init(this);
        mConnectionChangeMonitor = new ConnectionChangeMonitor(this);
        mConnectionChangeMonitor.startMonitor();

        //因为包含fragment，禁止默认的页面统计方式
        MobclickAgent.openActivityDurationTrack(false);

//        EventBus.getDefault().register(this);

        if(!userManager.isLogined())
            userManager.autoLogin();

        SmartRefreshLayout.setDefaultRefreshHeaderCreater(new DefaultRefreshHeaderCreater() {
            @NonNull
            @Override
            public RefreshHeader createRefreshHeader(Context context, RefreshLayout layout) {
                //return new MaterialHeader(context);
                return new ClassicsHeader(context);
            }
        });
    }

    private void initImageLoader() {
//        Map<String, String> headers =new HashMap<String, String>();
//        headers.put(Constants.HTTP_REFERER_KEY,Constants.HTTP_REFERER_VALUE);
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
//                .extraForDownloader(headers)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
                .imageDownloader(new CustomImageDownloader(mContext))
                .defaultDisplayImageOptions(defaultOptions)
                .threadPoolSize(5)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .memoryCache(new WeakMemoryCache())
                .memoryCacheSize(3 * 1024 * 1024)
                .denyCacheImageMultipleSizesInMemory()
                //.discCacheSize(10 * 1024 * 1024)
                .diskCacheFileCount(150)
                .tasksProcessingOrder(QueueProcessingType.FIFO)
                .build();
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
