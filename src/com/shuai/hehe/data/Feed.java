package com.shuai.hehe.data;

import com.google.gson.annotations.SerializedName;

/**
 *
 */
public class Feed {
	@SerializedName("id")
	public int mId;
	
	@SerializedName("type")
	public int mType;
	
	@SerializedName("title")
	public String mTitle;
	
	@SerializedName("content")
	public String mContent;
	
	@SerializedName("from")
	public int mFrom;
	//public int mState;
}
