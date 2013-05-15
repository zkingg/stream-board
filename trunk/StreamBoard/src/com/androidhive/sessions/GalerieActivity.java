package com.androidhive.sessions;

import core.Communication;
import core.ImpossibleConnectionException;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.FloatMath;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnTouchListener;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;

public  class GalerieActivity extends Activity  implements OnTouchListener{
	
	
		Bitmap currentImg;
		
		ImageView img;
		
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
		setContentView(R.layout.activity_galerie);
		
		//getWindow().setFlags(LayoutParams.FLAG_FULLSCREEN,LayoutParams.FLAG_FULLSCREEN);//full screen
		//requestWindowFeature(Window.FEATURE_NO_TITLE);//no titre
		
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
					img = (ImageView)findViewById(R.id.imageView1);
					img.setImageBitmap(currentImg);
					resize();
				}
				
			}
		}.execute();
		
		ImageView img = (ImageView)findViewById(R.id.imageView1);
		img.setImageBitmap(currentImg);
		
		img.setOnTouchListener(this);
		
		
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
				matrix.set(savedMatrix);
				matrix.postTranslate(event.getX() - start.x, event.getY() - start.y);
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
		Bitmap bmp = currentImg;
		
	    int iWidth=bmp.getWidth();
        int iHeight=bmp.getHeight();

        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics dm = new DisplayMetrics();
        display.getMetrics(dm);

        int dWidth=dm.widthPixels;
        int dHeight=dm.heightPixels;

        float sWidth=((float) dWidth)/iWidth;
        float sHeight=((float) dHeight)/iHeight;

        /* pour garder le ratio
        if(sWidth>sHeight) sWidth=sHeight;
        else sHeight=sWidth;*/

        Matrix matrix=new Matrix();
        matrix.postScale(sWidth,sHeight);
        
        Bitmap newImage=Bitmap.createBitmap(bmp, 0, 0, iWidth, iHeight, matrix, true);
        img.setImageBitmap(newImage);
	}

}
