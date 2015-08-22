package com.shuai.base.view;

import android.os.SystemClock;
import android.view.View;

/**
 * 处理快速在某个控件上双击2次(或多次)会导致onClick被触发2次(或多次)的问题
 * 通过判断2次click事件的时间间隔进行过滤
 * 
 * 子类通过实现{@link #onSingleClick}响应click事件
 */
public abstract class OnSingleClickListener implements View.OnClickListener {
    /**
     * 最短click事件的时间间隔
     */
    private static final long MIN_CLICK_INTERVAL = 600;
    /**
     * 上次click的时间
     */
    private long mLastClickTime;

    /**
     * click响应函数
     * 
     * @param v The view that was clicked.
     */
    public abstract void onSingleClick(View v);

    @Override
    public final void onClick(View v) {
        long currentClickTime = SystemClock.uptimeMillis();
        long elapsedTime = currentClickTime - mLastClickTime;
        //有可能2次连击，也有可能3连击，保证mLastClickTime记录的总是上次click的时间
        mLastClickTime = currentClickTime;

        if (elapsedTime <= MIN_CLICK_INTERVAL)
            return;

        onSingleClick(v);
    }

}
