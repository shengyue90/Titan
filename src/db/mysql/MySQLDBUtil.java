package db.mysql;

public class MySQLDBUtil {
	/**
	 * jdbc:mysql://[host1][:port1][,[host2][:port2]]...[/[database]] »
	 * [?propertyName1=propertyValue1[&propertyName2=propertyValue2]...]
	 */
	private static final String HOSTNAME = "localhost";
	private static final String PORT_NUM = "3306";
	private static final String DB_NAME = "laiproject";
	private static final String USERNAME = "root";
	private static final String PASSWORD = "root";
	public static final String URL = "jdbc:mysql://" + HOSTNAME + ":" + PORT_NUM 
										+ "/" + DB_NAME + "?user=" + USERNAME 
										+ "&password=" + PASSWORD
										+ "&autoreconnect=true";

	
}
