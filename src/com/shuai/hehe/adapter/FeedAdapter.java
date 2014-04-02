package com.shuai.hehe.adapter;

import java.util.Collection;
import java.util.List;

import com.shuai.hehe.data.Feed;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

public class FeedAdapter extends ArrayAdapter<Feed> {
	private Context mContext;
	private List<Feed> mFeeds;

	public FeedAdapter(Context context, List<Feed> objects) {
		super(context, 0, objects);
		mContext=context;
		mFeeds=objects;
	}
	
	@Override
	public int getItemViewType(int position) {
		Feed item = getItem(position);
		return item.mType;
	}

	@Override
	public int getViewTypeCount() {
		return 2;
	}

	@Override
	public void add(Feed object) {
		super.add(object);
	}

	@Override
	public void addAll(Collection<? extends Feed> collection) {
		// TODO Auto-generated method stub
		//super.addAll(collection);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		return super.getView(position, convertView, parent);
	}

	
	
	

}
