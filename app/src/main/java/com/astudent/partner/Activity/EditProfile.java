package com.astudent.partner.Activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;
import com.astudent.partner.Constant.URLHelper;
import com.astudent.partner.Helper.AppHelper;
import com.astudent.partner.Helper.ConnectionHelper;
import com.astudent.partner.Helper.CustomDialog;
import com.astudent.partner.Helper.SharedHelper;
import com.astudent.partner.Helper.VolleyMultipartRequest;
import com.astudent.partner.R;
import com.astudent.partner.TutorApplication;
import com.astudent.partner.Utils.Utilities;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EditProfile extends AppCompatActivity {

    private static final int SELECT_PHOTO = 100;
    public Context context = EditProfile.this;
    public Activity activity = EditProfile.this;
    String TAG = "EditActivity";
    CustomDialog customDialog;
    ConnectionHelper helper;
    Boolean isInternet;
    TextView changePasswordTxt, lblSave, email;
    EditText first_name, last_name, mobile_no, desc;
    ImageView profile_Image;
    Boolean isImageChanged = false;

    ImageView backImg;
    Uri filePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
            getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
        }
        setContentView(R.layout.activity_edit_profile);
        findViewByIdandInitialization();
        enableDisableETxt(false);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        backImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GoToMainActivity();
            }
        });

        lblSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (lblSave.getText().toString().equalsIgnoreCase(getString(R.string.edit))) {
                    lblSave.setText(getString(R.string.save));
                    enableDisableETxt(true);
                } else {

                    Pattern ps = Pattern.compile(".*[0-9].*");
                    Matcher firstName = ps.matcher(first_name.getText().toString());
                    Matcher lastName = ps.matcher(last_name.getText().toString());

                    if (email.getText().toString().equals("") || email.getText().toString().length() == 0) {
                        displayMessage(getString(R.string.email_validation));
                    } else if (mobile_no.getText().toString().equals("") || mobile_no.getText().toString().length() == 0) {
                        displayMessage(getString(R.string.mobile_number_empty));
                    } else if (first_name.getText().toString().equals("") || first_name.getText().toString().length() == 0) {
                        displayMessage(getString(R.string.first_name_empty));
                    } else if (last_name.getText().toString().equals("") || last_name.getText().toString().length() == 0) {
                        displayMessage(getString(R.string.last_name_empty));
                    } else if (firstName.matches()) {
                        displayMessage(getString(R.string.first_name_no_number));
                    } else if (lastName.matches()) {
                        displayMessage(getString(R.string.last_name_no_number));
                    }else if(mobile_no.getText().toString().length() < 10 || mobile_no.getText().toString().length() > 20){
                        displayMessage(getString(R.string.mobile_no_validation));
                    } else {
                        if (isInternet) {
                            updateProfile();
                        } else {
                            displayMessage(getString(R.string.something_went_wrong_net));
                        }
                    }
                }
            }
        });


        changePasswordTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showChangePasswordDialog();
            }
        });

        profile_Image.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View view) {
                Log.e(TAG, "onClick: ProfilePic");
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if(checkStoragePermission()) {
                                requestPermissions(new String[]{Manifest.permission.CAMERA,
                                        Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
                            }else{
                                goToImageIntent();

                            }
                        }else{
                            goToImageIntent();

                        }

            }
        });

    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private boolean checkStoragePermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100)
