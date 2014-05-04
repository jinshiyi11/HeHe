package com.shuai.hehe.data;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import android.R.color;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.shuai.hehe.base.ParallelAsyncTask;

public class DataManager {
    private static DataManager mDataManager;
    private Context mContext;
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
                            "type INT,title VARCHAR(255) NOT NULL UNIQUE," +
                            "content TEXT," +
                            "[from] INT," +
                            "state INT DEFAULT -1," +
                            "insert_time TIMESTAMP DEFAULT  CURRENT_TIMESTAMP()," +
                            "show_time TIMESTAMP DEFAULT  0," +
                            "starred_time TIMESTAMP DEFAULT  CURRENT_TIMESTAMP()" +
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
        
        loadStarFeeds();
    }
    
    public ParallelAsyncTask executeDbTask(ParallelAsyncTask task,Object... params ){
        return task.executeOnExecutor(mDbThread,params);
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
    
    public boolean isStarFeed(long feedId){
        if(mStarFeedIds==null){
            //TODO:mStarredFeedIds还没从db加载完成？
            return false;
        }
        
        return mStarFeedIds.contains(feedId);
    }
    
    public void addStarFeed(Feed feed){
        if(mStarFeedIds==null){
            //TODO:mStarredFeedIds还没从db加载完成？
            return;
        }
        
        mStarFeedIds.add((long) feed.getId());
    }
    
    public void removeStarFeed(long feedId){
        if(mStarFeedIds==null){
            //TODO:mStarredFeedIds还没从db加载完成？
            return;
        }
        
        mStarFeedIds.remove(feedId);
    }

    /**
     * 取收藏的新鲜事
     * @param mStarTime
     * @param mCount
     * @return
     */
    public ArrayList<StarFeed> getStarFeeds(long mStarTime, int mCount) {
        ArrayList<StarFeed> feeds=new ArrayList<StarFeed>();
        return feeds;
    }

}
