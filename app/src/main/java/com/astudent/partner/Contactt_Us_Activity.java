package com.astudent.partner;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;

import com.astudent.partner.Constant.URLHelper;

public class Contactt_Us_Activity extends AppCompatActivity {

    ImageView backArrow;
    WebView Contact_webview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contactt__us_);
        backArrow = (ImageView) findViewById(R.id.backArrow);
        Contact_webview = (WebView) findViewById(R.id.Contact_webview);

        Contact_webview.loadUrl(URLHelper.CONTACT_Us);

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
