package com.shuai.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.shuai.hehe.data.Constants;
import com.shuai.hehe.ui.AboutActivity;
import com.shuai.hehe.ui.LoginActivity;
import com.shuai.hehe.ui.MainActivity;
import com.shuai.hehe.ui.SettingsActivity;


/**
 *
 */

public class NavigateUtils {
    public static final int TAB_VIDEO = 0;
    public static final int TAB_ALBUM = 1;
    public static final int TAB_FAV = 2;
    public static final int TAB_USER = 3;

    public static void showTab(Context context, int tab) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(Constants.EXTRA_TAB, tab);

        context.startActivity(intent);
    }

    /**
     * 显示登录界面
     *
     * @param context
     */
    public static void showLoginActivity(Context context, Intent targetIntent) {
        showLoginActivity(context, targetIntent, -1);
    }

    public static void showLoginActivity(Context context, Intent targetIntent, int requestCode) {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.putExtra(Constants.EXTRA_LOGIN_TARGET_INTENT, targetIntent);
        if (context instanceof Activity) {
            ((Activity) context).startActivityForResult(intent, requestCode);
        } else {
            context.startActivity(intent);
        }
    }

//    public static void showSearchActivity(Context context) {
//        Intent intent = new Intent(context, SearchActivity.class);
//        context.startActivity(intent);
//    }

    public static void showSettingsActivity(Context context) {
        Intent intent = new Intent(context, SettingsActivity.class);
        context.startActivity(intent);
    }

    public static void showAboutActivity(Context context) {
        Intent intent = new Intent(context, AboutActivity.class);
        context.startActivity(intent);
    }

}
