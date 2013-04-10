package camera;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import database.Database;

public abstract class Camera {
	protected final String ROOT_FOLDER = "C:\\CameraProject\\";

	protected int id;
	protected String ip;
	protected int port;
	protected String name;
	protected String type;
	protected String room;

	public Camera(int id, String ip, int port, String name, String type, String room) {
		this.id = id;
		this.ip = ip;
		this.port = port;
		this.name = name;
		this.type = type;
		this.room = room;
	}

	public abstract boolean getImage();

	public boolean saveImg(InputStream input) {
		try {        	
			DateFormat format = new SimpleDateFormat("dd-MM-yyyy");
			SimpleDateFormat sdf = new java.text.SimpleDateFormat("HH-mm");
			String filename = "image_" + sdf.format(new Date());

			new File(ROOT_FOLDER + this.room + "\\" + format.format(new Date())).mkdirs();
			if(inputStreamToFile(input, ROOT_FOLDER + this.room + "\\" + format.format(new Date()) +  "\\" + filename + ".jpg")){
				copy(ROOT_FOLDER + this.room + "\\" + format.format(new Date()) +  "\\" + filename + ".jpg", ROOT_FOLDER + this.room + "\\" + format.format(new Date()) +  "\\last_image.jpg");
				System.out.println("Img save");
			}
			return true;
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
		}
		return false;
	}

	private Boolean inputStreamToFile(InputStream inputStream, String file_dest) {
		try {		 
			File file_img = new File(file_dest);

			// write the inputStream to a FileOutputStream
			OutputStream out = new FileOutputStream(file_img);

			int read = 0;
			byte[] bytes = new byte[1024];

			while ((read = inputStream.read(bytes)) != -1) {
				out.write(bytes, 0, read);
			}

			inputStream.close();
			out.flush();
			out.close();
		} catch (IOException e) {
			System.out.println(e.getMessage());
			return false;
		}
		return true;
	}

	public static ArrayList<Camera> getAllCamera() throws Exception{
		ArrayList<Camera> list = new ArrayList<Camera>();
		try {
			Connection myConnection = Database.getConnection();
			Statement stat = myConnection.createStatement();
			ResultSet rslt =  stat.executeQuery("SELECT c.id, ct.name, r.name, c.name, c.ip, c.port FROM cameras c JOIN camera_types ct ON (c.camera_type_id = ct.id) JOIN rooms r ON (r.id = c.room_id) WHERE c.active=1");
			while(rslt.next()) {
				if (rslt.getString("name").equals("AxisM1014")) {
					list.add(new CamAxisM1014(rslt.getInt("id"), rslt.getString("ip"), rslt.getInt("port"), rslt.getString("c.name"), rslt.getString("ct.name"), rslt.getString("r.name")));
				}
				else if (rslt.getString("name").equals("AxisM1010"))
				{
					list.add(new CamAxisM1010(rslt.getInt("id"), rslt.getString("ip"), rslt.getInt("port"), rslt.getString("c.name"), rslt.getString("ct.name"), rslt.getString("r.name")));
				}
			}
		}
		catch(SQLException ex) {
			System.out.println("Error verification : " + ex.getMessage());
		}
		return list;
	}

	private void copy(String src,String dest){
		FileInputStream in = null;
		FileOutputStream out =null;
		try {
			in = new FileInputStream(new File(src));
			out = new FileOutputStream(dest);
			byte buf[] = new byte[1024];
			int n;
			while((n=in.read(buf))!=-1){
				out.write(buf,0,n);
			}
			in.close();
			out.close();
		}
		catch (FileNotFoundException e) {e.printStackTrace();}
		catch (IOException e) {e.printStackTrace();}
		finally{
			try {
				if(in != null)in.close();
				if(out != null)out.close();
			} catch (IOException e) {}
		}
	}
}
