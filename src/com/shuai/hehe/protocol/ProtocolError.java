package com.shuai.hehe.protocol;

import com.android.volley.NetworkError;
import com.android.volley.ParseError;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.shuai.hehe.R;

import android.content.Context;

public class ProtocolError {

	public static String getErrorMessage(Context context, Exception e) {
		if (e instanceof NetworkError || e instanceof TimeoutError) {
			return context.getString(R.string.error_network_connection);
		} else if (e instanceof ServerError) {
			return context.getString(R.string.error_http_service);
		} else if (e instanceof ParseError) {
			return context.getString(R.string.error_data_format);
		} else {
			return context.getString(R.string.error_network_connection);
		}

	}

}
