package com.androidhive.sessions;
import core.Communication;

import android.app.Activity;
import android.os.Bundle;
import android.app.ListActivity;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import java.util.ArrayList;

public class ListCoursActivity extends ListActivity{
		
		ListView lvListe;
		Communication c ;
		ArrayList<String> list;
		 
		@Override
	    public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			 setContentView(R.layout.activity_list);
			 c= new Communication(ListCoursActivity.this);
			 
			 //lvListe = (ListView)findViewById(R.layout.activity_list); 
			 //String[] listeStrings = {"Cours1","Cours2","Cours3"};	 
			  list = new ArrayList<String>();
			 list = c.getListSalle();
			 
			 System.out.println(list.toString() + " " );    
			    
			 
			 setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,list));
			 
			 //lvListe.onListItemClick(this);
			 
		}
		
	   
		protected void onListItemClick(ListView l, View v, int position, long id) {
	        Log.e("","position : "+position);
	         // Intent launcher here
			Intent intent5 = new Intent(this,CourslistActivity.class);
			String l1 = list.get(position);
			Log.e("","l : "+l1);
			intent5.putExtra("list",l1);
			
			this.startActivityForResult(intent5, 1000);	
			
	    }
			 
			 /*
			ListView listView = getListView();
			listView.setTextFilterEnabled(true);
			
			listView.setOnItemClickListener(new OnItemClickListener() {
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					// When clicked, show a toast with the TextView text
					Toast.makeText(getApplicationContext(),
							((TextView) view).getText(), Toast.LENGTH_SHORT).show();
				}
			});
			*/
		

		
		
	
}

