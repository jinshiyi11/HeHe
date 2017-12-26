package com.shuai.hehe.protocol;

import android.content.Context;
import android.util.Log;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.shuai.hehe.data.Constants;
import com.shuai.hehe.data.UserInfo;
import com.shuai.hehe.logic.UserManager;
import java.io.UnsupportedEncodingException;
import java.util.LinkedList;
import java.util.List;
import cz.msebera.android.httpclient.message.BasicNameValuePair;

/**
 * 添加收藏
 */
public class AddFavTask extends BaseAutoReloginTask<Void> {
    private static final String TAG = AddFavTask.class.getSimpleName();

    public AddFavTask(Context context, int feedId,int feedType, Listener<Void> listener, Response.ErrorListener errorListener) {
        super(Method.POST, UrlHelper.getUrl(context, "api/addFav"),
                getBody(context, feedId,feedType), listener, errorListener);
    }

    private static List<BasicNameValuePair> getBody(Context context, int feedId,int feedType) {
        List<BasicNameValuePair> params = new LinkedList<BasicNameValuePair>();
        params.add(new BasicNameValuePair("feedId", String.valueOf(feedId)));
        params.add(new BasicNameValuePair("feedType", String.valueOf(feedType)));
        UserInfo accountInfo = UserManager.getInstance().getUserInfo();
        params.add(new BasicNameValuePair("uid", String.valueOf(accountInfo.getUid())));
        params.add(new BasicNameValuePair("access_token", accountInfo.getToken()));

        UrlHelper.addCommonParameters(context, params);
        return params;
    }

    @Override
    protected Response<Void> parseNetworkResponse(NetworkResponse response) {
        try {
            String jsonString = new String(response.data, "UTF-8");
            if (Constants.DEBUG) {
                Log.d(TAG, jsonString);
            }

            JsonParser parser = new JsonParser();
            JsonObject root = parser.parse(jsonString).getAsJsonObject();
            ErrorInfo result = ProtocolUtils.getProtocolInfo(root);
            if (result.getErrorCode() != 0) {
                return Response.error(result);
            }

            return Response.success(null, HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (Exception e) {
            return Response.error(new ParseError(e));
        }
    }
}
