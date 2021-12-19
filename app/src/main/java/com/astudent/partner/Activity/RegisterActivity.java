package com.astudent.partner.Activity;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.os.StrictMode;
import android.provider.MediaStore;

import androidx.annotation.DimenRes;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.android.volley.toolbox.StringRequest;
import com.astudent.partner.Utils.CallBack;
import com.astudent.partner.Utils.WebApiHelper;
import com.google.android.material.snackbar.Snackbar;

import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.firebase.iid.FirebaseInstanceId;
import com.astudent.partner.Adapter.RegisterDocAdapter;
import com.astudent.partner.Bean.Document;
import com.astudent.partner.Constant.URLHelper;
import com.astudent.partner.CountryPicker.Country;
import com.astudent.partner.CountryPicker.CountryPicker;
import com.astudent.partner.CountryPicker.CountryPickerListener;
import com.astudent.partner.Helper.AppHelper;
import com.astudent.partner.Helper.ConnectionHelper;
import com.astudent.partner.Helper.CustomDialog;
import com.astudent.partner.Helper.SharedHelper;
import com.astudent.partner.Helper.VolleyMultipartRequest;
import com.astudent.partner.R;
import com.astudent.partner.Utils.KeyHelper;
import com.astudent.partner.Utils.Utilities;
import com.astudent.partner.TutorApplication;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;

import net.alhazmy13.mediapicker.Video.VideoPicker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import cz.msebera.android.httpclient.entity.mime.content.FileBody;

import static com.astudent.partner.Helper.VolleyMultipartRequest.lineEnd;
import static com.astudent.partner.Helper.VolleyMultipartRequest.twoHyphens;
import static com.astudent.partner.TutorApplication.trimMessage;

public class RegisterActivity extends AppCompatActivity implements RegisterDocAdapter.ServiceClickListener {

    public Context context = RegisterActivity.this;
    public Activity activity = RegisterActivity.this;
    String TAG = "RegisterActivity";
    String device_token, device_UDID;
    ImageView backArrow;
    EditText email, first_name, last_name, mobile_no, password;
    CustomDialog customDialog;
    ConnectionHelper helper;
    Boolean isInternet;
    Utilities utils = new Utilities();

    Button signUpBtn;
    LinearLayout signInLayout;
    LinearLayout uploadvideo_lay;

    private CountryPicker mCountryPicker;
    ImageView countryImage;
    String country_code;
    TextView countryNumber, tv_editvideo;

    RecyclerView recyclerView;
    ImageView uploadImg;
    Document updatedDocument;
    int position = -1;
    ArrayList<Document> documentArrayList;
    ArrayList<String> uriImageList = new ArrayList<>();
    RegisterDocAdapter documentAdapter;
    private static final int SELECT_PHOTO = 100;
    Boolean isPermissionGivenAlready = false;
    public static final String TAGG = "DocumentActivity";
    public static int deviceHeight;
    public static int deviceWidth;


    private static final int SELECT_VIDEO = 1;

