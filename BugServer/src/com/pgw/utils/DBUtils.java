package com.pgw.utils;

import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class DBUtils {
	private static Connection conn = null;
	static {
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	public static Connection getConnection() throws SQLException {
		if(conn!=null) return conn;
		String url = "jdbc:mysql://localhost:3306/qt";
		String username = "root";
		String password = "11111111";
		return DriverManager.getConnection(url, username, password);
	}

}
