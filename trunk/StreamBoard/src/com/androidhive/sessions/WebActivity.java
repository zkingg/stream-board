package com.androidhive.sessions;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

public class WebActivity  extends Activity {
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
 
        
        WebView myWebView = (WebView) findViewById(R.id.webview);
        //myWebView.loadUrl("https://www.google.fr/");
        myWebView.loadUrl("http://www.sublicraft.org/");
 }
}
