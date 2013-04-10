package server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import camera.CamAxisM1014;

import users.User;

public class ServerThread extends Thread{
	private Socket client;
	
	ServerThread(Socket client) {
		this.client = client;
		start();
	}
	
	public void run() {
		String rep = null;
		String response = null;
		
        OutputStream output;
		try {
			output = client.getOutputStream();
		
	        InputStream input = client.getInputStream();
	        
	        rep = new BufferedReader(new InputStreamReader(input)).readLine();
	        
	        System.out.println("New client, address " + client.getInetAddress() + " on " + client.getPort() + ".");
	        System.out.println("Message the client " + rep);
			
	        String[] tab = rep.split(";");
	        switch(tab[0]){
	        case "login":
	        	try {
	        	int ident = User.verifUser(tab[1]);
					if(ident != -1) {
						response = String.valueOf(ident);
					} else {
						response = "Wrong identifiant";
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
			        output.write(response.getBytes());
				}
	        	break;
	        	
	        case "stream":
	        	try {
	        		FileInputStream in = new FileInputStream("C:\\Users\\Damien\\Pictures\\pic1.jpg");
	        		byte buf[] = new byte[1024];
	        		int n;
	        		while((n=in.read(buf))!=-1){
		        		output.write(buf,0,n);
	        		}
	        		System.out.println("stream img ok");
	        		in.close();
	        		output.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
	        	break;
	        	
	        case "getCurrentImage":
	        	try {
	        		DateFormat format = new SimpleDateFormat("dd-MM-yyyy");
	        		FileInputStream in = new FileInputStream("C:\\CameraProject\\" + tab[1] + "\\" + format.format(new Date()) + "\\last_image.jpg");
	        		byte buf[] = new byte[1024];
	        		int n;
	        		while((n=in.read(buf))!=-1){
		        		output.write(buf,0,n);
	        		}
	        		System.out.println("stream img actuelle de la salle " + tab[1] + "ok");
	        		in.close();
	        		output.close();
	        	} catch (Exception e){
	        		e.printStackTrace();
	        	}
	        	break;
	        	
	        case "getListRoom":
	        	try{
	        	    File folder = new File("C:\\CameraProject");
	        	    File[] listOfFiles = folder.listFiles();

	        	    for (int i = 0; i < listOfFiles.length; i++) {
	        	      if (listOfFiles[i].isDirectory()) {
	        	        response += ";" + listOfFiles[i].getName();
	        	      }
	        	    }
	        	    
	        		output.write(response.getBytes());
	        	} catch (Exception e){
	        		e.printStackTrace();
	        	}
	        	break;
	        case "getListDateRoom":
	        	try{
	        		System.out.println("C:\\CameraProject\\" + tab[1]);
	        	    File folder = new File("C:\\CameraProject\\" + tab[1]);
	        	    File[] listOfFiles = folder.listFiles();

	        	    for (int i = 0; i < listOfFiles.length; i++) {
	        	      if (listOfFiles[i].isDirectory()) {
	        	        response += ";" + listOfFiles[i].getName();
	        	      }
	        	    }
	        	    
	        		output.write(response.getBytes());
	        	} catch (Exception e){
	        		e.printStackTrace();
	        	}
	        	break;   
	        case "getListDateImgRoom":
	        	try{
	        		System.out.println("C:\\CameraProject\\" + tab[1] + "\\" + tab[2]);
	        	    File folder = new File("C:\\CameraProject\\" + tab[1] + "\\" + tab[2]);
	        	    File[] listOfFiles = folder.listFiles();

	        	    for (int i = 0; i < listOfFiles.length; i++) {
	        	      if (listOfFiles[i].isFile()) {
	        	        response += ";" + listOfFiles[i].getName();
	        	      }
	        	    }
	        	    
	        		output.write(response.getBytes());
	        	} catch (Exception e){
	        		e.printStackTrace();
	        	}
	        	break;
	        case "getImgFromHistory":
	        	try{
	        		System.out.println("C:\\CameraProject\\" + tab[1] + "\\" + tab[2] + "\\" + tab[3]);
	        		FileInputStream in = new FileInputStream("C:\\CameraProject\\" + tab[1] + "\\" + tab[2]+ "\\" + tab[3]);
	        		byte buf[] = new byte[1024];
	        		int n;
	        		while((n=in.read(buf))!=-1){
		        		output.write(buf,0,n);
	        		}
	        		System.out.println("stream histo img ok");
	        		in.close();
	        		output.close();
	        	} catch (Exception e){
	        		e.printStackTrace();
	        	}
	        	break;
	        }

	        client.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
}
