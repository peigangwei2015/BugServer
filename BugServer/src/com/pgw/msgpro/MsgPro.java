package com.pgw.msgpro;

import java.util.Map;

import org.java_websocket.WebSocket;
import com.alibaba.fastjson.JSONObject;
import com.pgw.domain.MsgHeader;
import com.pgw.domain.MsgType;
import com.pgw.utils.DBUtils;
import com.pgw.utils.JsonUtils;
import com.pgw.utils.MsgUtils;

public class MsgPro {
	private Map<String, WebSocket> conns;
	private String smsg;
	public MsgPro(Map<String, WebSocket> conns) {
		this.conns = conns;
	}

	public void exec(WebSocket sendSocket, String msg) {
		

	}


}