//            for (int grantResult : grantResults)
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[1] == PackageManager.PERMISSION_GRANTED)
                    goToImageIntent();
    }

    public void goToImageIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PHOTO);
    }

    private void showChangePasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(EditProfile.this);
        LayoutInflater inflater = EditProfile.this.getLayoutInflater();
        View view = inflater.inflate(R.layout.activity_change_password, null);

        builder.setView(view);

        final EditText currentPassword = (EditText) view.findViewById(R.id.current_password);
        final EditText newPassword = (EditText) view.findViewById(R.id.new_password);
        final EditText confirmPassword = (EditText) view.findViewById(R.id.confirm_password);

        Button cancelBtn = (Button) view.findViewById(R.id.btnCancel);
        Button submit = (Button) view.findViewById(R.id.changePasswordBtn);

        final AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentPassword.getText().toString().length() == 0) {
                    displayMessage(getString(R.string.please_enter_current_pass));
                } else if (newPassword.getText().toString().length() == 0) {
                    displayMessage(getString(R.string.please_enter_new_pass));
                }  else if(!Utilities.isValidPassword(newPassword.getText().toString())){
                    displayMessage(getString(R.string.password_validation2));
                } else if (confirmPassword.getText().toString().length() == 0) {
                    displayMessage(getString(R.string.please_enter_confirm_pass));
                } else if (newPassword.equals(currentPassword)) {
                    displayMessage(getString(R.string.current_and_new_passwords));
                } else if (!newPassword.getText().toString().equals(confirmPassword.getText().toString())) {
                    displayMessage(getString(R.string.different_passwords));
                } else {
                    dialog.dismiss();
                    changePassword(currentPassword.getText().toString(), newPassword.getText().toString(), confirmPassword.getText().toString());
                }
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void changePassword(String current_pass, String new_pass, String confirm_new_pass) {

        customDialog = new CustomDialog(context);
        customDialog.setCancelable(false);
        customDialog.show();
        JSONObject object = new JSONObject();
        try {
            object.put("password", new_pass);
            object.put("password_confirmation", confirm_new_pass);
            object.put("password_old", current_pass);
            Log.e("ChangePasswordAPI", "" + object);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLHelper.CHANGE_PASSWORD_API, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                customDialog.dismiss();
                Log.v("SignInResponse", response.toString());
//                displayMessage(response.optString("message"));

                displayMessage(context.getResources().getString(R.string.password_Change_done));

                SharedHelper.putKey(context, "current_status", "");
                SharedHelper.putKey(context, "loggedIn", getString(R.string.False));
                SharedHelper.putKey(context,"login_by","");
                Intent mainIntent = new Intent(context, ActivityPassword.class);
                mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(mainIntent);
                activity.finish();

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                customDialog.dismiss();
                String json = null;
                String Message;
                NetworkResponse response = error.networkResponse;
                Log.e("MyTest", "" + error);
                Log.e("MyTestError", "" + error.networkResponse);
                //Log.e("MyTestError1", "" + response.statusCode);
                if (response != null && response.data != null) {
                    try {
                        JSONObject errorObj = new JSONObject(new String(response.data));
                        Log.e("ErrorChangePasswordAPI", "" + errorObj.toString());

                        if (response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500) {
                            try {
                                displayMessage(errorObj.optString("error"));
                            } catch (Exception e) {
                                displayMessage(getString(R.string.something_went_wrong));
                            }
                        } else if (response.statusCode == 401) {
                            GoToBeginActivity();
                        } else if (response.statusCode == 422) {
                            json = TutorApplication.trimMessage(new String(response.data));
                            if (json != "" && json != null) {
                                displayMessage(json);
                            } else {
                                displayMessage(getString(R.string.please_try_again));
                            }
                        } else {
                            displayMessage(getString(R.string.please_try_again));
                        }

                    } catch (Exception e) {
                        displayMessage(getString(R.string.something_went_wrong));
                    }


                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("X-Requested-With", "XMLHttpRequest");
                headers.put("Authorization", "Bearer " + SharedHelper.getKey(context, "access_token"));
                return headers;
            }
        };

        TutorApplication.getInstance().addToRequestQueue(jsonObjectRequest);
    }

    private void enableDisableETxt(boolean isEnabled) {
        profile_Image.setEnabled(isEnabled);
        first_name.setEnabled(isEnabled);
        last_name.setEnabled(isEnabled);
        mobile_no.setEnabled(isEnabled);
        desc.setEnabled(isEnabled);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_PHOTO && resultCode == activity.RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            Log.e(TAG, "onActivityResult: img url" + filePath);
            try {
                Log.e(TAG, "onActivityResult: ");
                isImageChanged = true;
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                profile_Image.setImageBitmap(bitmap);
            }catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public void updateProfile() {
        if (isImageChanged) {
            updateProfileWithImage();
        } else {
            updateProfileWithoutImage();
        }
    }

    public void updateProfileWithImage(){
        isImageChanged = false;
        customDialog = new CustomDialog(context);
        customDialog.setCancelable(false);
        customDialog.show();
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST,
                URLHelper.PROVIDER_PROFILE_UPDATE, new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                customDialog.dismiss();

                String res = new String(response.data);
                try {
                    JSONObject jsonObject = new JSONObject(res);
                    SharedHelper.putKey(context, "id", jsonObject.optString("id"));
                    SharedHelper.putKey(context, "first_name", jsonObject.optString("first_name"));
                    SharedHelper.putKey(context, "last_name", jsonObject.optString("last_name"));
                    SharedHelper.putKey(context, "email", jsonObject.optString("email"));
                    if (jsonObject.optString("avatar").equals("") || jsonObject.optString("avatar") == null) {
                        SharedHelper.putKey(context, "picture", "");
                    } else {
                        SharedHelper.putKey(context, "picture", AppHelper.getImageUrl(jsonObject.optString("avatar")));
                    }

                    SharedHelper.putKey(context, "gender", jsonObject.optString("gender"));
                    SharedHelper.putKey(context, "mobile", jsonObject.optString("mobile"));
                    SharedHelper.putKey(context, "wallet_balance", jsonObject.optString("wallet_balance"));
                    SharedHelper.putKey(context, "payment_mode", jsonObject.optString("payment_mode"));
                    SharedHelper.putKey(context, "payment_mode", jsonObject.optString("payment_mode"));
                    SharedHelper.putKey(context, "description", jsonObject.optString("description"));
                    displayMessage(getString(R.string.update_success));
                    lblSave.setText(getString(R.string.edit));
                    enableDisableETxt(false);
                } catch (JSONException e) {
                    e.printStackTrace();
                    displayMessage(getString(R.string.something_went_wrong));
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                customDialog.dismiss();
                Log.e(TAG, "" + error);
                if (error.networkResponse == null) {
                    if (error.getClass().equals(TimeoutError.class)) {
                        // Show timeout error message
                        updateProfileWithoutImage();
                    }else{
                        displayMessage(getString(R.string.something_went_wrong));
                    }
                }else{
                    displayMessage(getString(R.string.something_went_wrong));
                }
            }
        }) {
            @Override
            public Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("first_name", first_name.getText().toString());
                params.put("last_name", last_name.getText().toString());
                params.put("email", email.getText().toString());
                params.put("mobile", mobile_no.getText().toString());
                params.put("description", desc.getText().toString());
                if (filePath != null) {
                    params.put("avatar", URLHelper.BASE_URL + "/" + filePath);
                } else {
                    params.put("avatar", "");
                }
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("X-Requested-With", "XMLHttpRequest");
                headers.put("Authorization", "Bearer " + SharedHelper.getKey(context, "access_token"));
                return headers;
            }

            @Override
            protected Map<String, VolleyMultipartRequest.DataPart> getByteData() throws AuthFailureError {
                Map<String, VolleyMultipartRequest.DataPart> params = new HashMap<>();
                params.put("avatar", new VolleyMultipartRequest.DataPart("userImage.jpg", AppHelper.getFileDataFromDrawable(profile_Image.getDrawable()), "image/jpeg"));
                return params;
            }
        };
        TutorApplication.getInstance().addToRequestQueue(volleyMultipartRequest);
    }


    public void updateProfileWithoutImage(){
        customDialog = new CustomDialog(context);
        customDialog.setCancelable(false);
        customDialog.show();
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, URLHelper.PROVIDER_PROFILE_UPDATE, new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                customDialog.dismiss();
                Log.e(TAG, "onResponse: Profile Update" + response.data);
                Log.e(TAG, "onResponse: Profile Update" + response.toString());
                String res = new String(response.data);
                try {
                    JSONObject jsonObject = new JSONObject(res);
                    SharedHelper.putKey(context, "id", jsonObject.optString("id"));
                    SharedHelper.putKey(context, "first_name", jsonObject.optString("first_name"));
                    SharedHelper.putKey(context, "last_name", jsonObject.optString("last_name"));
                    SharedHelper.putKey(context, "email", jsonObject.optString("email"));
                    if (jsonObject.optString("avatar").equals("") || jsonObject.optString("avatar") == null) {
                        SharedHelper.putKey(context, "picture", "");
                    } else {
                        SharedHelper.putKey(context, "picture", AppHelper.getImageUrl(jsonObject.optString("avatar")));
                    }
                    SharedHelper.putKey(context, "gender", jsonObject.optString("gender"));
                    SharedHelper.putKey(context, "mobile", jsonObject.optString("mobile"));
                    SharedHelper.putKey(context, "wallet_balance", jsonObject.optString("wallet_balance"));
                    SharedHelper.putKey(context, "payment_mode", jsonObject.optString("payment_mode"));
                    SharedHelper.putKey(context, "description", jsonObject.optString("description"));

                    displayMessage(getString(R.string.update_success));
                    lblSave.setText(getString(R.string.edit));
                    enableDisableETxt(false);

                } catch (JSONException e) {
                    e.printStackTrace();
                    displayMessage(getString(R.string.something_went_wrong));
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                customDialog.dismiss();
                Log.e(TAG, "" + error);
                if (error.networkResponse == null) {
                    if (error.getClass().equals(TimeoutError.class)) {
                        // Show timeout error message
                        updateProfileWithoutImage();
                    }else{
                        displayMessage(getString(R.string.something_went_wrong));
                    }
                }else{
                    displayMessage(getString(R.string.something_went_wrong));
                }
            }
        }) {
            @Override
            public Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("first_name", first_name.getText().toString());
                params.put("last_name", last_name.getText().toString());
                params.put("email", email.getText().toString());
                params.put("mobile", mobile_no.getText().toString());
                params.put("description", desc.getText().toString());
                if (filePath != null) {
                    params.put("avatar", URLHelper.BASE_URL + "/" + filePath);
                } else {
                    params.put("avatar", "");
                }
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("X-Requested-With", "XMLHttpRequest");
                headers.put("Authorization", "" + SharedHelper.getKey(context, "token_type") + " " + SharedHelper.getKey(context, "access_token"));
                return headers;
            }
        };
        TutorApplication.getInstance().addToRequestQueue(volleyMultipartRequest);
    }

    public void findViewByIdandInitialization() {
        email = (TextView) findViewById(R.id.email);
        first_name = (EditText) findViewById(R.id.first_name);
        last_name = (EditText) findViewById(R.id.last_name);
        mobile_no = (EditText) findViewById(R.id.mobile_no);
        desc = (EditText) findViewById(R.id.desc);
        changePasswordTxt = (TextView) findViewById(R.id.changePasswordTxt);
        profile_Image = (ImageView) findViewById(R.id.img_profile);
        helper = new ConnectionHelper(context);
        isInternet = helper.isConnectingToInternet();

        lblSave = (TextView) findViewById(R.id.lblSave);
        backImg = (ImageView) findViewById(R.id.backArrow);

        //Assign current profile values to the edittext
        //Glide.with(activity).load(SharedHelper.getKey(context, "picture")).placeholder(R.drawable.loading).error(R.drawable.ic_dummy_user).into(profile_Image);
        if (!SharedHelper.getKey(context, "picture").equalsIgnoreCase("")) {
            Log.e(TAG, "findViewByIdandInitialization: " + SharedHelper.getKey(context, "picture"));
            Picasso.with(context).load(SharedHelper.getKey(context, "picture")).memoryPolicy(MemoryPolicy.NO_CACHE)
                    .placeholder(R.drawable.ic_dummy_user).error(R.drawable.ic_dummy_user).into(profile_Image);
        }
        email.setText(SharedHelper.getKey(context, "email"));
        first_name.setText(SharedHelper.getKey(context, "first_name"));
        last_name.setText(SharedHelper.getKey(context, "last_name"));
        if (SharedHelper.getKey(context, "mobile") != null
                && !SharedHelper.getKey(context, "mobile").equals("null")) {
            mobile_no.setText(SharedHelper.getKey(context, "mobile"));
        }
        String description = SharedHelper.getKey(context, "description");
        if (description != null && !description.equalsIgnoreCase("null") && description.length() > 0) {
            desc.setText(description);
        }

        if (SharedHelper.getKey(context,"login_by").equals("facebook")) {
            changePasswordTxt.setVisibility(View.GONE);
        }else {
            changePasswordTxt.setVisibility(View.VISIBLE);
        }

    }


    public void GoToMainActivity() {
        Intent mainIntent = new Intent(activity, Home.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(mainIntent);
        activity.finish();
    }

    public void displayMessage(String toastString) {
//        try {
//            Snackbar snackbar = Snackbar.make(getCurrentFocus(), toastString, Snackbar.LENGTH_SHORT);
//            View snackBarView = snackbar.getView();
//            snackBarView.setBackgroundColor(ContextCompat.getColor(context, R.color.black));
//            TextView textView = (TextView) snackBarView.findViewById(R.id.snackbar_text);
//            textView.setTextColor(ContextCompat.getColor(context, R.color.white));
//            snackbar.show();
//        } catch (Exception e) {
//            try {
                Toast.makeText(context, "" + toastString, Toast.LENGTH_SHORT).show();
//            } catch (Exception ee) {
//                ee.printStackTrace();
//            }
//        }
    }
    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    public void onBackPressed() {
        GoToMainActivity();
    }

    public void GoToBeginActivity(){
        SharedHelper.putKey(activity, "loggedIn", getString(R.string.False));
        Intent mainIntent = new Intent(activity, ActivityPassword.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(mainIntent);
        activity.finish();
    }

}
