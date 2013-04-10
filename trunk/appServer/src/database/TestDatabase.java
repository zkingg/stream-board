package database;

public class TestDatabase {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			Database.getConnection();
			Database.getConnection();
			Database.getConnection();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
