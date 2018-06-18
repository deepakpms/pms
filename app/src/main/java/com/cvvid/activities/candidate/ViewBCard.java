package com.cvvid.activities.candidate;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.cvvid.R;

public class ViewBCard extends AppCompatActivity {

    ProgressBar pb;
    WebView  wv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_bcard);

        getSupportActionBar().setTitle("Bussiness Card");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        Intent intent = getIntent();
        String doc = intent.getStringExtra("url");

        wv = (WebView)findViewById(R.id.webview);
        pb = (ProgressBar)findViewById(R.id.progressBar);
        wv.getSettings().setJavaScriptEnabled(true);
        wv.getSettings().setPluginState(WebSettings.PluginState.ON);
        wv.setWebViewClient(new myWebClient());
        wv.loadUrl("http://docs.google.com/gview?embedded=true&url=" + doc);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    public class myWebClient extends WebViewClient
    {
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            pb.setVisibility(View.GONE);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public boolean shouldOverrideUrlLoading(
                WebView view, String url) {
            view.loadUrl(url);
            return super.shouldOverrideUrlLoading(view, url);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if((keyCode == KeyEvent.KEYCODE_BACK) && wv.canGoBack()){
            wv.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
