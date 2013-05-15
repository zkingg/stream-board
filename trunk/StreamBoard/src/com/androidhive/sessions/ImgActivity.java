package com.androidhive.sessions;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import thread.ToastExpander;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.FloatMath;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SubMenu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import core.Communication;
import core.ImpossibleConnectionException;

public class ImgActivity extends Activity implements  OnTouchListener {
	private String salle = "0";
	private ArrayList<String> salles;
	private boolean refresh_needed=false;
	private ImageView image = null;
	private Toast toast;
	private TextView texte_salle;
	
	// These matrices will be used to move and zoom image
	Matrix matrix = new Matrix();
	Matrix savedMatrix = new Matrix();

	// We can be in one of these 3 states
	static final int NONE = 0;
	static final int DRAG = 1;
	static final int ZOOM = 2;
	int mode = NONE;

	// Remember some things for zooming
	PointF start = new PointF();
	PointF mid = new PointF();
	float oldDist = 1f;
	String savedItemClicked;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(LayoutParams.FLAG_FULLSCREEN,LayoutParams.FLAG_FULLSCREEN);//full screen
		requestWindowFeature(Window.FEATURE_NO_TITLE);//no titre
		setContentView(R.layout.activity_img);
		image = (ImageView) findViewById(R.id.ivImage);
		image.setOnTouchListener(this);
		texte_salle = (TextView) findViewById(R.id.salle);
		
		getListeSalles();
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.clear();
		SubMenu s = menu.addSubMenu(0,10,0,"Changer de salle");
		System.out.println(salles);
		for(String salle : salles){
			s.add(salle);
		}
		
