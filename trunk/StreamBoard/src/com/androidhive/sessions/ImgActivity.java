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
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import core.Communication;

public class ImgActivity extends Activity implements OnClickListener, OnItemSelectedListener {
	private String salle = "0";
	private ImageView image = null;
	private Toast toast;
	private Spinner spinner;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		getWindow().setFlags(LayoutParams.FLAG_FULLSCREEN,LayoutParams.FLAG_FULLSCREEN);//full screen
		requestWindowFeature(Window.FEATURE_NO_TITLE);//no titre
		setContentView(R.layout.activity_img);
		image = (ImageView) findViewById(R.id.ivImage);
		findViewById(R.id.btn_refresh).setOnClickListener(this);
		findViewById(R.id.btn_sav).setOnClickListener(this);
		findViewById(R.id.ivImage).setOnClickListener(this);
		spinner = (Spinner)findViewById(R.id.spinner1);
		findViewById(R.id.zone_boutons).setVisibility(View.GONE);
		spinner.setOnItemSelectedListener(this);
		
		alimenterSpinner(spinner);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0,1,0,"Preference");
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
		case 1:
			Intent i = new Intent(this,Preference.class);
			startActivity(i);
		break;
		}
		return true;
	}
	
	private void alimenterSpinner(Spinner s){
		new AsyncTask<Void,Void,Void>()
		{
			private ArrayList<String> list;
			
			@SuppressWarnings({ "unchecked", "rawtypes" })
			@Override
			protected void onPostExecute(Void params) {
				if(list != null && list.size() != 0)
					spinner.setAdapter(new ArrayAdapter(ImgActivity.this,android.R.layout.simple_spinner_item,list));
				else
					spinner.setAdapter(new ArrayAdapter(ImgActivity.this,android.R.layout.simple_spinner_item,new Array[]{}));
				
				if(spinner.getCount() != 0)
					salle = spinner.getItemAtPosition(spinner.getFirstVisiblePosition()).toString();
							
				refreshImg();
			}
			
			@Override
			protected Void doInBackground(Void... params) {
				Communication c = new Communication(ImgActivity.this);
				list = c.getListSalle();
				return null;
			}
		}.execute();
	}
	
	private void refreshImg(){
		startLoadAnimation();
		new Thread() {
			private Bitmap img;
			public void run(){
				Communication c = new Communication(ImgActivity.this);
				c.setAppDir(ImgActivity.this.getCacheDir().getAbsolutePath());		
				img = c.getInstantImg(salle);
				runOnUiThread(new Thread() {
					public void run(){
						if(img == null)
							Toast.makeText(ImgActivity.this,"Connection impossible", Toast.LENGTH_LONG).show();
						else
							image.setImageBitmap(img);
					}
				});
				stopLoadAnimation();
			}
		}.start();
	
	}
	
	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.btn_refresh:
			refreshImg();
		break;
		case R.id.btn_sav:			
			sauvegarderImg();
		break;
		case R.id.ivImage:
			View v = findViewById(R.id.zone_boutons);
			if(v.getVisibility() == View.GONE){
				v.setVisibility(View.VISIBLE);
				v.startAnimation(AnimationUtils.loadAnimation(this,android.R.anim.fade_in));
			}else{
				v.startAnimation(AnimationUtils.loadAnimation(this,android.R.anim.fade_out));
				v.setVisibility(View.GONE);
			}
			break;
		}
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
		toast = Toast.makeText(getApplicationContext(),R.string.app_name, Toast.LENGTH_SHORT);
		LayoutInflater inflater = getLayoutInflater();
		View new_view = inflater.inflate(R.layout.load_layout,(ViewGroup)findViewById(R.id.loadlayout));
		toast.setView(new_view);
		toast.setGravity(Gravity.CENTER, 0, 0);
		ToastExpander.showFor(toast, 5000);

	}

	public void stopLoadAnimation(){
		if(toast != null){
			toast.cancel();
			ToastExpander.stop();
		}
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
	public void onItemSelected(AdapterView<?> parent, View arg1, int pos,long arg3) {
		salle = parent.getItemAtPosition(pos).toString();
		refreshImg();
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub
		Log.e("nothing","");
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}
}