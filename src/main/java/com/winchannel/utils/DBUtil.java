package com.winchannel.utils;

import java.sql.*;

public class DBUtil {
	
	/**
	 * 获取连接
	 */
	public static Connection getConnection(String driver,String dbUrl,String userName,String passWord) {
		Connection conn = null;
		try {
			Class.forName(driver);
			conn = DriverManager.getConnection(dbUrl, userName, passWord);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return conn;
	}
	
	

	/**
	 * 关闭连接
	 */
	public static void closeDbResources(Connection conn,Statement stmt,ResultSet rs){
		if(rs!=null){
			try{
				rs.close();
			}catch(SQLException e){
				e.printStackTrace();
			}
			rs=null;
		}
		if(stmt!=null){
			try{
				stmt.close();
			}catch(SQLException e){
				e.printStackTrace();
			}
			stmt=null;
		}
		if(conn!=null){
			try{
				conn.close();
			}catch(SQLException e){
				e.printStackTrace();
			}
			conn=null;
		}
	}
	
	
	
	
	
}
