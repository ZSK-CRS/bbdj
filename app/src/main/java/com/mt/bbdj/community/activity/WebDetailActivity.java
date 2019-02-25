package com.mt.bbdj.community.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.base.BaseActivity;

public class WebDetailActivity extends BaseActivity {

    private ProgressBar progressBar;
    private WebView webView;
    private String url;
    private RelativeLayout ivBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_detail);
        initParams();
        initView();
        loadData();
        initListener();
    }

    private void loadData() {
        WebSettings webSettings=webView.getSettings();
        //支持屏幕缩放
        webSettings.setSupportZoom(false);
        webSettings.setTextZoom(100);

        webSettings.setBuiltInZoomControls(false);
        webView.loadUrl(url);
        webView.setVerticalScrollBarEnabled(false); //垂直不显示
     //   webView.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);//滚动条在WebView内侧显示

    }

    private void initListener() {
        webView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    progressBar.setVisibility(View.GONE);
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    progressBar.setProgress(newProgress);
                }
            }
        });

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (webView.canGoBack()) {
                    webView.goBack();
                } else {
                    finish();
                }
            }
        });
    }

    private void initParams() {
        Intent intent = getIntent();
        url = intent.getStringExtra("urllink");
    }

    private void initView() {
        progressBar = findViewById(R.id.progressbar);
        webView = findViewById(R.id.webview);
        ivBack = findViewById(R.id.iv_back);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (webView.canGoBack() && keyCode == KeyEvent.KEYCODE_BACK) {
            webView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        webView.destroy();
    }
}
