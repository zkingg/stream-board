package database;

import java.io.File;
import java.sql.*;

public class Database {
	/*private static String ip = "213.111.29.142";
	private static Integer port = 3306;
	private static String login = "root";
	private static String password = "dadada";*/
	
	private static Connection myConnection = null;
	
	private Database(){}
	public static Connection getConnection() throws Exception {		
		if( myConnection == null || myConnection.isClosed())
		{
			//Database.load();
			myConnection =  Database.connect();
		}
		
		return myConnection;
	}
	
	private static Connection connect() throws Exception {
		try{
			InfoConf conf = new InfoConf();
			System.out.println("jdbc:mysql://" + conf.host + ":" + conf.port + "/" + conf.db);
			Connection connection = DriverManager.getConnection("jdbc:mysql://" + conf.host + ":" + conf.port + "/" + conf.db, conf.login, conf.mdp);
			System.out.println("Connection : OK");
			return connection;
		}
		catch (SQLException ex) {
			ex.printStackTrace();
			throw new Exception("Error can't connect to database ");
		}
	}
}
