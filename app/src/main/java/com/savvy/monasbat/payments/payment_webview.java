package com.savvy.monasbat.payments;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.savvy.monasbat.R;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

public class payment_webview extends AppCompatActivity {
    WebView webView;
    String TAG = "payment";
    String G_url = "";
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_webview);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void init() {
        webView = findViewById(R.id.webview);
        WebSettings settings = webView.getSettings();
        webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setAllowContentAccess(true);
        webView.getSettings().setAllowFileAccessFromFileURLs(true);
        webView.getSettings().setAllowUniversalAccessFromFileURLs(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);


        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String weburl) {
                System.out.println("URL AFTER=" + view.getUrl());
                //  String weburl = view.getUrl();
                //  url = url.substring(url.length() -12);
                if (weburl.contains("Syber_return")) {
                    finishPay(1);
                } else if (weburl.contains("cancelPayment")) {
                    finishPay(2);
                } else if (weburl.contains("returnPayment")) {
                    finishPay(1);
                }
                //else

            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                System.out.println(G_url + "url in loading =" + url);
                if (url.contains("Syber_return")) {
                    finishPay(1);
                } else if (url.contains("cancel")) {
                    finishPay(2);
                } else if (url.contains("returnPayment")) {
                    finishPay(1);
                } else if (url.equals(G_url)) {
                    finishPay(2);
                }
                return true;
            }

        });

        G_url = getIntent().getExtras().getString("url", "").replace("\"", "");
        System.out.println("GGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG" + G_url);
        webView.loadUrl(G_url);
    }

    private void finishPay(final int result) {

        setResult(result);
                finish();


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishPay(0);
    }


}
