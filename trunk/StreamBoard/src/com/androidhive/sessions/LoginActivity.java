package com.androidhive.sessions;

import java.util.Calendar;
import java.util.GregorianCalendar;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import core.Communication;
import core.ImpossibleConnectionException;

public class LoginActivity extends Activity {
	private ProgressDialog progress = null;
	EditText txtUsername;	
	Button btnLogin;
	AlertDialogManager alert = new AlertDialogManager();
	SessionManager session;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login); 
		// Session Manager
		session = new SessionManager(getApplicationContext());                

		// Email, Password input text
		txtUsername = (EditText) findViewById(R.id.txtUsername);
		// txtPassword = (EditText) findViewById(R.id.txtPassword); 

		//Toast.makeText(getApplicationContext(), "User Login Status: " + session.isLoggedIn(), Toast.LENGTH_LONG).show();


		// Login button
		btnLogin = (Button) findViewById(R.id.btnLogin);


		// Login button click event
		btnLogin.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// Get username, password from EditText
					startLoadAnimation();
					new Thread(){public void run(){
						String username = txtUsername.getText().toString();
						if(username.trim().length() > 0 ){
							Communication com = new Communication(LoginActivity.this);
							long id_user = 0;
							
							try {id_user = com.login(username);}
							catch (ImpossibleConnectionException e) {id_user = -10;}
							
							if((id_user) > 0){
								stopLoadAnimation();
								//session.createLoginSession(username, "");
								session.createLoginSession(username);
								
								// Staring MainActivity
								Intent i = new Intent(getApplicationContext(), MainActivity.class);
								startActivity(i);
								finish();
	
							}else{
								stopLoadAnimation();
								// username / password doesn't match
								if(id_user == -10)
									runOnUiThread(new Thread(){public void run(){alert.showAlertDialog(LoginActivity.this, "Erreur de communication", "Connexion impossible", false);}}) ;
								else
									runOnUiThread(new Thread(){public void run(){alert.showAlertDialog(LoginActivity.this, "Login failed..", "Username is incorrect", false);}}) ;
									
							}
						}else{
							runOnUiThread(new Thread(){public void run(){alert.showAlertDialog(LoginActivity.this, "Login failed..", "Please entrer your login ...", false);}}) ;
						}
					}}.start();
			}
		});
	}  

	public void startLoadAnimation(){
		progress = new ProgressDialog(this);
		progress.setCancelable(true);
		progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progress.setMessage("Connecting to server...");
		progress.show();
	}

	public void stopLoadAnimation(){
		if(progress != null){
			progress.cancel();
			progress = null;
		}
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