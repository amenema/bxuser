package com.dbxiao.galaxy.bxuser.chaincode.utils;


public class JSON {


	public static String toJSONString(Object object) {
		if (object == null) {
			return "";
		}
		return com.alibaba.fastjson.JSON.toJSONString(object);
	}

	public static <T> T parseObject(String text, Class<T> t){
		return com.alibaba.fastjson.JSON.parseObject(text, t);
	}

}
