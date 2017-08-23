package com.shuai.base.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TabHost;

/**
 * 修复TabHost中webview中的输入框无法通过hardware keyboard输入的问题
 * see http://stackoverflow.com/questions/14113062/input-from-hardware-keyboard-loses-focus
 * and https://code.google.com/p/android/issues/detail?id=2516
 */

public class TabHostEx extends TabHost {

    public TabHostEx(Context context) {
        super(context);
    }

    public TabHostEx(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void onTouchModeChanged(boolean isInTouchMode) {
        //基类实现会抢焦点
        //super.onTouchModeChanged(isInTouchMode);
    }

}