    private String selectedVideoPath;
    private VideoView videoview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
            getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
        }
        setContentView(R.layout.activity_register);
        findViewById();
        GetToken();
        setupRecyclerView();
        getDocList();

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        deviceHeight = displayMetrics.heightPixels;
        deviceWidth = displayMetrics.widthPixels;
        SharedHelper.putKey(getApplicationContext(), Utilities.skip_login, "");

        if (Build.VERSION.SDK_INT >= 16) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (videoview != null && videoview.isPlaying()) {
                    videoview.pause();
                }

                if (email.getText().toString().equals("") || email.getText().toString().equalsIgnoreCase(getString(R.string.sample_mail_id))) {
                    displayMessage(getString(R.string.email_validation));
                } else if (first_name.getText().toString().equals("") || first_name.getText().toString().equalsIgnoreCase(getString(R.string.first_name))) {
                    displayMessage(getString(R.string.first_name_empty));
                } else if (mobile_no.getText().toString().equals("") || mobile_no.getText().toString().equalsIgnoreCase(getString(R.string.mobile_no))) {
                    displayMessage(getString(R.string.mobile_number_empty));
                } else if (mobile_no.getText().toString().length() > 20) {
                    displayMessage(getString(R.string.mobile_no_validation));
                } else if (last_name.getText().toString().equals("") || last_name.getText().toString().equalsIgnoreCase(getString(R.string.last_name))) {
                    displayMessage(getString(R.string.last_name_empty));
                } else if (password.getText().toString().equals("") || password.getText().toString().equalsIgnoreCase(getString(R.string.password_txt))) {
                    displayMessage(getString(R.string.password_validation));
                } else if (password.getText().toString().length() < 6) {
                    displayMessage(getString(R.string.passwd_length));
                } /*else if (!Utilities.isValidPassword(password.getText().toString().trim())) {
                    displayMessage(getString(R.string.password_validation2));
                } */ else {
                    if (isInternet) {
//                        registerAPI();
                        signupFinal();
                    } else {
                        displayMessage(getString(R.string.something_went_wrong_net));
                    }
                }
            }
        });

        signInLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        uploadvideo_lay.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                SelectProfileVideo();
            }
        });
        videoview.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                SelectProfileVideo();

            }
        });

        tv_editvideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SelectProfileVideo();

            }
        });


    }

    private void SelectProfileVideo() {

        new VideoPicker.Builder(RegisterActivity.this)
                .mode(VideoPicker.Mode.CAMERA_AND_GALLERY)
                .directory(VideoPicker.Directory.DEFAULT)
                .extension(VideoPicker.Extension.MP4)
                .enableDebuggingMode(true)
                .build();

//        Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
//        startActivityForResult(i, SELECT_VIDEO);
    }


    private void signupFinal() {
        if (helper.isConnectingToInternet()) {
            /*Log.i("signupFinal", "called");
            final CustomDialog customDialog = new CustomDialog(context);
//            customDialog = new CustomDialog(RegisterActivity.this);
            customDialog.setCancelable(false);
            customDialog.show();
            JSONObject object = new JSONObject();
            try {

                object.put("device_type", "android");
                object.put("device_id", device_UDID);
                object.put("device_token", "" + device_token);
                object.put("login_by", "manual");
                object.put("first_name", first_name.getText().toString());
                object.put("role_id", 1);
                object.put("last_name", last_name.getText().toString());
                object.put("email", email.getText().toString());
                object.put("password", password.getText().toString());
                object.put("password_confirmation", password.getText().toString());
                object.put("mobile", countryNumber.getText().toString() + mobile_no.getText().toString());
                object.put("social_unique_id", "");
                Log.i("photos", new Gson().toJson(documentArrayList));

                JSONArray mJSONArray = new JSONArray();
                StringBuilder photos = new StringBuilder();
//                photos.append("[");

                for (int i = 0; i < documentArrayList.size(); i++) {
//                    photos.append(getLocalBitmapUri(documentArrayList.get(i).getBitmap()));
//                    uriImageList.add(getLocalBitmapUri(documentArrayList.get(i).getBitmap()).getAbsolutePath());
                    if (i < documentArrayList.size() - 1)
                        photos.append(",");
                }
//                photos.append("]");

                object.put("photos", mJSONArray);

              *//*  if(selectedVideoPath != null){
                    final File myVideo = new File(selectedVideoPath);
                    object.put("video_data", "["+myVideo+"]") ;
                }*//*

//                try {
//                    object.put("video_data", "{"+
//            "Content-Disposition: form-data,name=abc" +
////                            "\"" + myVideo.getName()+ "\"" +
//            ",filename=\"" +myVideo.getName() + "\"" +
//            ",Content-Type:" + "\"video/mp4\"" +
//                ",Content:"+ Arrays.toString(loadFile(myVideo))
//            + "}");
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }

//                try {
//                    object.put("video_data", "{name:" + myVideo + ",type: video/mp4, "+Arrays.toString(loadFile(myVideo))+"}");
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                object.put("video_data", "{"+
//                        "Content-Disposition: form-data; name=abc" +
//                            "\"" + myVideo.getName()+ "\"" +
//                        "; filename=\"" +myVideo.getName() + "\"" +
//                        ";Content-Type:mp4"
//                            ","+ Arrays.toString(loadFile(myVideo))
//                        + "}");

                        utils.print("InputToRegisterAPI", "" + object);

            } catch (
                    JSONException e) {
                e.printStackTrace();
            }

            StringRequest jsonObjectRequest = new StringRequest(Request.Method.POST, URLHelper.REGISTER, new Response.Listener<String>() {
                @Override
                public void onResponse(String  response) {
                    customDialog.dismiss();
                    utils.print("SignInResponse", response.toString());
                    SharedHelper.putKey(RegisterActivity.this, "email", email.getText().toString());
                    SharedHelper.putKey(RegisterActivity.this, "password", password.getText().toString());
                    signIn();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    customDialog.dismiss();
                    String json = null;
                    String Message;
                    NetworkResponse response = error.networkResponse;

                    if (error instanceof TimeoutError) {
                        registerAPI();
                    }

                    if (response != null && response.data != null) {
                        utils.print("MyTest", "" + error);
                        utils.print("MyTestError", "" + error.networkResponse);
                        utils.print("MyTestError1", "" + response.statusCode);
                        try {
                            JSONObject errorObj = new JSONObject(new String(response.data));

                            if (response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500) {
                                try {
                                    displayMessage(errorObj.optString("message"));
                                } catch (Exception e) {
                                    displayMessage(getString(R.string.something_went_wrong));
                                }
                            } else if (response.statusCode == 401) {
                                try {
                                    if (errorObj.optString("message").equalsIgnoreCase("invalid_token")) {
                                        //Call Refresh token
                                    } else {
                                        displayMessage(errorObj.optString("message"));
                                    }
                                } catch (Exception e) {
                                    displayMessage(getString(R.string.something_went_wrong));
                                }

                            } else if (response.statusCode == 422) {

                                json = trimMessage(new String(response.data));
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
               *//* @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<String, String>();
//                    headers.put("X-Requested-With", "XMLHttpRequest");
                    return headers;
                }*//*

                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
           ArrayList<String> mArrayList = new ArrayList<>();
                    for (int i = 0; i < documentArrayList.size(); i++) {
                        mArrayList.add(getLocalBitmapUri(documentArrayList.get(i).getBitmap()).getAbsolutePath());

                    }
                    Map<String,String> params = new HashMap<String, String>();
                    params.put("device_type","android");
                    params.put("device_id",device_UDID);
                    params.put("device_token","" + device_token);
                    params.put("login_by","manual");
                    params.put("first_name",first_name.getText().toString());
                    params.put("role_id","1");
                    params.put("last_name",last_name.getText().toString());
                    params.put("email",email.getText().toString());
                    params.put("password", password.getText().toString());
                    params.put("password_confirmation", password.getText().toString());
                    params.put("mobile", countryNumber.getText().toString() + mobile_no.getText().toString());
                    params.put("social_unique_id", "");
                    params.put("photos", new Gson().toJson(mArrayList));
                    return params;
                }
            };

            TutorApplication.getInstance().addToRequestQueue(jsonObjectRequest);
*/
            customDialog = new CustomDialog(RegisterActivity.this);
            customDialog.setCancelable(false);
            customDialog.show();
            VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, URLHelper.REGISTER, new Response.Listener<NetworkResponse>() {
                @Override
                public void onResponse(NetworkResponse response) {
                    try {
                        JSONObject mJSONObject=new JSONObject(new String(response.data));


                        if(mJSONObject.opt("error") != null){
                            displayMessage(mJSONObject.optString("error"));
                            return;
                        }

                        customDialog.dismiss();
                        utils.print("SignInResponse", response.toString());
                        SharedHelper.putKey(RegisterActivity.this, "email", email.getText().toString());
                        SharedHelper.putKey(RegisterActivity.this, "password", password.getText().toString());
                        signIn();
                    }catch(Exception exception){
                        displayMessage(exception.getMessage());
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if ((customDialog != null) && customDialog.isShowing())
                        customDialog.dismiss();
                    String json = null;
                    String Message;
                    NetworkResponse response = error.networkResponse;
                    if (response != null && response.data != null) {
                        Log.i("statusCode", response.statusCode + " <");
                        try {
                            JSONObject errorObj = new JSONObject(new String(response.data));
                            if (response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500) {
                                try {
                                    displayMessage(errorObj.optString("message"));
                                } catch (Exception e) {
                                    displayMessage(getString(R.string.something_went_wrong));
                                }
                            } else if (response.statusCode == 401) {
//                                GoToBeginActivity();
                                try {
                                    if (errorObj.optString("message").equalsIgnoreCase("invalid_token")) {
                                        //Call Refresh token
                                    } else {
                                        displayMessage(errorObj.optString("message"));
                                    }
                                } catch (Exception e) {
                                    displayMessage(getString(R.string.something_went_wrong));
                                }
                            } else if (response.statusCode == 422) {

                                json = trimMessage(new String(response.data));
                                if (json != "" && json != null) {
                                    displayMessage(json);
                                } else {
                                    displayMessage(getString(R.string.please_try_again));
                                }

                            } else if (response.statusCode == 503) {
                                displayMessage(getString(R.string.server_down));
                            } else {
                                displayMessage(getString(R.string.please_try_again));
                            }

                        } catch (Exception e) {
                            displayMessage(getString(R.string.something_went_wrong));
                        }

                    } else {
                        if (error instanceof NoConnectionError) {
                            displayMessage(getString(R.string.oops_connect_your_internet));
                        } else if (error instanceof NetworkError) {
                            displayMessage(getString(R.string.oops_connect_your_internet));
                        } else if (error instanceof TimeoutError) {
                            signupFinal();
                        }
                    }
                }
            }) {
                @Override
                public Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> object = new HashMap<>();
                    Log.e("getParams", "..");
                    try {
                        object.put("device_type", "android");
                        object.put("device_id", device_UDID);
                        object.put("device_token", "" + device_token);
                        object.put("login_by", "manual");
                        object.put("first_name", first_name.getText().toString());
                        object.put("role_id", "1");
                        object.put("last_name", last_name.getText().toString());
                        object.put("email", email.getText().toString());
                        object.put("password", password.getText().toString());
                        object.put("password_confirmation", password.getText().toString());
                        object.put("mobile", countryNumber.getText().toString() + mobile_no.getText().toString());
                        object.put("social_unique_id", "");
//                        String car_type = service_type.getAdapter().getItem(service_type.getSelectedItemPosition()).toString();
//                        Log.e("car_type_selected", car_type);

                        /*if (!car_type.equalsIgnoreCase("") && car_type != null) {
                            for (ServiceTypes type : serviceTypeArrayList) {
                                if (type.getName().equals(car_type)) {
                                    object.put("service_type", String.valueOf(type.getId()));
                                }
                            }
                        }*/

                        Log.e(TAG, "signupFinal: " + documentAdapter.getServiceListModel().toString());

                        /*for (Document document : documentAdapter.getServiceListModel()) {
                            if (document.getBitmap() != null) {
                                String key = "expires_at[" + document.getId() + "]";
                                object.put(key, document.getExpdate());
                            }
                        }*/

                        utils.print("InputToRegisterAPI", "" + object);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return object;
                }

                @Override
                protected Map<String, VolleyMultipartRequest.DataPart> getByteData() throws AuthFailureError {
                    Map<String, VolleyMultipartRequest.DataPart> params = new HashMap<>();
                    Log.i("getByteData", "..");

                    for(int i =0 ; i < documentAdapter.getServiceListModel().size() ; i++) {
                        Document mDocument = documentAdapter.getServiceListModel().get(i);
                        if (i == 0) {
                            if (mDocument.getBitmap() != null) {
                                String photo = "photos";
                                params.put(photo, new VolleyMultipartRequest.DataPart("identity_card" + System.currentTimeMillis() + ".jpg", AppHelper.getFileDataFromDrawable(mDocument.getBitmap()), "image/jpeg"));
                            }
                        } else {
                            if (mDocument.getBitmap() != null) {
                                String photo = "criminal_certification_image";
                                params.put(photo, new VolleyMultipartRequest.DataPart("criminal_certification_id" + System.currentTimeMillis() + ".jpg", AppHelper.getFileDataFromDrawable(mDocument.getBitmap()), "image/jpeg"));
                            }
                        }
                    }

                   /* if (selectedVideoPath != null) {
                        final File myVideo = new File(selectedVideoPath);
                        try {
//                        params.put("video_data", new DataPart(myVideo.getName() + ".mp4", loadFile(myVideo)));
                            params.put("video_data", new DataPart(myVideo.getName(), loadFile(myVideo)));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }*/
                    return params;
                }
            };
/*

            volleyMultipartRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
*/

            TutorApplication.getInstance().addToRequestQueue(volleyMultipartRequest);
//            TutorApplication.getInstance().addToRequestQueue(jsonObjectRequest);
        } else {
            displayMessage(getString(R.string.something_went_wrong_net));
        }

    }

