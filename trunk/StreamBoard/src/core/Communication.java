package core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;
import android.util.Log;


public class Communication{
	//* non local
	public String SRV_ADDR;
	//public static String SRV_ADDR = "DamienUX-PC";

	public String  SRV_PORT = "8080";//*/
	public static long TIME_MAX_ALLOWED = 5000 ;
	
	private Activity activity;
	private Socket s;
	private boolean is_connected = false;
	private String app_dir;
	
	public void setAppDir(String app_dir){this.app_dir = app_dir;}
	public String getAppDir(){return this.app_dir;}
	
	public Communication(Activity act){
		this.activity = act;
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this.activity);
		SRV_ADDR = pref.getString("ip_server","10.10.162.110");
	}
	
	public class Connection extends Thread{
		public void run(){
			System.out.println(SRV_ADDR+","+Integer.parseInt(SRV_PORT));
			try {
				s = new Socket(SRV_ADDR,Integer.parseInt(SRV_PORT));
				is_connected = s.isClosed();
			} catch (Exception e) { is_connected = false; }
		}
	}
	
	/*public Communication(){
		activity = null;
		SRV_ADDR = "10.10.162.110";
	}*/
	
	public boolean isConneted(){return this.is_connected;}
	
	/*
	 * @return id 
	 */
	public int login(String login) throws ImpossibleConnectionException{
		try {
			initSocket();
			System.out.println("Requete de connection :"+login);
			String str = ("login;"+login);
			envoi(str);
			System.out.println("Attente de réponse");
			String reponse = reception();				
			System.out.println("Fermeture socket");
			return Integer.parseInt(reponse);
		}
		catch( NumberFormatException e ){
			return -1;
		}
		catch(Exception e){
			e.printStackTrace();
			return -10;
		}
		finally{closeSocket();}
	}
	
	public ArrayList<String> getListDateSalle(String salle) throws ImpossibleConnectionException{
		ArrayList<String> list = new ArrayList<String>();
		try {
			initSocket();
			System.out.println("Demande de l'historique pour la salle:"+salle);
			String str = ("getListDateRoom;"+salle);
			envoi(str);
			System.out.println("Attente de réponse");
			String reponse = reception();	
			/*mise en forme */
			String[] tab_salle = reponse.split(";");
			for(String tmp_salle: tab_salle){
				System.out.println(salle);
				if(!tmp_salle.equals("") && !tmp_salle.equals("null"))
					list.add(tmp_salle);
			}
			
			System.out.println("Fermeture socket");
		}
		catch( NumberFormatException e ){
		}
		catch(Exception e){
			e.printStackTrace();
		}
		finally{closeSocket();}
		return list;
	}
	
	public ArrayList<String> getListDateImgRoom(String salle,String date) throws ImpossibleConnectionException{
		ArrayList<String> list = new ArrayList<String>();
		try {
			initSocket();
			System.out.println("Demande la liste des fichiers d'image par salle et date ,pour la salle"+salle);
			String str = ("getListDateImgRoom;"+salle+";"+date);
			envoi(str);
			System.out.println("Attente de réponse");
			String reponse = reception();	
			//mise en forme
			String[] tab_salle = reponse.split(";");
			for(String tmp_salle: tab_salle){
				System.out.println(salle);
				if(!tmp_salle.equals("") && !tmp_salle.equals("null"))
					list.add(tmp_salle);
			}
			
			System.out.println("Fermeture socket");
		}
		catch( NumberFormatException e ){
		}
		catch(Exception e){
			e.printStackTrace();
		}
		finally{closeSocket();}
		return list;
	}
	
	public Bitmap getImgFromHistory(String salle,String date,String fichier) throws ImpossibleConnectionException{
		try {
			Bitmap img = null;
			initSocket();
			envoi("getImgFromHistory;"+salle+";"+date+";"+fichier);
			InputStream in = s.getInputStream();
			
			File img_dir = new File(app_dir+"/history/");
			if(! img_dir.isDirectory())
				img_dir.mkdirs();
			
			String name = app_dir+"/history/"+fichier;
			//String name = Environment.getExternalStorageDirectory() + "/Pictures/now.png";
			System.out.println(name);
			File f = new File(name);
			if(f.isFile()){
				System.out.println("img trouvee");
			} else {
				System.out.println("nope");
			}
			FileOutputStream out = new FileOutputStream(name);
			//File picture = new File(name);
			byte buf[] = new byte[1024];
			int n;
			while((n=in.read(buf))!=-1){
			    out.write(buf,0,n);
			}
			in.close();
			out.close();
			
			//img = getBitmapFromURL(System.getProperty("java.io.tmpdir")+"/now.png");
			System.out.println("etape 0");
			img = getBitmapFromURL(name);
			System.out.println("etape 1");
			return img;
		} catch (Exception e) {
			Log.w("Exception", e.getMessage());
			return null;
		}
		finally{closeSocket();}
	}
	
	public ArrayList<String> getListSalle() throws ImpossibleConnectionException{
		ArrayList<String> list = new ArrayList<String>();
		try {
			initSocket();
			String str = "getListRoom";
			System.out.println("Action:getListRoom");
			envoi(str);
			System.out.println("Attente de réponse");
			String reponse = reception();				
			System.out.println("Fermeture socket");
			
			/*mise en forme */
			String[] tab_salle = reponse.split(";");
			for(String salle: tab_salle){
				System.out.println(salle);
				if(!salle.equals("") && !salle.equals("null"))
					list.add(salle);
			}
			
			return list;//si no element return null 
		}
		catch( NumberFormatException e ){}
		catch( ImpossibleConnectionException e){ throw new ImpossibleConnectionException(e.getMessage());}
		catch(Exception e){
			e.printStackTrace();
		}
		finally{closeSocket();}
		
		return list;
	}
	
	public static Bitmap getBitmapFromURL(String src) {
	    try {
	        Bitmap myBitmap = BitmapFactory.decodeFile(src);
	        return myBitmap;
	    } catch (Exception e) {
	        e.printStackTrace();
	        return null;
	    }
	}
	
	public Bitmap getInstantImg(String salle)throws ImpossibleConnectionException{
		try {
			Bitmap img = null;
			initSocket();
			envoi("getCurrentImage;"+salle);
			InputStream in = s.getInputStream();
			
			String name = app_dir+"/now.png";
			//String name = Environment.getExternalStorageDirectory() + "/Pictures/now.png";
			System.out.println(name);
			File f = new File(name);
			if(f.isFile()){
				System.out.println("img trouvee");
			} else {
				System.out.println("nope");
			}
			FileOutputStream out = new FileOutputStream(name);
			//File picture = new File(name);
			byte buf[] = new byte[1024];
			int n;
			while((n=in.read(buf))!=-1){
			    out.write(buf,0,n);
			}
			in.close();
			out.close();
			
			//img = getBitmapFromURL(System.getProperty("java.io.tmpdir")+"/now.png");
			System.out.println("etape 0");
			img = getBitmapFromURL(name);
			System.out.println("etape 1");
			return img;
		} catch (Exception e) {
			//Log.w("Exception", e.getMessage());
			e.printStackTrace();
			return null;
		}
		finally{closeSocket();}
	}
	
	@SuppressWarnings("unused")
	private void envoi(byte b[]){
		try {
			OutputStream o = s.getOutputStream();
			o.write(b);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void envoi(String str){
		try {
			PrintStream p = new PrintStream(s.getOutputStream());
			System.out.println("envoi en cour ...");
			p.println(str);		
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
	
	private String reception(){
		try {
			String rep= new BufferedReader(new InputStreamReader(s.getInputStream())).readLine();
			System.out.println("réponse :"+rep);
			return rep;
		}
		catch (SocketTimeoutException e) {
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

	}
	
	private void initSocket() throws Exception{	
		Connection c = new Connection();
		c.start();
		Calendar debut = new GregorianCalendar();
		while(c.isAlive()){
			try {Thread.sleep(500);}
			catch (InterruptedException e) {}
			Calendar now = new GregorianCalendar();
			if((now.getTimeInMillis() - debut.getTimeInMillis()) > TIME_MAX_ALLOWED){
				throw new ImpossibleConnectionException("Connection Impossible ...");
			}			
		}
	}
	
	private void closeSocket(){ 
		try {
			s.close();
		} catch (Exception e) {}
	}
}
