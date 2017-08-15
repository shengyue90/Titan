package db.mysql;

import java.sql.*;

public class MySQLTableCreation {
	public static void main(String[] args) {
		try {
			//https://dev.mysql.com/doc/connector-j/5.1/en/connector-j-usagenotes-connect-drivermanager.html
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			//= new com.mysql.jdbc.Driver()
			//forName --> used to initiallize a class
			Connection conn = null;
			try {
				System.out.println("Connect to " + MySQLDBUtil.URL);
			    conn = DriverManager.getConnection(MySQLDBUtil.URL);

			} catch (SQLException ex) {
			    // handle any errors
			    System.out.println("SQLException: " + ex.getMessage());
			    System.out.println("SQLState: " + ex.getSQLState());
			    System.out.println("VendorError: " + ex.getErrorCode());
			}
			if (conn == null) {
				return;
			}
			Statement stmt = conn.createStatement();
			String sql = "DROP TABLE IF EXISTS history";
			stmt.executeUpdate(sql);
			sql = "DROP TABLE IF EXISTS categories";
			stmt.executeUpdate(sql);
			sql = "DROP TABLE IF EXISTS items";
			stmt.executeUpdate(sql);
			sql = "DROP TABLE IF EXISTS users";
			stmt.executeUpdate(sql);
			
			
			sql = "CREATE TABLE items " + "(item_id VARCHAR(255) NOT NULL, " + 
					"name VARCHAR(255), " + "city VARCHAR(255), " + "state VARCHAR(255), "+
					"country VARCHAR(255), " + "zipcode VARCHAR(255), " + "rating VARCHAR(255), " +
					"address VARCHAR(255), " + "latitude DOUBLE, " + "longitude DOUBLE, " +
					"description VARCHAR(255), " + "snippet VARCHAR(255), " + "snippet_url VARCHAR(255)," +
					"image_url VARCHAR(255)," + "url VARCHAR(255), " + "PRIMARY KEY (item_id))";
			stmt.executeUpdate(sql);
			
			sql = "CREATE TABLE categories " + "(item_id VARCHAR(255) NOT NULL, " +
					"category VARCHAR(255)," + "PRIMARY KEY (item_id, category), " +
					"FOREIGN KEY (item_id) REFERENCES items(item_id))";
			stmt.executeUpdate(sql);
			
			sql = "CREATE TABLE users " + "(user_id VARCHAR(255) NOT NULL, " + 
					"password VARCHAR(255) NOT NULL, " + "first_name VARCHAR(255), " +
					"last_name VARCHAR(255), " + "PRIMARY KEY (user_id))";
			stmt.executeUpdate(sql);
			
			sql = "CREATE TABLE history " + "(history_id bigint(20) unsigned NOT NULL AUTO_INCREMENT, " +
					"user_id VARCHAR(255) NOT NULL, " + "item_id VARCHAR(255) NOT NULL, " +
					"last_favor_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP, " + 
					"PRIMARY KEY (history_id), " + "FOREIGN KEY (item_id) REFERENCES items(item_id), " +
					"FOREIGN KEY (user_id) REFERENCES users(user_id))";
			stmt.executeUpdate(sql);
			
			sql = "INSERT INTO users " + "VALUES('1111', '1111', 'YY', 'LIU')";
			stmt.executeUpdate(sql);
				
			System.out.println("import is done successfully");
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
