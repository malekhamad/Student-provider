package com.astudent.partner;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;

import com.astudent.partner.Constant.URLHelper;

public class About_Us_Activity extends AppCompatActivity {

    ImageView backArrow;
    WebView About_webview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about__us_);
        backArrow = (ImageView)findViewById(R.id.backArrow);
        About_webview = (WebView)findViewById(R.id.About_webview);

        About_webview.loadUrl(URLHelper.ABOUT_US);

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}

