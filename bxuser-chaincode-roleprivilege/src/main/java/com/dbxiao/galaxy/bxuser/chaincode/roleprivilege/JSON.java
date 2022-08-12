package com.dbxiao.galaxy.bxuser.chaincode.roleprivilege;

import com.google.gson.Gson;

public class JSON {

	private static final Gson gson = new Gson();

	public static String toJSONString(Object object) {
		if (object == null) {
			return "";
		}
		return gson.toJson(object);
	}

	public static <T> T parseObject(String text, Class<T> t){
		return gson.fromJson(text, t);
	}

}
