package com.androidhive.sessions;
import java.util.ArrayList;

import core.Communication;
import core.ImpossibleConnectionException;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;


public class HistActivity extends ListActivity {
	
	ListView lvListe;
	Communication c ;
	ArrayList<String> list;
	//String date="06-02-2013",salle="AMPHI-D1";
	String room_name;
	String date;
	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		 setContentView(R.layout.activity_hist);
		 
		 list = new ArrayList<String>();
		
		 
		 new AsyncTask<Void, Integer, Void>() {
			
			@Override
			protected void onPostExecute(Void result) {
				if(list.size() == 0)
					Toast.makeText(HistActivity.this, "Impossible d'afficher la liste des images",Toast.LENGTH_LONG).show();
				else
					setListAdapter(new ArrayAdapter<String>(HistActivity.this, android.R.layout.simple_list_item_1,list));
			}
			 
			@Override
			protected Void doInBackground(Void... params) {
				c= new Communication(HistActivity.this);
				try {

					room_name = HistActivity.this.getIntent().getExtras().getString("room_name");
					date = HistActivity.this.getIntent().getExtras().getString("list");
					list = c.getListDateImgRoom(room_name, date);

					System.out.println(list.toString() + " " );
					
				} catch (ImpossibleConnectionException e) {
					publishProgress(-1);
				}
				return null;
			}
			
			protected  void onProgressUpdate(Integer... values) {
				if(values[0] == -1){
					Toast.makeText(HistActivity.this,"Connection Impossible ...", Toast.LENGTH_LONG).show();
				}
			}
		}.execute();
		
		 System.out.println(list.toString() + " " );
		  
	}
	
	protected void onListItemClick(ListView l, View v, int position, long id) {
		Intent intent_galerie = new Intent(this,GalerieActivity.class);
		String img_name = list.get(position);
		intent_galerie.putExtra("img_name", img_name);
		intent_galerie.putExtra("room_name", room_name);
		intent_galerie.putExtra("date", date);
		
		this.startActivity(intent_galerie);	
    }
}