//    private void signupFinal() {
//        if (helper.isConnectingToInternet()) {
//            try {
//                JSONObject params = new JSONObject();
//                params.put("device_type", "android");
//                params.put("device_id", device_UDID);
//                params.put("device_token", device_token);
//                params.put("login_by", "manual");
//                params.put("first_name", first_name.getText().toString());
//                params.put("last_name", last_name.getText().toString());
//                params.put("email", email.getText().toString());
//                params.put("password", password.getText().toString());
//                params.put("password_confirmation", password.getText().toString());
//                params.put("mobile", countryNumber.getText().toString() + mobile_no.getText().toString());
//
//                for (int i = 0; i < documentArrayList.size(); i++) {
//                    params.put("photos[" + i + "]", getLocalBitmapUri(documentArrayList.get(i).getBitmap()));
//                }
//                final File myVideo = new File(selectedVideoPath);
//                params.put("video_data", myVideo);
//
//                Log.e("srkk", "" + params + "\n");
//                WebApiHelper.callPostApi(activity, URLHelper.REGISTER, params, true, new CallBack() {
//                    @Override
//                    public void onResponse(boolean is_success, String response) {
//                        if (is_success) {
//                            try {
//                                SharedHelper.putKey(RegisterActivity.this, "email", email.getText().toString());
//                                SharedHelper.putKey(RegisterActivity.this, "password", password.getText().toString());
//                                signIn();
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    }
//                });
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//        }
//    }

    public File getLocalBitmapUri(Bitmap bmp) {
        File file = null;
        try {
            File directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            if (!directory.isDirectory()) {
                directory.mkdirs();
            }
            file = new File(directory, System.currentTimeMillis() + "_doc.jpg");
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    private void getDocList() {
        if (helper.isConnectingToInternet()) {
            final CustomDialog customDialog = new CustomDialog(activity);
            customDialog.setCancelable(false);
            customDialog.show();

            final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URLHelper.GET_DOC, new JSONObject(), new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject result) {
                    customDialog.dismiss();
                    JSONArray response = result.optJSONArray("document");
                    Log.e(TAG, "onResponse: " + response.toString());
                    if (response.length() > 0) {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject doc = response.optJSONObject(i);
                            Document document = new Document();
                            document.setId(doc.optString("id"));
                            document.setName(doc.optString("name"));
                            document.setType(doc.optString("type"));
                            JSONObject docObj = doc.optJSONObject("document");
                            try {
                                if (docObj != null) {
                                    document.setImg(docObj.optString("url"));
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            documentArrayList.add(document);
                        }
                        if (documentArrayList.size() > 0) {
                            documentAdapter = new RegisterDocAdapter(documentArrayList, context);
                            documentAdapter.setServiceClickListener(RegisterActivity.this);
                            recyclerView.setAdapter(documentAdapter);
                        }
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    customDialog.dismiss();
                    String json = null;
                    String Message;
                    NetworkResponse response = error.networkResponse;
                    utils.print("MyTest", "" + error);
                    utils.print("MyTestError", "" + error.networkResponse);

                    if (response != null && response.data != null) {
                        utils.print("MyTestError1", "" + response.statusCode);
                        try {
                            JSONObject errorObj = new JSONObject(new String(response.data));
                            if (response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500) {
                                displayMessage(getString(R.string.something_went_wrong));
                            } else if (response.statusCode == 401) {
                                displayMessage(getString(R.string.invalid_credentials));
                            } else if (response.statusCode == 422) {
                                json = trimMessage(new String(response.data));
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

                    } else {
                        if (error instanceof NoConnectionError) {
                            displayMessage(getString(R.string.oops_connect_your_internet));
                        } else if (error instanceof NetworkError) {
                            displayMessage(getString(R.string.oops_connect_your_internet));
                        } else if (error instanceof TimeoutError) {
                            getDocList();
                        }
                    }

                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<String, String>();
                    headers.put("X-Requested-With", "XMLHttpRequest");
                    return headers;
                }
            };
            TutorApplication.getInstance().addToRequestQueue(jsonObjectRequest);
            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                    6000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
            );
        } else {
            displayMessage(getString(R.string.something_went_wrong_net));
        }
    }

    private void setupRecyclerView() {
        documentArrayList = new ArrayList<>();
        documentAdapter = new RegisterDocAdapter(documentArrayList, context);
        documentAdapter.setServiceClickListener(this);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 2);
        RegisterActivity.ItemOffsetDecoration itemDecoration = new RegisterActivity.ItemOffsetDecoration(context, R.dimen._5sdp);
        recyclerView.addItemDecoration(itemDecoration);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(documentAdapter);
    }

    public void findViewById() {
        email = (EditText) findViewById(R.id.email);
        first_name = (EditText) findViewById(R.id.first_name);
        last_name = (EditText) findViewById(R.id.last_name);
        mobile_no = (EditText) findViewById(R.id.mobile_no);
        password = (EditText) findViewById(R.id.password);
        signUpBtn = (Button) findViewById(R.id.btnSignUp);
        signInLayout = (LinearLayout) findViewById(R.id.lnrRegister);

        videoview = (VideoView) findViewById(R.id.video_Player);

        uploadvideo_lay = (LinearLayout) findViewById(R.id.uploadvideo_lay);
        helper = new ConnectionHelper(context);
        isInternet = helper.isConnectingToInternet();

        countryNumber = (TextView) findViewById(R.id.country_number);
        countryImage = (ImageView) findViewById(R.id.country_image);
        tv_editvideo = (TextView) findViewById(R.id.tv_editvideo);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        uploadImg = (ImageView) findViewById(R.id.upload_img);

        mCountryPicker = CountryPicker.newInstance("Select Country");
        ArrayList<Country> nc = new ArrayList<>();
        for (Country c : Country.getAllCountries()) {
            nc.add(c);
        }
        Collections.reverse(nc);
        mCountryPicker.setCountriesList(nc);
        setListener();

    }

    private void setListener() {
        mCountryPicker.setListener(new CountryPickerListener() {
            @Override
            public void onSelectCountry(String name, String code, String dialCode,
                                        int flagDrawableResID) {
//                mCountryNameTextView.setText(name);
//                mCountryIsoCodeTextView.setText(code);
                countryNumber.setText(dialCode);

                countryImage.setImageResource(flagDrawableResID);
                mCountryPicker.dismiss();
            }
        });
        countryImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCountryPicker.show(getSupportFragmentManager(), "COUNTRY_PICKER");
            }
        });
        countryNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCountryPicker.show(getSupportFragmentManager(), "COUNTRY_PICKER");
            }
        });
        getUserCountryInfo();
    }

    private void getUserCountryInfo() {
        Locale current = getResources().getConfiguration().locale;
        Country country = Country.getCountryFromSIM(RegisterActivity.this);
        if (country != null) {
            countryImage.setImageResource(country.getFlag());
            countryNumber.setText(country.getDialCode());
            country_code = country.getDialCode();
        } else {
            Toast.makeText(RegisterActivity.this, "Required Sim", Toast.LENGTH_SHORT).show();
        }
    }


    private void registerAPI() {

        customDialog = new CustomDialog(RegisterActivity.this);
        customDialog.setCancelable(false);
        customDialog.show();
        JSONObject object = new JSONObject();
        try {
            object.put("device_type", "android");
            object.put("device_id", device_UDID);
            object.put("device_token", "" + device_token);
            object.put("login_by", "manual");
            object.put("first_name", first_name.getText().toString());
            object.put("last_name", last_name.getText().toString());
            object.put("email", email.getText().toString());
            object.put("password", password.getText().toString());
            object.put("password_confirmation", password.getText().toString());
            object.put("mobile", countryNumber.getText().toString() + mobile_no.getText().toString());
            utils.print("InputToRegisterAPI", "" + object);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLHelper.REGISTER, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                customDialog.dismiss();
                utils.print("SignInResponse", response.toString());
                SharedHelper.putKey(RegisterActivity.this, "email", email.getText().toString());
                SharedHelper.putKey(RegisterActivity.this, "password", password.getText().toString());
                signIn();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                customDialog.dismiss();
                String json = null;
                String Message;
                NetworkResponse response = error.networkResponse;

                if (response != null && response.data != null) {
                    utils.print("MyTest", "" + error);
                    utils.print("MyTestError", "" + error.networkResponse);
                    utils.print("MyTestError1", "" + response.statusCode);
                    try {
                        JSONObject errorObj = new JSONObject(new String(response.data));

                        if (response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500) {
                            try {
                                displayMessage(errorObj.optString("message"));
                            } catch (Exception e) {
                                displayMessage(getString(R.string.something_went_wrong));
                            }
                        } else if (response.statusCode == 401) {
                            try {
                                if (errorObj.optString("message").equalsIgnoreCase("invalid_token")) {
                                    //Call Refresh token
                                } else {
                                    displayMessage(errorObj.optString("message"));
                                }
                            } catch (Exception e) {
                                displayMessage(getString(R.string.something_went_wrong));
                            }

                        } else if (response.statusCode == 422) {

                            json = trimMessage(new String(response.data));
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
                return headers;
            }
        };
        TutorApplication.getInstance().addToRequestQueue(jsonObjectRequest);
    }

    public void signIn() {
        Log.i("signIn", "called");
        if (isInternet) {
            customDialog = new CustomDialog(RegisterActivity.this);
            customDialog.setCancelable(false);
            customDialog.show();
            JSONObject object = new JSONObject();
            try {
                object.put("grant_type", "password");
                object.put("client_id", URLHelper.CLIENT_ID);
                object.put("client_secret", URLHelper.CLIENT_SECRET_KEY);
                object.put("device_type", "android");
                object.put("device_id", device_UDID);
                object.put("device_token", device_token);
                object.put("email", SharedHelper.getKey(RegisterActivity.this, "email"));
                object.put("password", SharedHelper.getKey(RegisterActivity.this, "password"));
                object.put("scope", "");
                utils.print("InputToLoginAPI", "" + object);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLHelper.LOGIN, object, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    customDialog.dismiss();
                    utils.print("SignUpResponse", response.toString());
                    SharedHelper.putKey(context, "settings", "no");
                    SharedHelper.putKey(context, "access_token", response.optString("access_token"));
                    SharedHelper.putKey(context, "refresh_token", response.optString("refresh_token"));
                    SharedHelper.putKey(context, "token_type", response.optString("token_type"));
                    SharedHelper.putKey(context, "status",response.optString("status"));

                    getProfile();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    customDialog.dismiss();
                    String json = null;
                    NetworkResponse response = error.networkResponse;
                    if (response != null && response.data != null) {
                        try {
                            JSONObject errorObj = new JSONObject(new String(response.data));

                            if (response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500) {
                                try {
                                    displayMessage(errorObj.optString("message"));
                                } catch (Exception e) {
                                    displayMessage(getString(R.string.something_went_wrong));
                                }
                            } else if (response.statusCode == 401) {
                                try {
                                    if (errorObj.optString("message").equalsIgnoreCase("invalid_token")) {
                                        //Call Refresh token
                                    } else {
                                        displayMessage(errorObj.optString("message"));
                                    }
                                } catch (Exception e) {
                                    displayMessage(getString(R.string.something_went_wrong));
                                }

                            } else if (response.statusCode == 422) {

                                json = trimMessage(new String(response.data));
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
                    return headers;
                }
            };

            TutorApplication.getInstance().addToRequestQueue(jsonObjectRequest);
        } else {
            displayMessage(getString(R.string.something_went_wrong_net));
        }

    }

    public void getProfile() {
        Log.i("getProfile", "called");
        if (isInternet) {
            customDialog = new CustomDialog(RegisterActivity.this);
            customDialog.setCancelable(false);
            customDialog.show();
            JSONObject object = new JSONObject();
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URLHelper.PROVIDER_PROFILE + "?device_type=android&device_id=" + device_UDID + "&device_token=" + device_token, object, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    customDialog.dismiss();
                    utils.print("GetProfile", response.toString());
                    SharedHelper.putKey(RegisterActivity.this, "id", response.optString("id"));
                    SharedHelper.putKey(RegisterActivity.this, "first_name", response.optString("first_name"));
                    SharedHelper.putKey(RegisterActivity.this, "last_name", response.optString("last_name"));
                    SharedHelper.putKey(RegisterActivity.this, "email", response.optString("email"));
                    SharedHelper.putKey(context, "description", response.optString("description"));
                    SharedHelper.putKey(RegisterActivity.this, "picture", AppHelper.getImageUrl(response.optString("picture")));
                    SharedHelper.putKey(RegisterActivity.this, "gender", response.optString("gender"));
                    SharedHelper.putKey(RegisterActivity.this, "mobile", response.optString("mobile"));
                    SharedHelper.putKey(RegisterActivity.this, "wallet_balance", response.optString("wallet_balance"));
                    SharedHelper.putKey(RegisterActivity.this, "payment_mode", response.optString("payment_mode"));
                    if (!response.optString("currency").equalsIgnoreCase("") || !response.optString("currency").equalsIgnoreCase("null")) {
                        SharedHelper.putKey(context, "currency", response.optString("currency"));
                    } else {
                        SharedHelper.putKey(context, "currency", "$");
                    }
                    SharedHelper.putKey(RegisterActivity.this, "loggedIn", getString(R.string.True));
                    GoToMainActivity();
                    //GoToSettingsStart();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    customDialog.dismiss();
                    String json = null;
                    String Message;
                    NetworkResponse response = error.networkResponse;
                    if (response != null && response.data != null) {
                        try {
                            JSONObject errorObj = new JSONObject(new String(response.data));

                            if (response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500) {
                                try {
                                    displayMessage(errorObj.optString("message"));
                                } catch (Exception e) {
                                    displayMessage(getString(R.string.something_went_wrong));
                                }
                            } else if (response.statusCode == 401) {
                                try {
                                    if (errorObj.optString("message").equalsIgnoreCase("invalid_token")) {
                                        //Call Refresh token
                                    } else {
                                        displayMessage(errorObj.optString("message"));
                                    }
                                } catch (Exception e) {
                                    displayMessage(getString(R.string.something_went_wrong));
                                }

                            } else if (response.statusCode == 422) {

                                json = trimMessage(new String(response.data));
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
                    headers.put("Authorization", "" + "Bearer" + " " + SharedHelper.getKey(RegisterActivity.this, KeyHelper.KEY_ACCESS_TOKEN));
                    return headers;
                }
            };

            TutorApplication.getInstance().addToRequestQueue(jsonObjectRequest);
        } else {
            displayMessage(getString(R.string.something_went_wrong_net));
        }
    }

    public void GetToken() {
        try {
            if (!SharedHelper.getKey(context, "device_token").equals("") && SharedHelper.getKey(context, "device_token") != null) {
                device_token = SharedHelper.getKey(context, "device_token");
                utils.print(TAG, "GCM Registration Token: " + device_token);
            } else {
                device_token = "" + FirebaseInstanceId.getInstance().getToken();
                SharedHelper.putKey(context, "device_token", "" + FirebaseInstanceId.getInstance().getToken());
                utils.print(TAG, "Failed to complete token refresh: " + device_token);
            }
        } catch (Exception e) {
            device_token = "COULD NOT GET FCM TOKEN";
            utils.print(TAG, "Failed to complete token refresh");
        }

        try {
            device_UDID = android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
            utils.print(TAG, "Device UDID:" + device_UDID);
        } catch (Exception e) {
            device_UDID = "COULD NOT GET UDID";
            e.printStackTrace();
            utils.print(TAG, "Failed to complete device UDID");
        }
    }


    public void GoToMainActivity() {
        Intent mainIntent = new Intent(RegisterActivity.this, Home.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(mainIntent);
        RegisterActivity.this.finish();
    }

    public void GoToSettingsStart() {
        Intent mainIntent = new Intent(activity, SettingsStartActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(mainIntent);
        activity.finish();
    }

    public void displayMessage(String toastString) {
        try {
            Snackbar snackbar = Snackbar.make(getCurrentFocus(), toastString, Snackbar.LENGTH_SHORT);
            View snackBarView = snackbar.getView();
            snackBarView.setBackgroundColor(ContextCompat.getColor(context, R.color.black));
            TextView textView = snackBarView.findViewById(R.id.snackbar_text);
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
        if (SharedHelper.getKey(context, "from").equalsIgnoreCase("email")) {
            Intent mainIntent = new Intent(RegisterActivity.this, ActivityEmail.class);
            mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(mainIntent);
            RegisterActivity.this.finish();
        } else {
            Intent mainIntent = new Intent(RegisterActivity.this, ActivityPassword.class);
            mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(mainIntent);
            RegisterActivity.this.finish();
        }
    }

    public void goToImageIntent() {
        isPermissionGivenAlready = true;
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PHOTO);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onActivityResult(
            final int requestCode,
            final int resultCode,
            final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SELECT_PHOTO && resultCode == activity.RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            try {
                //bitmap = MediaStore.Images.Media.getBitmap(activity.getContentResolver(), uri);
                Bitmap resizeImg = getBitmapFromUri(this, uri);
                if (resizeImg != null && uri != null && AppHelper.getPath(this, uri) != null) {
                    Bitmap reRotateImg = AppHelper.modifyOrientation(resizeImg, AppHelper.getPath(this, uri));
                    uploadImg.setImageBitmap(reRotateImg);
                    updatedDocument.setBitmap(reRotateImg);

                    /*imageUpdateListener = (AdapterImageUpdateListener) documentAdapter;
                    imageUpdateListener.onImageSelectedUpdate(reRotateImg, position);*/

                    documentAdapter.setList(documentArrayList);
                    documentAdapter.notifyDataSetChanged();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (requestCode == VideoPicker.VIDEO_PICKER_REQUEST_CODE && resultCode == RESULT_OK) {
            List<String> mPaths = data.getStringArrayListExtra(VideoPicker.EXTRA_VIDEO_PATH);
            selectedVideoPath = mPaths.get(0);
            if (selectedVideoPath == null) {
                Log.e("selected_video_path =", "null!");
                videoview.setVisibility(View.GONE);
                uploadvideo_lay.setVisibility(View.VISIBLE);
                tv_editvideo.setVisibility(View.GONE);
                finish();
            } else {
                Log.e("selected video path =", " " + selectedVideoPath);
                videoview.setVisibility(View.VISIBLE);
                uploadvideo_lay.setVisibility(View.GONE);
                tv_editvideo.setVisibility(View.VISIBLE);
                videoview.setVideoPath(selectedVideoPath);

                MediaController mediaController = new
                        MediaController(this);
                mediaController.setAnchorView(videoview);
                videoview.setMediaController(mediaController);
                videoview.start();
            }
        }
//        else if (requestCode == SELECT_VIDEO) {
//            if (resultCode == RESULT_OK) {
//                selectedVideoPath = getPath(data.getData());
//                if (selectedVideoPath == null) {
//                    Log.e("selected_video_path =", "null!");
//
//                    videoview.setVisibility(View.GONE);
//                    uploadvideo_lay.setVisibility(View.VISIBLE);
//                    tv_editvideo.setVisibility(View.GONE);
//                    finish();
//                } else {
//                    /**
//                     * Setup selected View
//                     * selectedVideoPath is path to the selected video
//                     */
//
//                    Log.e("selected video path =", " " + selectedVideoPath);
//                    videoview.setVisibility(View.VISIBLE);
//                    uploadvideo_lay.setVisibility(View.GONE);
//                    tv_editvideo.setVisibility(View.VISIBLE);
//                    videoview.setVideoPath(selectedVideoPath);
//
//                    MediaController mediaController = new
//                            MediaController(this);
//                    mediaController.setAnchorView(videoview);
//                    videoview.setMediaController(mediaController);
//                    videoview.start();
//                }
//            }
//        }
    }

    private static Bitmap getBitmapFromUri(@NonNull Context context, @NonNull Uri uri) throws
            IOException {
        Log.e(TAGG, "getBitmapFromUri: Resize uri" + uri);
        ParcelFileDescriptor parcelFileDescriptor =
                context.getContentResolver().openFileDescriptor(uri, "r");
        assert parcelFileDescriptor != null;
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        Log.e(TAGG, "getBitmapFromUri: Height" + deviceHeight);
        Log.e(TAGG, "getBitmapFromUri: width" + deviceWidth);
        int maxSize = Math.min(deviceHeight, deviceWidth);
        if (image != null) {
            Log.e(TAGG, "getBitmapFromUri: Width" + image.getWidth());
            Log.e(TAGG, "getBitmapFromUri: Height" + image.getHeight());
            int inWidth = image.getWidth();
            int inHeight = image.getHeight();
            int outWidth;
            int outHeight;
            if (inWidth > inHeight) {
                outWidth = maxSize;
                outHeight = (inHeight * maxSize) / inWidth;
            } else {
                outHeight = maxSize;
                outWidth = (inWidth * maxSize) / inHeight;
            }
            return Bitmap.createScaledBitmap(image, outWidth, outHeight, false);
        } else {
            Toast.makeText(context, context.getString(R.string.valid_image), Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    @Override
    public void onDocImgClick(Document document, int position) {
        updatedDocument = document;
        this.position = position;
        if (checkStoragePermission()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.CAMERA,
                        Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
            }
        } else {
            goToImageIntent();
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private boolean checkStoragePermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            for (int grantResult : grantResults) {
                if (grantResult == PackageManager.PERMISSION_GRANTED) {
                    if (!isPermissionGivenAlready) {
                        goToImageIntent();
                    }
                }
            }
        }
    }

    private class ItemOffsetDecoration extends RecyclerView.ItemDecoration {

        private int mItemOffset;

        ItemOffsetDecoration(int itemOffset) {
            mItemOffset = itemOffset;
        }

        ItemOffsetDecoration(@NonNull Context context, @DimenRes int itemOffsetId) {
            this(context.getResources().getDimensionPixelSize(itemOffsetId));
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                                   RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            outRect.set(mItemOffset, mItemOffset, mItemOffset, mItemOffset);
        }

    }

    public String getPath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        if (cursor != null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);

        } else return null;
    }

    private byte[] loadFile(File file) throws IOException {
        InputStream is = new FileInputStream(file);

        long length = file.length();
        if (length > Integer.MAX_VALUE) {
            // File is too large
        }
        byte[] bytes = new byte[(int) length];

        int offset = 0;
        int numRead = 0;
        while (offset < bytes.length
                && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
            offset += numRead;
        }

        if (offset < bytes.length) {
            throw new IOException("Could not completely read file " + file.getName());
        }

        is.close();
        return bytes;
    }

}

