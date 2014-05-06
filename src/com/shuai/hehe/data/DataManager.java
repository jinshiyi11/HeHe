package com.shuai.hehe.data;

import java.util.ArrayList;
import java.util.HashSet;
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
    
    //private LinkedBlockingDeque<Runnable> mDbTasks;
    
    /**
     * 用来执行数据库操作的线程，所有数据库操作都在一个单独的行程中串行执行
     */
    private Executor mDbThread=Executors.newSingleThreadExecutor();
    
    /**
     * 收藏的新鲜事的id集合
     */
    private Set<Long> mStarFeedIds;
    
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
                            "id INT NOT NULL AUTO_INCREMENT PRIMARY KEY," +//该id的目的是记录收藏过多少个新鲜事
                            "feed_id INT NOT NULL" + //这个才是新鲜事的id
                            "type INT," +
                            "title VARCHAR(255) NOT NULL UNIQUE," +
                            "content TEXT," +
                            "[from] INT," +
                            //"state INT DEFAULT -1," +
                            //"insert_time TIMESTAMP DEFAULT  CURRENT_TIMESTAMP()," +
                            "show_time TIMESTAMP DEFAULT  0," +
                            "star_time TIMESTAMP DEFAULT  CURRENT_TIMESTAMP()" +
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
        
        loadStarFeeds();
    }
    
    public ParallelAsyncTask executeDbTask(ParallelAsyncTask task,Object... params ){
        return task.executeOnExecutor(mDbThread,params);
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
    
    private void loadStarFeeds(){
        ParallelAsyncTask<Void, Void, Set<Long>> task=new ParallelAsyncTask<Void, Void, Set<Long>>(){

            @Override
            protected Set<Long> doInBackground(Void... params) {
                Set<Long> feedIds=new HashSet<Long>();
                SQLiteDatabase db = mDbHelper.getReadableDatabase();
                Cursor cursor = db.rawQuery("select feed_id from star_feed", null);
                while(cursor.moveToNext()){
                    feedIds.add(cursor.getLong(0));
                }
                return feedIds;
            }

            @Override
            protected void onPostExecute(Set<Long> result) {
                super.onPostExecute(result);
                mStarFeedIds=result;
            }
        };
        
        executeDbTask(task);
    }
    
    /**
     * 该feedId对应的新鲜事是否已收藏
     * @param feedId
     * @return
     */
    public boolean isStarFeed(final long feedId){
        if(mStarFeedIds==null){
            //TODO:mStarredFeedIds还没从db加载完成？
            return false;
        }
        
        return mStarFeedIds.contains(feedId);
    }
    
    /**
     * 添加收藏
     * @param feed
     */
    public void addStarFeed(final Feed feed){
        if(mStarFeedIds==null){
            //TODO:mStarredFeedIds还没从db加载完成？
            return;
        }
        
        mStarFeedIds.add((long) feed.getId());
        
        ParallelAsyncTask<Void, Void, Void> task=new ParallelAsyncTask<Void, Void, Void>(){

            @Override
            protected Void doInBackground(Void... params) {
                SQLiteDatabase database = mDbHelper.getWritableDatabase();
                ContentValues values=new ContentValues();
                values.put("feed_id", feed.getId());
                values.put("type",feed.getType());
                values.put("title",feed.getTitle());
                values.put("content",feed.getContent());
                values.put("from",feed.getFrom());
                values.put("show_time",feed.getShowTime());
                values.put("star_time",System.currentTimeMillis());
                database.insert("star_feed", null, values);
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                super.onPostExecute(result);
                //添加完成，发送通知
//                Intent intent=new Intent(Constants.ACTION_ADD_STAR_FEED);
//                intent.putExtra(Constants.PUT_EXTRA_FEED, feed);
//                mLocalBroadcastManager.sendBroadcast(intent);
                starFeedAdded(feed);
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
        
        mStarFeedIds.remove(feedId);
        
        ParallelAsyncTask<Long, Void, Void> task=new ParallelAsyncTask<Long, Void, Void>(){

            @Override
            protected Void doInBackground(Long... params) {
                SQLiteDatabase database = mDbHelper.getWritableDatabase();
                database.delete("star_feed", "feed_id=?", new String[]{Long.toString(feedId)});
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                super.onPostExecute(result);
                //删除完成，发送通知
//                Intent intent=new Intent(Constants.ACTION_DELETE_STAR_FEED);
//                intent.putExtra(Constants.PUT_EXTRA_FEED_ID, feedId);
//                mLocalBroadcastManager.sendBroadcast(intent);
                starFeedRemoved(feedId);
            }
            
        };
        executeDbTask(task);
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
                FeedContentParser.parseAlbumFeedContent((AlbumFeed) feed, feed.getContent());
                break;
            case FeedType.TYPE_VIDEO:
                feed=new VideoFeed();
                FeedContentParser.parseVideoFeedContent((VideoFeed) feed, feed.getContent());
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

                feeds.add(feed);
            }
            }catch(JSONException e){
                Log.e(TAG, e.getMessage());
            }
        }
        return feeds;
    }

}
