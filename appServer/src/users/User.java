package users;

import java.sql.*;
import database.Database;

public class User {
	public static int verifUser(String login) throws Exception{
		try {
			Connection myConnection = Database.getConnection();
			Statement stat = myConnection.createStatement();
			ResultSet rslt =  stat.executeQuery("SELECT users.id FROM users WHERE users.login='" + login + "'");
			while(rslt.next()){
				return rslt.getInt("id");
			}
			return -1;
		}
		catch(SQLException ex) {
			System.out.println("Error verification");
		}
		return -2;
	}
}
