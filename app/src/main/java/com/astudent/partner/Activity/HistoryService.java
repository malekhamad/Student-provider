package com.astudent.partner.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;
import com.astudent.partner.Helper.AppHelper;
import com.astudent.partner.R;

public class HistoryService extends AppCompatActivity implements View.OnClickListener {

    ImageView imgBeforeService;
    ImageView imgAfterService;
    TextView lblBeforeService, lblAfterService;
    Activity activity;
    ImageView backArrow;
    String before_comment = "", before_image = "";
    String after_comment = "", after_image = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
            getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
        }
        setContentView(R.layout.activity_history_service);
        findviewById();
        setOnClickListener();
    }

    private void findviewById() {
        imgBeforeService = (ImageView) findViewById(R.id.imgBeforeService);
        imgAfterService = (ImageView) findViewById(R.id.imgAfterService);
        backArrow = (ImageView) findViewById(R.id.backArrow);
        lblBeforeService = (TextView) findViewById(R.id.lblBeforeService);
        lblAfterService = (TextView) findViewById(R.id.lblAfterService);
        before_comment = getIntent().getExtras().getString("before_comment");
        after_comment = getIntent().getExtras().getString("after_comment");
        before_image = getIntent().getExtras().getString("before_image");
        after_image = getIntent().getExtras().getString("after_image");
        //Before Part

        if (before_comment.equalsIgnoreCase("") || before_comment.equalsIgnoreCase("null"))
            lblBeforeService.setText("No comments found!");
        else
            lblBeforeService.setText(before_comment);

        if (!before_image.equalsIgnoreCase(""))
            Picasso.with(activity).load(AppHelper.getImageUrl(before_image)).memoryPolicy(MemoryPolicy.NO_CACHE).placeholder(R.drawable.no_image).error(R.drawable.no_image).into(imgBeforeService);
        else
            imgBeforeService.setBackgroundResource(R.drawable.no_image);
        //After Part
        if (after_comment.equalsIgnoreCase("") || after_comment.equalsIgnoreCase("null"))
            lblAfterService.setText("No comments found!");
        else
            lblAfterService.setText(after_comment);

        if (!after_image.equalsIgnoreCase(""))
            Picasso.with(activity).load(AppHelper.getImageUrl(after_image)).memoryPolicy(MemoryPolicy.NO_CACHE).placeholder(R.drawable.no_image).error(R.drawable.no_image).into(imgAfterService);
        else
            imgAfterService.setBackgroundResource(R.drawable.no_image);
    }

    private void setOnClickListener() {
        imgBeforeService.setOnClickListener(this);
        imgAfterService.setOnClickListener(this);
        backArrow.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == imgBeforeService) {
            if (before_image != null && !before_image.equalsIgnoreCase("null") && before_image.length() > 0) {
                Intent intent = new Intent(HistoryService.this, ShowPicture.class);
                intent.putExtra("image", AppHelper.getImageUrl(before_image));
                startActivity(intent);
            }

        }
        if (v == imgAfterService) {
            if (after_image != null && !after_image.equalsIgnoreCase("null") && after_image.length() > 0) {
                Intent intent = new Intent(HistoryService.this, ShowPicture.class);
                intent.putExtra("image", AppHelper.getImageUrl(after_image));
                startActivity(intent);
            }
        }

        if (v == backArrow) {
            finish();
        }
    }

}
