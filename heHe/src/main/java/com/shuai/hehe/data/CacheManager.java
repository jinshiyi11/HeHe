package com.shuai.hehe.data;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

import org.apache.commons.codec.digest.DigestUtils;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.jakewharton.disklrucache.DiskLruCache;
import com.jakewharton.disklrucache.DiskLruCache.Editor;
import com.jakewharton.disklrucache.DiskLruCache.Snapshot;
import com.shuai.utils.StorageUtils;

public class CacheManager {
	private static final String TAG=CacheManager.class.getSimpleName();
	
	private static final String HTTP_RESPONSE_CACHE_DIR = "http_cache";
	private static DiskLruCache mDiskLruCache;
	private static CacheManager mSelf;



	public static void init(Context appContext) {
		try {
			File cacheDir = StorageUtils.getCacheDirectory(appContext);

			File individualCacheDir = new File(cacheDir,
					HTTP_RESPONSE_CACHE_DIR);
			if (!individualCacheDir.exists()) {
				if (!individualCacheDir.mkdir()) {
					individualCacheDir = cacheDir;
				}
			}

			mDiskLruCache = DiskLruCache.open(individualCacheDir, 1, 1, 20 * 1024 * 1024);
		} catch (IOException e) {
			Log.e(TAG, e.toString());
		}
	}
	
	public static CacheManager getInstance(){
		if (mSelf == null) {
			synchronized (CacheManager.class) {
				if (mSelf == null) {
					mSelf = new CacheManager();
				}
			}
		}
		return mSelf;
	}
	
	protected CacheManager(){
		
	}
	
	private String getHashKey(String url){
		return DigestUtils.md5Hex(url).toLowerCase(Locale.getDefault());
	}
	
	public String get(String url){
		if(TextUtils.isEmpty(url)){
			return null;
		}
		
		String key=getHashKey(url);
		String result=null;
		try {
			Snapshot snapshot = mDiskLruCache.get(key);
			result = snapshot.getString(0);
			snapshot.close();
		} catch (IOException e) {
			Log.e(TAG, e.toString());
		}
		
		return result;
	}
	
	public void set(String url,String data){
		if(TextUtils.isEmpty(url)){
			return;
		}
		
		String key=getHashKey(url);
		try {
			Editor edit = mDiskLruCache.edit(key);
			edit.set(0, data);
			edit.commit();
		} catch (IOException e) {
			Log.e(TAG, e.toString());
		}
	}
	
	public void flush(){
		try {
			mDiskLruCache.flush();
		} catch (IOException e) {
			Log.e(TAG, e.toString());
		}
	}
}
