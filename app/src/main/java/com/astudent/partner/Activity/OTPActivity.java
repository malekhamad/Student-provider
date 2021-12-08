package com.astudent.partner.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.snackbar.Snackbar;
import androidx.core.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.astudent.partner.Constant.URLHelper;
import com.astudent.partner.Helper.CustomDialog;
import com.astudent.partner.Helper.SharedHelper;
import com.astudent.partner.R;
import com.astudent.partner.TutorApplication;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class OTPActivity extends Activity {

    EditText txtOTP;
    TextView lblClickToResend;
    Button btnSendOTP;
    ImageView imgBack;
    Context context;
    String strOTP = "";
    CustomDialog customDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.otp_screen);
        context = this;
        init();
    }

    private void init() {
        txtOTP = (EditText) findViewById(R.id.txtOTP);
        lblClickToResend = (TextView) findViewById(R.id.lblClickToResend);
        btnSendOTP = (Button) findViewById(R.id.btnSendOTP);
        imgBack = (ImageView) findViewById(R.id.imgBack);
        txtOTP.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        btnSendOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("", "onClick: otp " + SharedHelper.getKey(context, "otp"));
                if (txtOTP.getText().toString().equalsIgnoreCase("")){
                    Toast.makeText(context, "Please enter OTP!", Toast.LENGTH_SHORT).show();
                }else if (!txtOTP.getText().toString().equalsIgnoreCase(SharedHelper.getKey(context, "otp"))){
                    Toast.makeText(context, "Your OTP is incorrect!", Toast.LENGTH_SHORT).show();
                } else{
                    Intent resetIntent = new Intent(context, ChangePassword.class);
                    startActivity(resetIntent);
                    finish();
                }
            }
        });

        lblClickToResend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                forgetPassword();
            }
        });

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void forgetPassword() {
        customDialog = new CustomDialog(context);
        customDialog.setCancelable(false);
        customDialog.show();
        JSONObject object = new JSONObject();
        try {
            object.put("email", SharedHelper.getKey(context, "email"));
            Log.e("InputToLoginAPI",""+object);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLHelper.FORGET_PASSWORD, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                customDialog.dismiss();
                displayMessage(""+response.optString("message"));

                JSONObject userObj = response.optJSONObject("provider");

                SharedHelper.putKey(context, "otp", ""+userObj.optString("otp"));
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                customDialog.dismiss();
                String json = null;
                String Message;
                NetworkResponse response = error.networkResponse;
                if(response != null && response.data != null){
                    try {
                        JSONObject errorObj = new JSONObject(new String(response.data));

                        if(response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500){
                            try{
                                displayMessage(errorObj.optString("message"));
                            }catch (Exception e){
                                displayMessage("Something went wrong.");
                            }
                        }else if(response.statusCode == 401){
                            try{
                                if(errorObj.optString("message").equalsIgnoreCase("invalid_token")){
                                    //Call Refresh token
                                }else{
                                    displayMessage(errorObj.optString("message"));
                                }
                            }catch (Exception e){
                                displayMessage("Something went wrong.");
                            }

                        }else if(response.statusCode == 422){

                            json = TutorApplication.trimMessage(new String(response.data));
                            if(json !="" && json != null) {
                                displayMessage(json);
                            }else{
                                displayMessage("Please try again.");
                            }

                        }else{
                            displayMessage("Please try again.");
                        }

                    }catch (Exception e){
                        displayMessage("Something went wrong.");
                    }


                }
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("X-Requested-With", "XMLHttpRequest");
                return headers;
            }
        };

        TutorApplication.getInstance().addToRequestQueue(jsonObjectRequest);
    }

    public void displayMessage(String toastString) {
        try {
            Snackbar snackbar = Snackbar.make(getCurrentFocus(), toastString, Snackbar.LENGTH_SHORT);
            View snackBarView = snackbar.getView();
            snackBarView.setBackgroundColor(ContextCompat.getColor(context, R.color.black));
            TextView textView = (TextView) snackBarView.findViewById(R.id.snackbar_text);
            textView.setTextColor(ContextCompat.getColor(context, R.color.white));
            snackbar.show();
        } catch (Exception e) {
            try {
                Toast.makeText(context, "" + toastString, Toast.LENGTH_SHORT).show();
            } catch (Exception ee) {
                ee.printStackTrace();
            }
        }
    }
}
