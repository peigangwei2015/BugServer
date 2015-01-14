package com.pgw.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.pgw.domain.User;
import com.pgw.utils.DBUtils;

public class ConnectDao {
	private static Connection dbconn;
	static {
		try {
			dbconn = DBUtils.getConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private PreparedStatement pre;
	private ResultSet res;

	public void insert(String name, String ip) {
		try {
			Map<String, Object> user = queryByName(name);
			if (user != null) {
				updateConn(name, ip, 1);
				return;
			}
			String sql = "insert into connect (username,ip,isconn) value(?,?,?)";
			pre = dbconn.prepareStatement(sql);
			pre.setString(1, name);
			pre.setString(2, ip);
			pre.setInt(3, 1);
			int res = pre.executeUpdate();
			System.out.println("插入了：" + res + "行数据！");
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (pre != null) {
				try {
					pre.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 更新链接记录
	 * 
	 * @param name
	 * @param isconn
	 */
	public void updateConn(String name, String ip, int isconn) {
		try {
			String sql = "update connect set isconn=?,ip=? where username=?";
			pre = dbconn.prepareStatement(sql);
			pre.setInt(1, isconn);
			if (ip != null) {
				pre.setString(2, ip);
			} else {
				pre.setString(2, "");
			}
			pre.setString(3, name);
			int res = pre.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (pre != null) {
				try {
					pre.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 根据Id查询
	 * 
	 * @param id
	 *            要出查询的ID
	 * @return 查询到用户
	 */
	public Map<String, Object> queryById(int id) {
		User user = new User();
		user.setId(id);
		String sql = "select * from connect where id='"+id+"'";
		List<Map<String, Object>> list = query(sql);
		if (list.size() ==0) {
			return null;
		}
		return list.get(0);
	}

	/**
	 * 根据名字查询
	 * 
	 * @param name
	 *            要查询的名字
	 * @return 返回查询呢到的用户
	 */
	public Map<String, Object> queryByName(String name) {
		User user = new User();
		user.setName(name);
		String sql = "select * from connect where username='"+name+"'";
		List<Map<String, Object>> list = query(sql);
		if (list.size() ==0) {
			return null;
		}
		return list.get(0);

	}

	/**
	 * 根据Ip查询
	 * 
	 * @param ip
	 *            要查询的IP
	 * @return 返回查询到的用户
	 */
	public Map<String, Object> queryByIp(String ip) {
		User user = new User();
		user.setIp(ip);
		String sql = "select * from connect where ip='"+ip+"'";
		List<Map<String, Object>> list = query(sql);
		if (list.size() ==0) {
			return null;
		}
		return list.get(0);
	}

	/**
	 * 列出所有在线用户
	 * 
	 * @return 返回在线用户列表
	 */
	public List<Map<String, Object>> listOnline() {
		User user = new User();
		user.setIsconn(1);
		String sql = "select * from connect where isconn=1";
		return query(sql);
	}

	/**
	 * 列出所有用户
	 * 
	 * @return 返回所有用户列表
	 */
	public List<Map<String,Object>> listAll() {
		String sql = "select * from connect  ";
		return query(sql);
	}

	/**
	 * 基础查询
	 * 
	 * @param user
	 * @return
	 */
	private List<Map<String,Object>> query(String sql) {
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		try {
			pre = dbconn.prepareStatement(sql);
			res = pre.executeQuery();
			
			while (res.next()) {
				Map<String, Object> map=new HashMap<String, Object>(); 
				map.put("id", res.getInt("id"));
				map.put("ip", res.getString("ip"));
				map.put("username", res.getString("username"));
				map.put("isConn", res.getInt("isconn"));
				list.add(map);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (pre != null)
					pre.close();
				if (res != null)
					res.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return list;
	}

}
