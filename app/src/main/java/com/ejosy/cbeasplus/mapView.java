package com.ejosy.cbeasplus;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class mapView extends AppCompatActivity {
    WebView simpleWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_view);

        simpleWebView = (WebView) findViewById(R.id.mapWebView);

        //
        Intent intent = getIntent();
        //
        String latv = intent.getStringExtra("lat_read");
        String longv = intent.getStringExtra("long_read");//if it's a string you stored.
        //
        StringBuilder str_latv = new StringBuilder(latv);
        StringBuilder str_longv = new StringBuilder(longv);
        // insert character value at offset 8
        str_latv.insert(1, '.');
        str_longv.insert(1, '.');
        //
        simpleWebView.setWebViewClient(new MyWebViewClient());
        String url = "http://maps.google.com/maps?q=loc:" + str_latv+ "," + str_longv;
        simpleWebView.getSettings().setJavaScriptEnabled(true);
        simpleWebView.loadUrl(url); // load a web page in a web view
    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }
}