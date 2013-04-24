package com.androidhive.sessions;

import core.Communication;
import core.ImpossibleConnectionException;
import android.app.Activity;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ImageView;

public class GalerieActivity extends Activity{
	
	Bitmap currentImg;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_galerie);
		
		new AsyncTask<Void, Integer, Void>() {
			@Override
			protected Void doInBackground(Void[] params) {
				
				String img_name = GalerieActivity.this.getIntent().getExtras().getString("img_name");
				String room_name = GalerieActivity.this.getIntent().getExtras().getString("room_name");
				String date = GalerieActivity.this.getIntent().getExtras().getString("date");
				
				Communication c = new Communication(GalerieActivity.this);
				c.setAppDir(GalerieActivity.this.getCacheDir().getAbsolutePath());
				try {
					currentImg = c.getImgFromHistory(room_name, date, img_name);
				} catch (ImpossibleConnectionException e) {
					e.printStackTrace();
				}			
				return null;
			}
			
			protected void onPostExecute(Void result) {
				if (currentImg != null) {
					ImageView img = (ImageView)findViewById(R.id.imageView1);
					img.setImageBitmap(currentImg);
				}
			}
		}.execute();
	}
}
