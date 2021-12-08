package com.astudent.partner.Activity;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.astudent.partner.Helper.SharedHelper;
import com.astudent.partner.R;
import com.astudent.partner.Utils.LocaleUtils;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);


        final RadioGroup chooseLanguage = findViewById(R.id.choose_language);
        final RadioButton english = findViewById(R.id.english);
        final RadioButton arabic = findViewById(R.id.arabic);

        String dd = LocaleUtils.getLanguage(this);
        switch (dd) {
            case "en":
                english.setChecked(true);
                break;
            case "ar":
                arabic.setChecked(true);
                break;
            default:
                english.setChecked(true);
                break;
        }

        chooseLanguage.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.english:
                        setLanguage("English");
                        break;
                    case R.id.arabic:
                        setLanguage("Arabic");
                        break;
                }
            }
        });


    }

    private void setLanguage(String value) {
        SharedHelper.putKey(this, "language", value);
        switch (value) {
            case "English":
                LocaleUtils.setLocale(this, "en");
                break;
            case "Arabic":
                LocaleUtils.setLocale(this, "ar");
                break;
            default:
                LocaleUtils.setLocale(this, "en");
                break;
        }
        startActivity(new Intent(this, Home.class)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK)
                .putExtra("change_language", true));
        this.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
}