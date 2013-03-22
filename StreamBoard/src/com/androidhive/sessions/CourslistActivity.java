package com.androidhive.sessions;



import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import core.Communication;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;




public class CourslistActivity extends ListActivity{
	
	ListView lvListe;
	Communication c ;
	ArrayList<String> list;
	
	//
	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		 setContentView(R.layout.activity_courslist);
		 
		 c= new Communication(CourslistActivity.this);
		 
		 list = new ArrayList<String>();
		 list = c.getListDateSalle(this.getIntent().getExtras().getString("list"));
		 
		 System.out.println(list.toString() + " " );    
		    
		 
		 setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,list));
		
	}
	

	protected void onListItemClick(ListView l, View v, int position, long id) {
        
         // Intent launcher here
//		Intent intent5 = new Intent(this,CourslistActivity.class);
//		intent5.putExtra("list",list.get(position));
//		this.startActivityForResult(intent5, 1000);	
		//startActivity(intent5); 
    }

	
	
}