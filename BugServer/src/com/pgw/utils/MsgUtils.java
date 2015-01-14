package com.pgw.utils;



import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.pgw.domain.MsgHeader;

public class MsgUtils {
	public static String formatMsg(int type) {
		JSONObject msgJson = (JSONObject) JSON.toJSON(new MsgHeader("", "",
				type));
		return msgJson.toJSONString();
	}

	public static String formatMsg(MsgHeader header) {
		JSONObject msgJson = (JSONObject) JSON.toJSON(header);
		return msgJson.toJSONString();
	}

	public static String formatMsg(MsgHeader header, Object object) {
		JSONObject msgJson = (JSONObject) JSON.toJSON(header);
		msgJson.put("body", object);
		return msgJson.toJSONString();
	}

	public static String formatMsg(String getter, int type) {
		JSONObject msgJson = (JSONObject) JSON.toJSON(new MsgHeader(getter, "",
				type));
		return msgJson.toJSONString();
	}
	
}