		menu.add(0,2,0,"Rafraichir");
		menu.add(0,3,0,"Sauvegarder");
		menu.add(0,1,0,"Preference");
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
		case 1://preferences
			refresh_needed = true;
			Intent i = new Intent(this,Preference.class);
			startActivity(i);
			break;
		case 2://rafraichir
			refreshImg();
			break;
		case 3://sauvegarder
			sauvegarderImg();
			break;
		case 10://changer salle
			if(salles==null || salles.size()==0)
				Toast.makeText(this, "Imposssible de récuperer les salles ...", Toast.LENGTH_SHORT).show();
			break;
		default:
			//Log.i("",""+item.getTitle());
			if(salles.contains(""+item.getTitle())){//si option est une salle
				this.salle = ""+item.getTitle();
				texte_salle.setText(item.getTitle());
				refreshImg();
			}
			break;
		}
		return true;
	}
	
	private void getListeSalles(){
		new AsyncTask<Void,Integer,Void>()
		{
			@Override
			protected void onPreExecute() {
				startLoadAnimation();
			}
			
			@Override
			protected void onPostExecute(Void params) {
				stopLoadAnimation();
				if(salles != null && salles.size() != 0){
					salle = salles.get(0);
					texte_salle.setText(salle);
					refreshImg();
				}
			}
			
			@Override
			protected Void doInBackground(Void... params) {
				Communication c = new Communication(ImgActivity.this);
				try {ImgActivity.this.salles = c.getListSalle();}
				catch (ImpossibleConnectionException e) {publishProgress(-1);}
				
				return null;
			}
			
			@Override
			protected  void onProgressUpdate(Integer[] values) {
				if(values[0] == -1){
					Toast.makeText(ImgActivity.this,"Connection impossible", Toast.LENGTH_LONG).show();
				}
			}
		}.execute();
	}
	
	private void refreshImg(){
		new AsyncTask<Void, Integer, Void>(){
			private Bitmap img;
			
			@Override
			protected Void doInBackground(Void... params) {
				Communication c = new Communication(ImgActivity.this);
				c.setAppDir(ImgActivity.this.getCacheDir().getAbsolutePath());	
				try{img = c.getInstantImg(salle);}
				catch(ImpossibleConnectionException e){
					publishProgress(-1);
				}
				
				publishProgress(1);
				return null;
			}
			
			@Override
			protected void onProgressUpdate(Integer... values) {
				if(values[0] ==-1)//si echec connection
					Toast.makeText(ImgActivity.this,"Connection impossible", Toast.LENGTH_LONG).show();
				else{
					if(img == null)
						Toast.makeText(ImgActivity.this,"Connection impossible", Toast.LENGTH_LONG).show();
					else
						image.setImageBitmap(img);
				}
			}
			
			@Override
			protected void onPreExecute() {
				startLoadAnimation();
			}
			
			@Override
			protected void onPostExecute(Void result) {
				resize();
				stopLoadAnimation();
			}
		}.execute();
	}

	@SuppressLint("SimpleDateFormat")
	private void sauvegarderImg(){
		File file = new File(this.getCacheDir().getAbsolutePath()+"/now.png");
		if(! file.isFile()){
			return;
		}
		File img_dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath()+"/streamboard");
		//File img_dir = new File(this.getCacheDir()+"/streamboard");

		System.out.println(img_dir.getAbsolutePath());
		if(! img_dir.isDirectory())
			img_dir.mkdirs();
		
		FileInputStream in =null;
		FileOutputStream out =null;
		try {
			in = new FileInputStream(file);
			DateFormat date = new SimpleDateFormat("dd-MM-yyyy_HH-mm-ss");
			//System.out.println(date.format(new Date()));
			
			out = new FileOutputStream(img_dir+"/screen_"+salle+"_"+date.format(new Date())+".png");
			byte buf[] = new byte[1024];
			int n;
			while((n=in.read(buf))!=-1){
			    out.write(buf,0,n);
			}
			in.close();
			out.close();
			sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + Environment.getExternalStorageDirectory())));
			Toast.makeText(this, "Image sauvegardé", Toast.LENGTH_SHORT).show();
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
	
	@SuppressLint("ShowToast")
	public void startLoadAnimation(){
		findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
	}

	public void stopLoadAnimation(){
		findViewById(R.id.progressBar).setVisibility(View.GONE);
	}
	
	public void copy(String src,String dest){
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

	@Override
	protected void onResume() {
		if(refresh_needed){
			getListeSalles();
			refresh_needed = false;
		}
		
		super.onResume();
	}
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		ImageView view = (ImageView) v;

		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN:
			savedMatrix.set(matrix);
			start.set(event.getX(), event.getY());
			//Log.d("", "mode=DRAG");
			mode = DRAG;
			break;
		case MotionEvent.ACTION_POINTER_DOWN:
			oldDist = spacing(event);
			Log.d("", "oldDist=" + oldDist);
			if (oldDist > 10f) {
				savedMatrix.set(matrix);
				midPoint(mid, event);
				mode = ZOOM;
				//Log.d("", "mode=ZOOM");
			}
			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_POINTER_UP:
			mode = NONE;
			//Log.d("", "mode=NONE");
			break;
		case MotionEvent.ACTION_MOVE:
			if (mode == DRAG) {
				float dx = event.getX() - start.x;
				float dy = event.getY() - start.y;

				matrix.set(savedMatrix);
				matrix.postTranslate(dx,dy);
			} else if (mode == ZOOM) {
				float newDist = spacing(event);
				//Log.d("", "newDist=" + newDist);
				if (newDist > 10f) {
					matrix.set(savedMatrix);
					float scale = newDist / oldDist;
					matrix.postScale(scale, scale, mid.x, mid.y);
				}
			}
			break;
		}

		view.setImageMatrix(matrix);
		return true;
	}
	
	/** Determine the space between the first two fingers */
	private float spacing(MotionEvent event) {
		float x = event.getX(0) - event.getX(1);
		float y = event.getY(0) - event.getY(1);
		return FloatMath.sqrt(x * x + y * y);
	}

	/** Calculate the mid point of the first two fingers */
	private void midPoint(PointF point, MotionEvent event) {
		float x = event.getX(0) + event.getX(1);
		float y = event.getY(0) + event.getY(1);
		point.set(x / 2, y / 2);
	}
	
	private void resize(){
		Bitmap bmp = Communication.getBitmapFromURL(this.getCacheDir()+"/now.png");
		
	    int iWidth=bmp.getWidth();
        int iHeight=bmp.getHeight();

        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics dm = new DisplayMetrics();
        display.getMetrics(dm);

        int dWidth=dm.widthPixels;
        int dHeight=dm.heightPixels;

        float sWidth=((float) dWidth)/iWidth;
        float sHeight=((float) dHeight)/iHeight;

        if(sWidth>sHeight) sWidth=sHeight;
        else sHeight=sWidth;

        Matrix matrix=new Matrix();
        matrix.postScale(sWidth,sHeight);
        Bitmap newImage=Bitmap.createBitmap(bmp, 0, 0, iWidth, iHeight, matrix, true);
        image.setImageBitmap(newImage);
	}

}