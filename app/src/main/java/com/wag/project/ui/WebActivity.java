package com.wag.project.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.webkit.WebView;

import com.wag.project.R;

/**
 * Webview Activity class that displays webview with user details on ReclyclerView click.
 */
public class WebActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);

        final Toolbar toolbar   = (Toolbar)findViewById(R.id.toolbar);
        final WebView webView = (WebView) findViewById(R.id.webview);
        setSupportActionBar(toolbar);

        final Intent intent = getIntent();
        final String webLink = intent.getStringExtra(getString(R.string.user_link));
        webView.loadUrl(webLink);
    }

}
