package com.astudent.partner.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;
import com.astudent.partner.Helper.AppHelper;
import com.astudent.partner.Models.ServiceListModel;
import com.astudent.partner.R;

import org.json.JSONArray;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ServiceListAdapter extends RecyclerView.Adapter<ServiceListAdapter.ViewHolder> {

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
    public ServiceListAdapter(ArrayList<ServiceListModel> listModel, Context context) {
        this.listModels = listModel;
        this.context = context;
    }

    public interface ServiceClickListener {
        void onServiceClick(boolean[] selectedService, ServiceListModel serviceListModel);

        void onServiceUnSelect(boolean[] selectedService, ServiceListModel serviceListModel);
    }


    public List<ServiceListModel> getServiceListModel() {
        return listModels;
    }

    public void setServiceClickListener(ServiceClickListener serviceClickListener) {
        this.serviceClickListener=serviceClickListener;
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
            if (serviceClickListener != null) {
                pos = (int) v.getTag();
                ServiceListModel serviceListModel = listModels.get(pos);
                if (!selectedService[pos] && serviceListModel.getAvailable().equalsIgnoreCase("false")) {
                    serviceListModel = listModels.get(pos);
                    serviceListModel.setAvailable("true");
                    Log.d(TAG, "onClick: " + serviceListModel.getPricePerHour());
                    serviceClickListener.onServiceClick(selectedService, serviceListModel);
                    selectedService[pos] = true;
                    service_list_name.setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));
                    selectImg.setVisibility(View.VISIBLE);
                } else {
                    serviceListModel = listModels.get(pos);
                    selectedService[pos] = false;
                    service_list_name.setTextColor(ContextCompat.getColor(context, android.R.color.black));
                    serviceClickListener.onServiceUnSelect(selectedService, serviceListModel);
                    serviceListModel.setAvailable("false");
                    selectImg.setVisibility(View.GONE);
                    servicePriceTxt.setVisibility(View.GONE);
                }
            }
        }
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        final ServiceListModel serviceListModel = listModels.get(position);
        selectedService = new boolean[listModels.size()];
        Log.v(TAG, "Response Name " + serviceListModel.getAvailable());
        holder.service_list_name.setText(serviceListModel.getName());
        //Glide.with(context).load(R.drawable.carpentry_select).into(holder.service_image_icon);
        Log.e(TAG, "onBindViewHolder: " + AppHelper.getImageUrl(serviceListModel.getImage()));
        Picasso.with(context).load(AppHelper.getImageUrl(serviceListModel.getImage()))
                .error(R.drawable.grey_bg).placeholder(R.drawable.grey_bg)
                .fit().memoryPolicy(MemoryPolicy.NO_CACHE).centerCrop().into(holder.service_image_icon);
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

    public static Bitmap getBitmapFromURL(String strURL) {
        try {
            URL url = new URL(strURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public int getItemCount() {
        return listModels.size();
    }

    private void showDialog(String price, final ViewHolder viewHolder){

    }

    /*{
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = layoutInflater.inflate(R.layout.get_price_layout, null);
        dialogBuilder.setView(dialogView);

        final EditText editText = (EditText) dialogView.findViewById(R.id.ed_price);
        final Button submitBtn = (Button) dialogView.findViewById(R.id.submit_btn);
        //final ImageView closeImg = (ImageView) dialogView.findViewById(R.id.close_img);
        final AlertDialog alertDialog = dialogBuilder.create();
        if (price != null)
            editText.setText(price);
        alertDialog.show();
       *//* closeImg.setVisibility(View.GONE);
        closeImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedService[pos] = false;
                alertDialog.dismiss();
            }
        });*//*

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editText.getText().length() > 0) {
                    serviceListModel = listModels.get(pos);
                    serviceListModel.setPricePerHour(editText.getText().toString());
                    serviceListModel.setPrice(editText.getText().toString());
                    serviceListModel.setAvailable("true");
                    Log.d(TAG, "onClick: " + serviceListModel.getPricePerHour());
                    serviceClickListener.onServiceClick(selectedService, serviceListModel);
                    selectedService[pos] = true;
                    viewHolder.service_list_name.setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));
                    viewHolder.selectImg.setVisibility(View.VISIBLE);
                    viewHolder.servicePriceTxt.setVisibility(View.VISIBLE);
                    notifyDataSetChanged();
                    alertDialog.dismiss();
                } else {
                    editText.setError(context.getString(R.string.required));
                }
            }
        });
    }*/
}
