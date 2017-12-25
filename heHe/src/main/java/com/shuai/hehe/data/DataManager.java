package com.shuai.hehe.data;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.json.JSONException;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.shuai.hehe.base.ParallelAsyncTask;
import com.shuai.hehe.protocol.FeedContentParser;
import com.shuai.utils.StorageUtils;
import com.umeng.analytics.MobclickAgent;

public class DataManager {
    private static final String TAG="DataManager";
    private static DataManager mDataManager;
    private Context mContext;
    private LocalBroadcastManager mLocalBroadcastManager;
    
    private DatabaseHelper mDbHelper;
    
    /**
     * 数据库名
     */
    private static final String DB_NAME="data.db";

    /**
     * 数据库版本
     */
    private static final int DB_VERSION = 1;
    
    /**
     * 记录上次看到相册的第几张图片。如果再次打开相册，自动跳到上次浏览的图片
     */
    private Map<Long, Integer> mLastAblumPicPositions=new HashMap<Long, Integer>();
    
    public interface OnStarFeedChangedListener{
        void onStarFeedAdded(Feed feed);
        void onStarFeedRemoved(long feedId);
    }
    
    private Set<OnStarFeedChangedListener> mStarFeedChangedListeners=new HashSet<OnStarFeedChangedListener>();
    
    public static synchronized void init(Context context) {
        if(mDataManager==null){
            mDataManager=new DataManager(context);
        }
    }

    public static  DataManager getInstance(){
        if(mDataManager==null){
            throw new IllegalStateException("init() not called!");
        }
        return mDataManager;
    }

    private class DatabaseHelper extends SQLiteOpenHelper {

        public DatabaseHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            String[] sqls={
                    "CREATE TABLE IF NOT EXISTS star_feed(" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT," +//该id的目的是记录收藏过多少个新鲜事
                            "feed_id INTEGER NOT NULL UNIQUE," + //这个才是新鲜事的id
                            "type INTEGER," +
                            "title TEXT NOT NULL UNIQUE," +
                            "content TEXT," +
                            "[from] INTEGER," +
                            //"state INT DEFAULT -1," +
                            //"insert_time TIMESTAMP DEFAULT  CURRENT_TIMESTAMP()," +
                            "show_time TIMESTAMP DEFAULT  0," +
                            "star_time INTEGER" +//from 1970
                            ")",
                            
                            };
            
