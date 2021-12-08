package com.astudent.partner.Utils;

import android.content.Context;
import android.util.Log;

import com.astudent.partner.Helper.CustomDialog;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.MySSLSocketFactory;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import java.security.KeyStore;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.message.BasicHeader;
import cz.msebera.android.httpclient.protocol.HTTP;


public class WebApiHelper {

    public static void callPostApi(final Context context, String url, JSONObject params, final boolean is_loading, final CallBack callBack) {

        final CustomDialog progressDialog = new CustomDialog(context);
        progressDialog.setCancelable(false);

        StringEntity stringEntity = null;
        try {
            stringEntity = new StringEntity(params.toString(), HTTP.UTF_8);
            stringEntity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(60000);

        client.addHeader("X-Requested-With", "XMLHttpRequest");
        client.addHeader("Content-Type", "multipart/form-data");
        try {
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null, null);
            MySSLSocketFactory sf = new MySSLSocketFactory(trustStore);
            sf.setHostnameVerifier(MySSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            client.setSSLSocketFactory(sf);

        } catch (Exception e) {
            e.printStackTrace();
        }
        client.post(context, url, stringEntity, "application/json", new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                if (is_loading) {
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (progressDialog.isShowing())
                    progressDialog.dismiss();
                callBack.onResponse(true, new String(responseBody));
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if (progressDialog.isShowing())
                    progressDialog.dismiss();
                callBack.onResponse(false, error.getLocalizedMessage());
                if (statusCode == 401) {
                    Log.e("TAG", error.getLocalizedMessage());
                }
            }
        });
    }

    public static void callGetApi(Context context, String url, final boolean is_loading, final CallBack callBack) {

        final CustomDialog progressDialog = new CustomDialog(context);
        progressDialog.setCancelable(false);
        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(60000);
        client.addHeader("Accept", "application/json");

        client.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                if (is_loading) {
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (progressDialog.isShowing())
                    progressDialog.dismiss();

                callBack.onResponse(true, new String(responseBody));

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if (progressDialog.isShowing())
                    progressDialog.dismiss();
                callBack.onResponse(false, error.getLocalizedMessage());
                if (statusCode == 401) {
                    Log.e("TAG", error.getLocalizedMessage());
                }
            }
        });
    }
}
