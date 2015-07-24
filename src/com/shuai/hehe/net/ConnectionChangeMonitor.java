package com.shuai.hehe.net;

import java.util.LinkedList;
import java.util.List;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class ConnectionChangeMonitor extends BroadcastReceiver {
	private Context mContext;
	private List<ConnectionChangeListener> mConnectionChangeListeners=new LinkedList<ConnectionChangeListener>();
	
	private boolean mConnectedSent=false;//CONNECTIVITY_ACTION有时会多次触发，使用该标记过滤多余的通知
	private boolean mDisconnectedSent=false;//CONNECTIVITY_ACTION有时会多次触发，使用该标记过滤多余的通知
	
	public interface ConnectionChangeListener{
		void onConnectionChanged(boolean connected);
	}
	
	public void addConnectionChangeListener(ConnectionChangeListener listener) {
		mConnectionChangeListeners.add(listener);
	}

	public void removeConnectionChangeListener(ConnectionChangeListener listener) {
		mConnectionChangeListeners.remove(listener);
	}

	private void notifyConnectionChange(boolean result) {		
		for (ConnectionChangeListener listener : mConnectionChangeListeners) {
			listener.onConnectionChanged(result);
		}
	}
	
	public ConnectionChangeMonitor(Context appContext) {
		if(!(appContext instanceof Application)){
			throw new IllegalArgumentException();
		}
		
		mContext=appContext;
	}

	public void startMonitor() {
		IntentFilter filter=new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
		mContext.registerReceiver(this, filter);
	}

	public void stopMonitor() {
		mContext.unregisterReceiver(this);
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		if(isInitialStickyBroadcast())
			return;
		
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
		if (activeNetInfo != null && activeNetInfo.isConnected()) {
			//CONNECTIVITY_ACTION有时会多次触发，过滤多余的通知
			if(!mConnectedSent){
				mConnectedSent=true;
				mDisconnectedSent=false;
				notifyConnectionChange(true);
			}
			
		}else{
			if(!mDisconnectedSent){
				mDisconnectedSent=true;
				mConnectedSent=false;
				notifyConnectionChange(false);
			}
			
		}

	}
}
