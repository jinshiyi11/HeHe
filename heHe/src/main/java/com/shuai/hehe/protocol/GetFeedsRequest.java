package com.shuai.hehe.protocol;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONException;

import android.content.Context;
import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonRequest;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.shuai.hehe.data.AlbumFeed;
import com.shuai.hehe.data.Constants;
import com.shuai.hehe.data.Feed;
import com.shuai.hehe.data.BlogFeed;
import com.shuai.hehe.data.FeedType;
import com.shuai.hehe.data.VideoFeed;
import com.shuai.hehe.ui.FeedFragment;

import cz.msebera.android.httpclient.message.BasicNameValuePair;

/**
 * 取新鲜事
 */
public class GetFeedsRequest extends JsonRequest<ArrayList<Feed>> {
    private final static String TAG = GetFeedsRequest.class.getSimpleName();

    public static Boolean sFeedCacheLock = new Boolean(true);
    public final static int FEED_CACHE_COUNT = 20;
    private final static String FEED_CACHE_FILENAME = "feed_cache.json";

    public GetFeedsRequest(Context context,int type,long id, int count, Listener<ArrayList<Feed>> listener, ErrorListener errorListener) {
        super(Method.GET, getUrl(context,type,id, count), null, listener, errorListener);
        if (Constants.DEBUG) {
            Log.d(TAG, getUrl(context,type,id, count));
        }
    }

    /**
     * 获取新鲜事的url
     *
     * @param id
     * @param count
     * @return
     */
    public static String getUrl(Context context, int type, long id, int count) {
        List<BasicNameValuePair> params = new LinkedList<>();
        params.add(new BasicNameValuePair("id", Long.toString(id)));
        params.add(new BasicNameValuePair("count", Integer.toString(count)));

        if (type == FeedFragment.TYPE_VIDEO) {
            return UrlHelper.getUrl(context,"api/getVideoList", params);
        } else {
            return UrlHelper.getUrl(context,"api/getAlbumList", params);
        }
    }

    /**
     * 加载cache
     * 
     * @param context
     * @return
     */
    public static ArrayList<Feed> loadCache(Context context) {
        synchronized (sFeedCacheLock) {
            ArrayList<Feed> feedList = null;

            try {
                File file = context.getFileStreamPath(FEED_CACHE_FILENAME);
                if (!file.exists())
                    return null;

                FileInputStream fs = context.openFileInput(FEED_CACHE_FILENAME);
                byte[] buffer = new byte[512];
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                int bytesRead = -1;
                while ((bytesRead = fs.read(buffer)) != -1) {
                    byteArrayOutputStream.write(buffer, 0, bytesRead);
                }

                String json = byteArrayOutputStream.toString();
                byteArrayOutputStream.close();
                fs.close();

                //feedList = parseFeeds(json);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return feedList;
        }

    }

    /**
     * 缓存最近n条新鲜事
     * 
     * @param feedList
     */
    public static void saveCache(Context context, final ArrayList<Feed> feedList) {
        try {
            final FileOutputStream fileOutput = context.openFileOutput(FEED_CACHE_FILENAME, Context.MODE_PRIVATE);
            final ArrayList<Feed> feeds = new ArrayList<Feed>();
            for (int i = 0; i < feedList.size() && i < FEED_CACHE_COUNT; i++) {
                feeds.add(feedList.get(i));
            }

            Thread thread = new Thread() {

                @Override
                public void run() {
                    synchronized (sFeedCacheLock) {
                        Gson gson = new Gson();
                        String json = gson.toJson(feeds);
                        try {
                            fileOutput.write(json.getBytes());
                            fileOutput.close();
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                }

            };
            thread.run();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        }

    }

    @Override
    protected Response<ArrayList<Feed>> parseNetworkResponse(NetworkResponse response) {
        try {
            String jsonString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            if (Constants.DEBUG) {
                Log.d(TAG, jsonString);
            }

            JsonParser parser=new JsonParser();
            JsonObject root = parser.parse(jsonString).getAsJsonObject();
            ErrorInfo error = ProtocolUtils.getProtocolInfo(root);
            if (error.getErrorCode() != 0) {
                return Response.error(error);
            }


            ArrayList<Feed> feedList = parseFeeds(root.getAsJsonArray(ProtocolUtils.DATA));
            return Response.success(feedList, HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JSONException je) {
            return Response.error(new ParseError(je));
        } catch (Exception e) {
            //TODO:这个是否有必要
            return Response.error(new ParseError(e));
        }
    }

    private static ArrayList<Feed> parseFeeds(JsonArray jsonArray) throws JSONException {
        ArrayList<Feed> feedList = new ArrayList<Feed>();
        for (int i = 0; i < jsonArray.size(); i++) {
            JsonObject jsonObject = (JsonObject) jsonArray.get(i);

            int feedType = jsonObject.get("type").getAsInt();
            switch (feedType) {
            case FeedType.TYPE_ALBUM: {
                Gson gson = new Gson();
                AlbumFeed feed = gson.fromJson(jsonObject, AlbumFeed.class);
                FeedContentParser.parseAlbumFeedContent(feed, feed.getContent());
                feedList.add(feed);
                break;
            }
            case FeedType.TYPE_VIDEO: {
                Gson gson = new Gson();
                VideoFeed feed = gson.fromJson(jsonObject, VideoFeed.class);
                FeedContentParser.parseVideoFeedContent(feed, feed.getContent());
                feedList.add(feed);
                break;
            }
            case FeedType.TYPE_BLOG: {
                Gson gson = new Gson();
                BlogFeed feed = gson.fromJson(jsonObject, BlogFeed.class);
                FeedContentParser.parseBlogFeedContent(feed, feed.getContent());
                feedList.add(feed);
                break;
            }
            default:
                break;
            }
        }

        return feedList;
    }

}
