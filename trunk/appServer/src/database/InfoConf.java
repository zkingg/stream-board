package database;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class InfoConf {
	public String host;
	public int port;
	public String db;
	public String login;
	public String mdp;
	public String file_name;

	public InfoConf() {
		file_name = "config.yml";
		// file_name = "script.config.yml";

		File file = new File(file_name);
		if (!file.exists()) {
			try {
				FileWriter file_w = new FileWriter(file_name);
				file_w.write("MySQL:\r\n");
				file_w.write(" Host: localhost\r\n");
				file_w.write(" Port: 3306\r\n");
				file_w.write(" Login: root\r\n");
				file_w.write(" Password: 'dadada'\r\n");
				file_w.write(" Database: streamboard");
				file_w.flush();
				file_w.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		BufferedReader file_r = null;
		try {
			file_r = new BufferedReader(new FileReader(file_name));
			String line;

			while ((line = file_r.readLine()) != null) {
				if (line.contains("Host:")) {
					host = line.split(":")[1].substring(1);
				} else if (line.contains("Port:")) {
					port = Integer.parseInt(line.split(":")[1].substring(1));
				} else if (line.contains("Database:")) {
					db = line.split(":")[1].substring(1);
				} else if (line.contains("Login:")) {
					login = line.split(":")[1].substring(1);
				} else if (line.contains("Password:")) {
					mdp = line.split(":")[1].substring(1);
					if (mdp.equals("\"\"") || mdp.equals("\'\'"))
						mdp = "";
				} 
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (file_r != null)
					file_r.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}