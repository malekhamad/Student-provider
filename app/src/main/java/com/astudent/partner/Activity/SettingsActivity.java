package com.astudent.partner.Activity;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.astudent.partner.Constant.URLHelper;
import com.astudent.partner.Helper.SharedHelper;
import com.astudent.partner.R;
import com.astudent.partner.TutorApplication;
import com.astudent.partner.Utils.LocaleUtils;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

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
        String lang = "";
        switch (value) {
            case "Arabic":
                LocaleUtils.setLocale(this, "ar");
                lang= "ar";

                break;
            default:
                LocaleUtils.setLocale(this, "en");
                lang= "en";

                break;
        }
        sendLanguageRequest(lang);

    }

    private void sendLanguageRequest(String languageCode){
        JSONObject object = new JSONObject();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URLHelper.SEND_LANGUAGE+"?locale="+languageCode ,object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                Log.i("Language", "onResponse: send language successfully");
                // we call these methods once backend return successfully or failure
                startActivity(new Intent(SettingsActivity.this, Home.class)
                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        .putExtra("change_language", true));
                SettingsActivity.this.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("Language", "onResponse: send language Failure");
                // we call these methods once backend return successfully or failure
                startActivity(new Intent(SettingsActivity.this, Home.class)
                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        .putExtra("change_language", true));
                SettingsActivity.this.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization",""+SharedHelper.getKey(SettingsActivity.this, "token_type")+" "+SharedHelper.getKey(SettingsActivity.this, "access_token"));
                return headers;
            }
        };

        TutorApplication.getInstance().addToRequestQueue(jsonObjectRequest);


    }
}