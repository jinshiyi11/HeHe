package com.shuai.hehe.adapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.shuai.hehe.data.Feed;

public class FeedAdapter extends ArrayAdapter<Feed> {
    private Context mContext;
    private FeedList mFeeds;

    public static class FeedList extends ArrayList<Feed> {
        //用来快速检测对象是否已存在
        private HashMap<Integer, Feed> mKeys = new HashMap<Integer, Feed>();

        private boolean exist(Feed object) {
            if (mKeys.get(object.getId()) == null)
                return false;
            else
                return true;
        }

        @Override
        public boolean add(Feed object) {
            if (exist(object)) {
                return false;
            }

            mKeys.put(object.getId(), object);
            return super.add(object);
        }

        @Override
        public void add(int index, Feed object) {
            if (exist(object)) {
                return;
            }

            mKeys.put(object.getId(), object);
            super.add(index, object);
        }

        @Override
        public boolean addAll(Collection<? extends Feed> collection) {
            return addAll(size(), collection);

        }

        @Override
        public boolean addAll(int index, Collection<? extends Feed> collection) {
            Feed[] datas = collection.toArray(new Feed[0]);
            //倒序添加，保证结果顺序和collection一致
            for (int i = datas.length - 1; i >= 0; i--) {
                Feed info = datas[i];
                add(index, info);
            }

            return true;
        }

        @Override
        public void clear() {
            super.clear();
            mKeys.clear();
        }

        @Override
        public Feed remove(int index) {
            Feed info = super.remove(index);
            mKeys.remove(info.getId());
            return info;
        }

        @Override
        public boolean remove(Object object) {
            mKeys.remove(((Feed) object).getId());
            return super.remove(object);
        }

    }

    public FeedAdapter(Context context, FeedList objects) {
        super(context, 0, objects);
        mContext = context;
        mFeeds = objects;
    }

    @Override
    public int getItemViewType(int position) {
        Feed item = getItem(position);
        return item.getType();
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return super.getView(position, convertView, parent);
    }

}
