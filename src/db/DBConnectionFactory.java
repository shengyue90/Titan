package db;

import db.mysql.MySQLConnection;

public class DBConnectionFactory {
	private static final String DEFAULT_DB = "mysql";
	
	//overloading here
	public static DBConnection getDBConnection() {
		return getDBConnection(DEFAULT_DB);
	}
	
	//create db connection based on given db type
	public static DBConnection getDBConnection(String db) {
		switch (db) {
			case "mysql" : return MySQLConnection.getInstance();
				
			default: throw new IllegalArgumentException("Invalid db: " + db);
		}
	}
}
