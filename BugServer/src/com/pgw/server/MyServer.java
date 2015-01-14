package com.pgw.server;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import com.alibaba.fastjson.JSONObject;
import com.pgw.dao.ConnectDao;
import com.pgw.domain.MsgHeader;
import com.pgw.domain.MsgType;
import com.pgw.domain.User;
import com.pgw.test.AndroidDoc;
import com.pgw.utils.MsgUtils;

public class MyServer extends WebSocketServer {
	private static final int PORT = 8888;
//	测试连接的间隔时间
	private static final int TEST_CONNECT_TIME=60;
	public static Map<String, WebSocket> conns = new HashMap<String, WebSocket>();
	private String smsg;
	private ConnectDao connDao = new ConnectDao();

	public MyServer(InetSocketAddress address) {
		super(address);

	}

	public MyServer(int port) {
		super(new InetSocketAddress(port));
	}

	@Override
	public void onOpen(WebSocket conn, ClientHandshake arg1) {
		System.out.println(conn.getRemoteSocketAddress().getHostName()
				+ " Enter!");
	}

	@Override
	public void onClose(WebSocket conn, int arg1, String msg, boolean arg3) {
		InetSocketAddress address = conn.getRemoteSocketAddress();
		System.out.println(address.getAddress().getHostAddress());

		Map<String, Object> user = connDao.queryByIp(address.getAddress().getHostAddress()+":"+address.getPort());
		if (user != null) {
			conns.remove((String)user.get("username"));
			connDao.updateConn((String)user.get("username"), null, 2);
			System.out.println(user.get("username") + " 退出了!");
		}
	}

	@Override
	public void onError(WebSocket conn, Exception ex) {
		ex.printStackTrace();
	}

	@Override
	public void onMessage(WebSocket conn, String msg) {
		System.out.println(msg);
		JSONObject msgObj = JSONObject.parseObject(msg);
		int type = msgObj.getIntValue("type");
		if (type==MsgType.LOGIN) {
			// 登陆信息：
			// 1.获取登陆的用户名
			String userName = msgObj.getString("body");
			// 2.以用户名为Key将连接保存在集合中
			conns.put(userName, conn);
			if (!userName.equals("admin")) {
				// 3.获取用户的地址
				InetSocketAddress address = conn.getRemoteSocketAddress();
				// 4.将用户信息插入的数据库中
				connDao.insert(userName, address.getAddress().getHostAddress()+":"+address.getPort());
			}
			// 5.回复用户登陆成功
			smsg = MsgUtils.formatMsg(MsgType.LOGIN_SUCCESS);
			conn.send(smsg);
		} else if (type==MsgType.LIST_ONLINE) {
			// 获取在线用户列表
			List<Map<String, Object>> onlineList = connDao.listOnline();
			// 将列表发送给请求者
			conn.send(MsgUtils.formatMsg(new MsgHeader("", "server",
					MsgType.LIST_ONLINE), onlineList));
		} else {
			// 转发信息：
			// 1.从集合中得到接受者的连接
			WebSocket getterSocket = conns.get(msgObj.getString("getter"));
			// 如果接受者连接存在就发送出去
			if (getterSocket != null) {
				getterSocket.send(msg);
			} else {
				// 如果接受者链接不存在，就告诉发送者，接受者不存在
				conn.send(MsgUtils.formatMsg(MsgType.USER_NOT_EXIST));
			}
		}

	}

	@Override
	public void onMessage(WebSocket conn, ByteBuffer message) {
		// 将数据转发给控制手机
		WebSocket webSocket = conns.get("admin");
		if (webSocket != null) {
			webSocket.send(message);
		}
		super.onMessage(conn, message);
	}

	public static void main(String[] args) {
		MyServer myServer = new MyServer(PORT);
		myServer.start();
		myServer.testConnect();
		System.out.println("Server started!");
	}

	public void testConnect() {
		new TestConnect().start();
	}

	/**
	 * 测试连接是否可用
	 * 
	 * @author Administrator
	 * 
	 */
	public class TestConnect extends Thread {
		private WebSocket ws;

		@Override
		public void run() {
			while (true) {
				try {
					Thread.sleep(TEST_CONNECT_TIME * 1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				for (Map.Entry<String, WebSocket> entry : conns.entrySet()) {
					ws = entry.getValue();
					ws.send(MsgUtils.formatMsg(MsgType.TEST_CONNECT));
				}
			}
		}
	}

}
