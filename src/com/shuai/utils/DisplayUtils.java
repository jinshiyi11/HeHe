package com.shuai.utils;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;

public class DisplayUtils {
	public static float getScreenWidth(Context context) {
		Resources res = context.getResources();
		DisplayMetrics metrics = res.getDisplayMetrics();
		return metrics.widthPixels;
	}

	public static float getScreenHeight(Context context) {
		Resources res = context.getResources();
		DisplayMetrics metrics = res.getDisplayMetrics();
		return metrics.heightPixels;
	}

	public static DisplayMetrics getDisplayMetrics(Context context) {
		Resources res = context.getResources();
		DisplayMetrics metrics = res.getDisplayMetrics();
		return metrics;
	}
	
	/**
	 * 单位px转换成单位sp(字体)
	 * @return
	 */
	public static int px2sp(Context context, int pxVaule) {
		return (int)(pxVaule / getDisplayMetrics(context).scaledDensity + 0.5f);
	}

	/**
	 * 将sp值转换为px值，保证文字大小不变
	 * 
	 */
	public static int sp2px(Context context, float spValue) {
		return (int) (spValue * getDisplayMetrics(context).scaledDensity + 0.5f);
	}

	/**
	 * 将px转换成dp值
	 * @return
	 */
	public static int px2dp(Context context, int pxVaule) {
		return (int)(pxVaule / getDisplayMetrics(context).density + 0.5f);
	}

	/**
	 * 将dp值转换为px值
	 * 
	 */
	public static int dp2px(Context context, float dpValue) {
		return (int) (dpValue * getDisplayMetrics(context).density + 0.5f);
	}
}