            for (String sql : sqls) {
                db.execSQL(sql);
            }

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // TODO Auto-generated method stub

        }

    }

    private DataManager(Context context) {
        mContext=context;
        mDbHelper= new DatabaseHelper(mContext);
        mLocalBroadcastManager=LocalBroadcastManager.getInstance(mContext);
    }
    
    /**
     * 记录上次看到相册的第几张图片
     * @param feedId
     * @param picPosition 第几张图片，从0开始
     */
    public void setLastAlbumPicPosition(long feedId,int picPosition){
        if(picPosition==0){
            mLastAblumPicPositions.remove(feedId);
            return;
        }
        mLastAblumPicPositions.put(feedId, picPosition);
    }
    
    /**
     * 上次看到相册的第几张图片
     * @param feedId
     * @return
     */
    public int getLastAlbumPicPosition(long feedId) {
        Integer picPosition=mLastAblumPicPositions.get(feedId);
        if(picPosition==null)
            return 0;
        else
            return picPosition;
    }
    
    public void addStarFeedChangedListener(OnStarFeedChangedListener listener){
        mStarFeedChangedListeners.add(listener);
    }
    
    public void removeStarFeedChangedListener(OnStarFeedChangedListener listener){
        mStarFeedChangedListeners.remove(listener);
    }
    
    void starFeedAdded(Feed feed){
        for(OnStarFeedChangedListener listener:mStarFeedChangedListeners){
            listener.onStarFeedAdded(feed);
        }
    }
    
    void starFeedRemoved(long feedId){
        for(OnStarFeedChangedListener listener:mStarFeedChangedListeners){
            listener.onStarFeedRemoved(feedId);
        }
    }
    
    /**
     * 该feedId对应的新鲜事是否已收藏
     * @param feedId
     * @return
     */
    public boolean isStarFeed(final long feedId){
        
        return mStarFeedIds.contains(feedId);
    }
    
    /**
     * 添加收藏
     * @param feed
     */
    public void addStarFeed(final Feed feed){
        
        //统计
        MobclickAgent.onEvent(mContext, Stat.EVENT_STAR);
        
        mStarFeedIds.add((long) feed.getId());
        //发送通知
        starFeedAdded(feed);
        
        ParallelAsyncTask<Object, Object, Object> task=new ParallelAsyncTask<Object, Object, Object>(){

            @Override
            protected Object doInBackground(Object... params) {
                SQLiteDatabase database = mDbHelper.getWritableDatabase();
                ContentValues values=new ContentValues();
                values.put("feed_id", feed.getId());
                values.put("type",feed.getType());
                values.put("title",feed.getTitle());
                values.put("content",feed.getContent());
                values.put("[from]",feed.getFrom());
                values.put("show_time",feed.getShowTime());
                values.put("star_time",System.currentTimeMillis());
                database.insert("star_feed", null, values);
                return null;
            }

            @Override
            protected void onPostExecute(Object result) {
                super.onPostExecute(result);
                //添加完成，发送通知
//                Intent intent=new Intent(Constants.ACTION_ADD_STAR_FEED);
//                intent.putExtra(Constants.PUT_EXTRA_FEED, feed);
//                mLocalBroadcastManager.sendBroadcast(intent);
                //starFeedAdded(feed);
            }
        };
        executeDbTask(task);
    }
    
    /**
     * 删除收藏的新鲜事
     * @param feedId
     */
    public void removeStarFeed(final long feedId){
        if(mStarFeedIds==null){
            //TODO:mStarredFeedIds还没从db加载完成？
            return;
        }
        
        //统计
        MobclickAgent.onEvent(mContext, Stat.EVENT_UNSTAR);
        
        mStarFeedIds.remove(feedId);
        //发送通知
        starFeedRemoved(feedId);
        
        ParallelAsyncTask<Object, Object, Object> task=new ParallelAsyncTask<Object, Object, Object>(){

            @Override
            protected Object doInBackground(Object... params) {
                SQLiteDatabase database = mDbHelper.getWritableDatabase();
                database.delete("star_feed", "feed_id=?", new String[]{Long.toString(feedId)});
                return null;
            }

            @Override
            protected void onPostExecute(Object result) {
                super.onPostExecute(result);
                //删除完成，发送通知
                //starFeedRemoved(feedId);
            }
            
        };
        executeDbTask(task,feedId);
    }

    /**
     * 取收藏的新鲜事
     * @param mStarTime
     * @param mCount
     * @return
     */
    public ArrayList<Feed> getStarFeeds(long mStarTime, int mCount) {
        ArrayList<Feed> feeds=new ArrayList<Feed>();
        
        SQLiteDatabase database = mDbHelper.getReadableDatabase();
        String sql="SELECT feed_id,type,title,content,[from],show_time,star_time FROM star_feed ";
        if(mCount>0){
            sql=sql+" WHERE star_time>? ";
        }else{
            sql=sql+" WHERE star_time<? ";
        }
        
        sql=sql+" ORDER BY star_time DESC LIMIT "+Math.abs(mCount);
        Cursor cursor = database.rawQuery(sql, new String[]{Long.toString(mStarTime)});
        while (cursor.moveToNext()) {
            try{
            int feedType=cursor.getInt(cursor.getColumnIndex("type"));
            Feed feed=null;
            
            switch (feedType) {
            case FeedType.TYPE_ALBUM:
                feed=new AlbumFeed();
                break;
            case FeedType.TYPE_VIDEO:
                feed=new VideoFeed();
                break;
            case FeedType.TYPE_BLOG:
                feed=new BlogFeed();
                break;
            default:
                break;
            }
            
            if (feed != null) {
                feed.setId(cursor.getLong(cursor.getColumnIndex("feed_id")));
                feed.setType(cursor.getInt(cursor.getColumnIndex("type")));
                feed.setTitle(cursor.getString(cursor.getColumnIndex("title")));
                feed.setContent(cursor.getString(cursor.getColumnIndex("content")));
                feed.setFrom(cursor.getInt(cursor.getColumnIndex("from")));
                feed.setShowTime(cursor.getLong(cursor.getColumnIndex("show_time")));
                feed.setStarTime(cursor.getLong(cursor.getColumnIndex("star_time")));
                
                //在设置content之后再解析content内容
                switch (feedType) {
                case FeedType.TYPE_ALBUM:
                    FeedContentParser.parseAlbumFeedContent((AlbumFeed) feed, feed.getContent());
                    break;
                case FeedType.TYPE_VIDEO:
                    FeedContentParser.parseVideoFeedContent((VideoFeed) feed, feed.getContent());
                    break;
                case FeedType.TYPE_BLOG:
                    FeedContentParser.parseBlogFeedContent((BlogFeed) feed, feed.getContent());
                    break;
                default:
                    break;
                }

                feeds.add(feed);
            }
            }catch(JSONException e){
                Log.e(TAG, e.getMessage());
            }
        }
        return feeds;
    }

}
