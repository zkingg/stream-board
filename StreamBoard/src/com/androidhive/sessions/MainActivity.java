package com.androidhive.sessions;

import java.util.HashMap;


import android.content.Intent;
import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener{
	
	// Alert Dialog Manager
	AlertDialogManager alert = new AlertDialogManager();
	
	// Session Manager Class
	SessionManager session;
	
	// Button Logout
	Button btnLogout;
	
	// Button Cours
	Button btnCours;
	
	// Button Aide
	Button btnAide;
	
	//Button Web
	Button btnWeb;
		
	//Button List
	Button btnList;
		
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // Session class instance
        session = new SessionManager(getApplicationContext());
        
        TextView lblName = (TextView) findViewById(R.id.lblName);
        //TextView lblEmail = (TextView) findViewById(R.id.lblEmail);
        
        // Button List
        btnList = (Button) findViewById(R.id.btnList);
        btnList.setOnClickListener(this);
        
        // Button Cours
        btnCours = (Button) findViewById(R.id.btnCours);
        btnCours.setOnClickListener(this);
        
        // Button Aide
        btnAide = (Button) findViewById(R.id.btnaide);
        btnAide.setOnClickListener(this);
        
        // Button Web
        btnWeb = (Button) findViewById(R.id.btnweb);
        btnWeb.setOnClickListener(this);
        // Button logout
        btnLogout = (Button) findViewById(R.id.btnLogout);
        
        Toast.makeText(getApplicationContext(), "User Login Status: " + session.isLoggedIn(), Toast.LENGTH_LONG).show();
        
        
        /**
         * Call this function whenever you want to check user login
         * This will redirect user to LoginActivity is he is not
         * logged in
         * */
        
        //session.checkLogin();
        
        // get user data from session
        HashMap<String, String> user = session.getUserDetails();
        //Map user = session.getUserDetails();
        
        // name
        String name = user.get(SessionManager.KEY_NAME);
        
        // email
        //String email = user.get(SessionManager.KEY_EMAIL);
        
        // displaying user data
        lblName.setText(Html.fromHtml("Name: <b>" + name + "</b>"));
        //lblEmail.setText(Html.fromHtml("Email: <b>" + email + "</b>"));
        
        
        /**
         * Logout button click event
         * */
        btnLogout.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// Clear the session data
				// This will clear all session data and 
				// redirect user to LoginActivity
				session.logoutUser();
			}
		});
    }

	@Override
public void onClick(View v) {
		
		switch(v.getId()){
		
		// TODO Auto-generated method stub
		case R.id.btnCours: 
		Intent intent = new Intent(this,ImgActivity.class);
		this.startActivity(intent);	
		break;
		
		
		// TODO Auto-generated method stub
		case R.id.btnaide: 
		Intent intent2 = new Intent(this,AideActivity.class);
		this.startActivityForResult(intent2, 1000);	
		break;
		
		// TODO Auto-generated method stub
		case R.id.btnweb:
		Intent intent3 = new Intent(this,WebActivity.class);
		this.startActivityForResult(intent3, 1000);	
		break;
		
		// TODO Auto-generated method stub
		case R.id.btnList: 
		Intent intent4 = new Intent(this,ListCoursActivity.class);
		this.startActivityForResult(intent4, 1000);	
		break;
		
		}
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		session.checkLogin();
		
	}
        
	 @Override
	    protected void onRestart() {
	        super.onRestart();
	        session.checkLogin();
	    }
}