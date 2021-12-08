package com.astudent.partner.Adapter;

import android.content.Context;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;
import com.astudent.partner.Constant.URLHelper;
import com.astudent.partner.Helper.AppHelper;
import com.astudent.partner.Helper.CustomDialog;
import com.astudent.partner.Helper.SharedHelper;
import com.astudent.partner.Models.ServiceListModel;
import com.astudent.partner.R;
import com.astudent.partner.TutorApplication;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SettingsServiceListAdapter extends RecyclerView.Adapter<SettingsServiceListAdapter.ViewHolder> {

    private ArrayList<ServiceListModel> listModels;
    private Context context;
    JSONArray jsonArraylist;
    private RadioButton lastChecked = null;
    BottomSheetBehavior behavior;
    String TAG = "ServiceListAdapter";
    private int pos;
    private ServiceClickListener serviceClickListener;
    ServiceListModel serviceListModel;
    boolean[] selectedService;
    boolean select;
    String serviceName;
    private CustomDialog customDialog;

    public SettingsServiceListAdapter(ArrayList<ServiceListModel> listModel, Context context) {
        this.listModels = listModel;
        this.context = context;
    }

    public void setSelect(boolean select) {
        this.select = select;
    }

    public interface ServiceClickListener {
        void onServiceClick(boolean[] selectedService, ServiceListModel serviceListModel);

        void onServiceUnSelect(boolean[] selectedService, ServiceListModel serviceListModel);
    }


    public List<ServiceListModel> getServiceListModel() {
        return listModels;
    }

    public void setServiceClickListener(ServiceClickListener serviceClickListener) {
        this.serviceClickListener = serviceClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.service_list_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView service_list_name, servicePriceTxt;
        ImageView service_image_icon, selectImg;
        CheckBox service_checkbox;

        public ViewHolder(View itemView) {
            super(itemView);
            service_image_icon = (ImageView) itemView.findViewById(R.id.service_image_icon);
            service_list_name = (TextView) itemView.findViewById(R.id.service_list_name);
            servicePriceTxt = (TextView) itemView.findViewById(R.id.service_price);
            selectImg = (ImageView) itemView.findViewById(R.id.select);
            service_list_name.setOnClickListener(this);
            service_image_icon.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
//            if (serviceClickListener != null && select) {
                pos = (int) v.getTag();
                ServiceListModel serviceListModel = listModels.get(pos);
            Log.e(TAG, "onClick: ServiceListModel" + serviceListModel);
                    if (serviceListModel.getName().equalsIgnoreCase("Others")){
                        showServicePopUp();
                    }else {
                        serviceListModel.setAvailable("true");
                        Log.d(TAG, "onClick: " + serviceListModel.getPricePerHour());
                        serviceClickListener.onServiceClick(selectedService, serviceListModel);
                        selectedService[pos] = true;
                        service_list_name.setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));
                    }
        }
    }

    private void showServicePopUp() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = layoutInflater.inflate(R.layout.get_price_layout, null);
        dialogBuilder.setView(dialogView);
        final EditText editText = (EditText) dialogView.findViewById(R.id.serviceName);
        final Button submitBtn = (Button) dialogView.findViewById(R.id.submitBtn);
        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editText != null && !editText.getText().toString().equalsIgnoreCase(""))
                    serviceName = editText.getText().toString();
                else
                    Toast.makeText(context,"Kindly enter service name",Toast.LENGTH_SHORT).show();
                alertDialog.dismiss();
                sendAddServiceReq(serviceName);
            }
        });
    }

    private void sendAddServiceReq(final String serviceName) {
        customDialog = new CustomDialog(context);
        customDialog.setCancelable(false);
        customDialog.show();
        JSONObject object = new JSONObject();
        try {
            object.put("services", serviceName);
            Log.e("servicetypename req", "" + object);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLHelper.UPDATE_SERVICE,
                object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                customDialog.dismiss();
                Log.v("SignInResponse", response.toString());
                Toast.makeText(context,response.optString("message"),Toast.LENGTH_SHORT).show();
                //displayMessage(context.getResources().getString(R.string.password_Change_done));
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
                                displayMessage(context.getString(R.string.something_went_wrong));
                            }
                        } else if (response.statusCode == 401) {
                            GoToBeginActivity();
                        } else if (response.statusCode == 422) {
                            json = TutorApplication.trimMessage(new String(response.data));
                            if (json != "" && json != null) {
                                displayMessage(json);
                            } else {
                                displayMessage(context.getString(R.string.please_try_again));
                            }
                        } else {
                            displayMessage(context.getString(R.string.please_try_again));
                        }

                    } catch (Exception e) {
                        displayMessage(context.getString(R.string.something_went_wrong));
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

    private void GoToBeginActivity() {
    }

    public void displayMessage(String toastString) {
        try {
           /* Snackbar snackbar = Snackbar.make(context, toastString, Snackbar.LENGTH_SHORT);
            View snackBarView = snackbar.getView();
            snackBarView.setBackgroundColor(ContextCompat.getColor(ForgetPassword.this, R.color.black));
            TextView textView = (TextView) snackBarView.findViewById(R.id.snackbar_text);
            textView.setTextColor(ContextCompat.getColor(ForgetPassword.this, R.color.white));
            snackbar.show();*/
        } catch (Exception e) {
            try {
                //Toast.makeText(ForgetPassword.this, "" + toastString, Toast.LENGTH_SHORT).show();
            } catch (Exception ee) {
                ee.printStackTrace();
            }
        }
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        final ServiceListModel serviceListModel = listModels.get(position);
        selectedService = new boolean[listModels.size()];
        Log.v(TAG, "Response Name " + serviceListModel.getAvailable());
        holder.service_list_name.setText(serviceListModel.getName());
        Log.e(TAG, "onBindViewHolder: " + AppHelper.getImageUrl(serviceListModel.getImage()));
        if (serviceListModel.getImage() != null&&
                !serviceListModel.getImage().equalsIgnoreCase("")) {
            Picasso.with(context).load(AppHelper.getImageUrl(serviceListModel.getImage()))
                    .error(R.drawable.grey_bg).placeholder(R.drawable.grey_bg)
                    .fit().memoryPolicy(MemoryPolicy.NO_CACHE).centerCrop().into(holder.service_image_icon);
        }
        holder.service_image_icon.setTag(position);
        holder.service_list_name.setTag(position);
        if (serviceClickListener != null && serviceListModel.getAvailable().equalsIgnoreCase("true")) {
            holder.service_list_name.setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));
            holder.selectImg.setVisibility(View.VISIBLE);
        } else {
            holder.service_list_name.setTextColor(ContextCompat.getColor(context, R.color.black));
            holder.selectImg.setVisibility(View.GONE);
        }

    }
    @Override
    public int getItemCount() {
        return listModels.size();
    }

}
