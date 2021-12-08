package com.astudent.partner.Activity;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.facebook.login.LoginManager;
import com.astudent.partner.Constant.URLHelper;
import com.astudent.partner.Helper.SharedHelper;
import com.astudent.partner.R;
import com.astudent.partner.TutorApplication;

import org.json.JSONObject;

import java.util.HashMap;

public class WaitingForApproval extends AppCompatActivity {
    Button logoutBtn;
    public Handler ha;
    private String token;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_waiting_for_approval);
        token = SharedHelper.getKey(WaitingForApproval.this, "access_token");
        logoutBtn = (Button) findViewById(R.id.logoutBtn);
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SharedHelper.getKey(WaitingForApproval.this, "login_by").equals("facebook")) {
                    LoginManager.getInstance().logOut();
                    SharedHelper.putKey(WaitingForApproval.this, "current_status", "");
                    SharedHelper.putKey(WaitingForApproval.this, "login_by", "");
                } else {

                }
                SharedHelper.putKey(WaitingForApproval.this, "loggedIn", getString(R.string.False));
                Intent mainIntent = new Intent(WaitingForApproval.this, ActivityPassword.class);
                mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(mainIntent);
//                WaitingForApproval.this.finish();
                finish();
            }
        });

        ha = new Handler();
        //check status every 3 sec
        ha.postDelayed(new Runnable() {
            @Override
            public void run() {
                //call function
                checkStatus();
                ha.postDelayed(this, 2000);
            }
        }, 2000);
    }


    @Override
    protected void onRestart() {
        super.onRestart();

    }

    @Override
    public void onBackPressed() {

    }

    private void checkStatus() {
        String url = URLHelper.BASE_URL + "/api/provider/trip";

        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                Log.e("CheckStatus", "" + response.toString());
                //SharedHelper.putKey(context, "currency", response.optString("currency"));

                if (response.optString("account_status").equals("approved")) {
                    ha.removeCallbacksAndMessages(null);
                    startActivity(new Intent(WaitingForApproval.this, Home.class));
                    finish();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.v("Error", error.toString());
                //errorHandler(error);
            }
        }) {
            @Override
            public java.util.Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("X-Requested-With", "XMLHttpRequest");
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };
        TutorApplication.getInstance().addToRequestQueue(jsonObjectRequest);
    }


}
