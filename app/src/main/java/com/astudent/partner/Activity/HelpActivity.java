package com.astudent.partner.Activity;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;

import com.astudent.partner.Constant.URLHelper;
import com.astudent.partner.R;

public class HelpActivity extends AppCompatActivity {

    ImageView backArrow;
    WebView Contact_webview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        backArrow = (ImageView)findViewById(R.id.backArrow);
        Contact_webview = (WebView)findViewById(R.id.Contact_webview);

        Contact_webview.loadUrl(URLHelper.HELP_WEB_URL);

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
    }

