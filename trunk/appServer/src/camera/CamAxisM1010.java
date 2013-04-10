package camera;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;


import org.apache.commons.codec.binary.Base64;

public class CamAxisM1010 extends Camera {

	public CamAxisM1010(int id, String ip, int port, String name, String type, String room) {
		super(id, ip, port, name, type, room);
	}
	
	public boolean getImage() {
		try {
			URL url_axis = new URL("http://" + this.ip + ':' + this.port + "/axis-cgi/jpg/image.cgi");
	
	        URLConnection img_axis = url_axis.openConnection();

	        String userpass = "root" + ":" + "dadada";
	        String basicAuth = "Basic " + new String(new Base64().encode(userpass.getBytes()));
	        img_axis.setRequestProperty ("Authorization", basicAuth);
	        
	        InputStream input = img_axis.getInputStream();
	        if(inputStreamToFile(input, "C:\\Users\\Damien\\Pictures\\fichier_image.jpg")){
	        	System.out.println("Img save");
	        }
	        //BufferedReader in = new BufferedReader(new InputStreamReader(img_axis.getInputStream()));
	        
	        
		} catch(Exception ex) {
			System.out.println(ex.getMessage());
			return false;
		}
		
		return true;
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

}
