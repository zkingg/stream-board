package com.androidhive.sessions;



import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import core.Communication;
import core.ImpossibleConnectionException;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;




public class CourslistActivity extends ListActivity{
	
	ListView lvListe;
	Communication c ;
	ArrayList<String> list;
	String room_name;
	
	//
	@Override
    public void onCreate(Bundle savedInstanceState) {
		 super.onCreate(savedInstanceState);
		 setContentView(R.layout.activity_courslist);
		 list = new ArrayList<String>();
		
		 new AsyncTask<Void, Integer, Void>() {
			
			@Override
			protected void onPostExecute(Void result) {
				if(list.size() == 0)
					Toast.makeText(CourslistActivity.this, "Impossible d'afficher la liste des dates",Toast.LENGTH_LONG).show();
				else
					setListAdapter(new ArrayAdapter<String>(CourslistActivity.this, android.R.layout.simple_list_item_1,list));
			}
			 
			@Override
			protected Void doInBackground(Void... params) {
				c= new Communication(CourslistActivity.this);
				try {
					room_name = CourslistActivity.this.getIntent().getExtras().getString("list");
					list = c.getListDateSalle(room_name);
				} catch (ImpossibleConnectionException e) {
					publishProgress(-1);
				}
				return null;
			}
			
			protected  void onProgressUpdate(Integer... values) {
				if(values[0] == -1){
					Toast.makeText(CourslistActivity.this,"Connection Impossible ...", Toast.LENGTH_LONG).show();
				}
			}
		}.execute();
		
		 System.out.println(list.toString() + " " );
		 
		 
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
        
		Log.e("","position : "+position);
        // Intent launcher here
		Intent intent7 = new Intent(this,HistActivity.class);
		String l1 = list.get(position);
		Log.e("","l : "+l1);
		intent7.putExtra("list",l1);
		intent7.putExtra("room_name", room_name);
		
		this.startActivityForResult(intent7, 1000);	
    }

	 @Override
		public boolean onCreateOptionsMenu(Menu menu) {
			// TODO Auto-enerated method stub
			menu.add(0,1,0,"Preference");
			return true;
		}
		
	@Override
		public boolean onOptionsItemSelected(MenuItem item) {
			// TODO Auto-generated method stub
			switch(item.getItemId()){
			case 1:
				Intent i = new Intent(this,Preference.class);
				startActivity(i);
			break;
			}
			return true;
		}
	
}