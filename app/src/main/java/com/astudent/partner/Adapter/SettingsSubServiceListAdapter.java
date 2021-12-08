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
import android.widget.Toast;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;
import com.astudent.partner.Helper.AppHelper;
import com.astudent.partner.R;

import org.json.JSONArray;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by CSS10 on 18-01-2018.
 */

public class SettingsSubServiceListAdapter extends RecyclerView.Adapter<SettingsSubServiceListAdapter.ViewHolder> {

    private ArrayList<SubServiceListModel> listModels;
    private Context context;
    JSONArray jsonArraylist;
    private RadioButton lastChecked = null;
    BottomSheetBehavior behavior;
    String TAG = "ServiceListAdapter";
    private int pos;
    private SubServiceClickListener serviceClickListener;
    SubServiceListModel serviceListModel;
    boolean[] selectedService;
    boolean select;

    ArrayList<String> SelectedIdlist= new ArrayList<String>();


public SettingsSubServiceListAdapter(ArrayList<SubServiceListModel> listModel, Context context) {
        this.listModels = listModel;
        this.context = context;
        }

public void setSelect(boolean select) {
        this.select = select;
        }

public interface SubServiceClickListener {
    void onSubServiceClick(boolean[] selectedService, SubServiceListModel subServiceListModel);

    void onSubServiceUnSelect(boolean[] selectedService, SubServiceListModel subServiceListModel);
}


    public List<SubServiceListModel> getServiceListModel() {
        return listModels;
    }

    public ArrayList<String> getSelectedSubList() {
        return SelectedIdlist;
    }


    public void setServiceClickListener(SettingsSubServiceListAdapter.SubServiceClickListener serviceClickListener) {
        this.serviceClickListener = serviceClickListener;
    }

    @Override
    public SettingsSubServiceListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.sub_service_list_item_new, parent, false);
        SettingsSubServiceListAdapter.ViewHolder vh = new SettingsSubServiceListAdapter.ViewHolder(v);
        return vh;
    }


public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    TextView service_list_name, servicePriceTxt;
    ImageView selectImg;
    CheckBox service_checkbox;

    public ViewHolder(View itemView) {
        super(itemView);
//        service_image_icon = (ImageView) itemView.findViewById(R.id.service_image_icon);
        service_list_name = (TextView) itemView.findViewById(R.id.service_list_name);
        servicePriceTxt = (TextView) itemView.findViewById(R.id.service_price);
        selectImg = (ImageView) itemView.findViewById(R.id.imgSelect);
        service_list_name.setOnClickListener(this);
//        service_image_icon.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
//            if (serviceClickListener != null && select) {
        pos = (int) v.getTag();
        SubServiceListModel subServiceListModel = listModels.get(pos);
        String subServiceString = subServiceListModel.getsubservices();
        if (!selectedService[pos] && subServiceListModel.getAvailable().equalsIgnoreCase("false")) {
            serviceListModel = listModels.get(pos);
            subServiceListModel.setAvailable("true");
            Log.d(TAG, "onClick: " + subServiceListModel.getPricePerHour());
            serviceClickListener.onSubServiceClick(selectedService, subServiceListModel);
            selectedService[pos] = true;
            service_list_name.setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));
            selectImg.setVisibility(View.VISIBLE);
            Picasso.with(context).load(R.drawable.check_tick)
                    .error(R.drawable.check_tick).placeholder(R.drawable.check_tick)
                    .fit().memoryPolicy(MemoryPolicy.NO_CACHE).centerCrop().into(selectImg);

        } else {
            subServiceListModel = listModels.get(pos);
            selectedService[pos] = false;
            service_list_name.setTextColor(ContextCompat.getColor(context, android.R.color.black));
            serviceClickListener.onSubServiceUnSelect(selectedService, subServiceListModel);
            subServiceListModel.setAvailable("false");
            Picasso.with(context).load(R.drawable.check_untick)
                    .error(R.drawable.check_untick).placeholder(R.drawable.check_untick)
                    .fit().memoryPolicy(MemoryPolicy.NO_CACHE).centerCrop().into(selectImg);
//            selectImg.setVisibility(View.GONE);
//            servicePriceTxt.setVisibility(View.GONE);
        }
//            } else {
//                displayMessage();
//            }
    }
}

    public void displayMessage() {
        try {
            Toast.makeText(context, "Please contact admin to update your service", Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onBindViewHolder(final SettingsSubServiceListAdapter.ViewHolder holder, final int position) {

        final SubServiceListModel subServiceListModel = listModels.get(position);
        selectedService = new boolean[listModels.size()];
        Log.v(TAG, "Response Name " + subServiceListModel.getAvailable());
        holder.service_list_name.setText(subServiceListModel.getName());
        //Glide.with(context).load(R.drawable.carpentry_select).into(holder.service_image_icon);
        Log.e(TAG, "onBindViewHolder: " + AppHelper.getImageUrl(subServiceListModel.getImage()));
        /*Picasso.with(context).load(AppHelper.getImageUrl(subServiceListModel.getImage()))
                .error(R.drawable.grey_bg).placeholder(R.drawable.grey_bg)
                .fit().memoryPolicy(MemoryPolicy.NO_CACHE).centerCrop().into(holder.service_image_icon);
        holder.service_image_icon.setTag(position);*/
        holder.service_list_name.setTag(position);
        if (serviceClickListener != null && subServiceListModel.getAvailable().equalsIgnoreCase("true")) {
            holder.service_list_name.setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));
            Picasso.with(context).load(R.drawable.check_tick)
                    .error(R.drawable.check_tick).placeholder(R.drawable.check_tick)
                    .fit().memoryPolicy(MemoryPolicy.NO_CACHE).centerCrop().into(holder.selectImg);

            /*SelectedIdlist.add(subServiceListModel.getId());
            subServiceListModel.setAvailable("true");
            serviceClickListener.onSubServiceClick(new boolean[]{true}, subServiceListModel);*/

        } else {
            holder.service_list_name.setTextColor(ContextCompat.getColor(context, R.color.black));
            Picasso.with(context).load(R.drawable.check_untick)
                    .error(R.drawable.check_untick).placeholder(R.drawable.check_untick)
                    .fit().memoryPolicy(MemoryPolicy.NO_CACHE).centerCrop().into(holder.selectImg);
        }


       /* holder.select_icon.setTag(position);

        if (position == 0 && holder.select_icon.isChecked()) {
            lastChecked = holder.select_icon;
            lastCheckedPos = 0;
        }*/


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

    private void showDialog(String price, final SettingsSubServiceListAdapter.ViewHolder viewHolder){

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
                    serviceClickListener.onSubServiceClick(selectedService, serviceListModel);
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
