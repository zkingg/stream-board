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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


import org.apache.commons.codec.binary.Base64;

public class CamAxisM1014 extends Camera {

	public CamAxisM1014(int id, String ip, int port, String name, String type, String room) {
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
	        
	        if (saveImg(input)) {
	        	return true;
	        } else {
	        	return false;
	        }
	        

	        //BufferedReader in = new BufferedReader(new InputStreamReader(img_axis.getInputStream()));
	        
	        
		} catch(Exception ex) {
			System.out.println(ex.getMessage());
			return false;
		}
	}
}
