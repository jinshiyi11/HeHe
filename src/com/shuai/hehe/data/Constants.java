package com.shuai.hehe.data;

public class Constants {
	public static boolean DEBUG = true;
	public static final String SERVER_ADDRESS;

	static {
		if (DEBUG) {
			SERVER_ADDRESS = "http://10.0.2.2:8080/hehe_server";
		} else {
			SERVER_ADDRESS = "";
		}
	}
	
	

}
