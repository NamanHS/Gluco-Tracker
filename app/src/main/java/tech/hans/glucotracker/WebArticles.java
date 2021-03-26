package tech.hans.glucotracker;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;


public class WebArticles extends AppCompatActivity {
    ProgressBar prog;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_articles);
        Intent intent = getIntent();
        String url = intent.getStringExtra("url");
        WebView webber = findViewById(R.id.webber);
        webber.getSettings().setLoadWithOverviewMode(true);
        webber.getSettings().setUseWideViewPort(true);
        webber.getSettings().setJavaScriptEnabled(true);
        webber.setWebViewClient(new WebViewClient());
        webber.loadUrl(url);

    }
}