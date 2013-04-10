package server;

import java.util.ArrayList;

import camera.Camera;

public class ServerRuntime extends Thread{
    
	private static ArrayList<Camera> list;
	private int mins = 2;
    
	ServerRuntime() {
		try {
			list = Camera.getAllCamera();
		} catch (Exception ex) {
			//todo
		}
		start();
	}
	
	public void run() {
		while(true) {
            for (Camera c : list) {
            	c.getImage();
            }
            try {
				Thread.sleep(1000 * 60 * mins);
				System.out.println("Imgs save");
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
