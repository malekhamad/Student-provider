package com.astudent.partner.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import androidx.annotation.DimenRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.snackbar.Snackbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;
import com.astudent.partner.Activity.EditProfile;
import com.astudent.partner.Adapter.SettingsServiceListAdapter;
import com.astudent.partner.Adapter.SettingsSubServiceListAdapter;
import com.astudent.partner.Adapter.SubServiceListModel;
import com.astudent.partner.Constant.URLHelper;
import com.astudent.partner.Helper.CustomDialog;
import com.astudent.partner.Helper.SharedHelper;
import com.astudent.partner.Models.ServiceListModel;
import com.astudent.partner.R;
import com.astudent.partner.Utils.KeyHelper;
import com.astudent.partner.TutorApplication;
import com.astudent.partner.Utils.Keyname;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.astudent.partner.TutorApplication.trimMessage;

public class SettingsFragment extends Fragment implements SettingsServiceListAdapter.ServiceClickListener,
        View.OnClickListener, SettingsSubServiceListAdapter.SubServiceClickListener {

    public static final String TAG = "SettingsFragment";
    SettingsServiceListAdapter serviceListAdapter;
    SettingsSubServiceListAdapter subServiceListAdapter;
    private RecyclerView recyclerView;
    RecyclerView subRecycler;
    FragmentActivity mActivity;
    Context context;
    Button btnSave;
    SeekBar seekBar;
    TextView lblCount;
    int availableService = 0;
    CustomDialog loadingDialog;
    ArrayList<String> lstSettings = new ArrayList<>();
    ArrayList<ServiceListModel> serviceListModels;
    ServiceListModel serviceListModel;
    ArrayList<SubServiceListModel> subserviceListModels;
    ArrayList<String> selectedSubServiceList;


    TextView editTxt;
    ImageView userImg, backImg;
    TextView lblName;

    ArrayList<Services> selectedServices = new ArrayList<>();
    ArrayList<SubServices> selectedSubSubService = new ArrayList<>();
    boolean[] selectedService;
    boolean[] selectedSubService;
    private SettingsFgmtListener listener;

    Button subserviceupdate;
    Button subservicecancel;

    @Override
    public void onServiceClick(boolean[] selectedService, ServiceListModel serviceListModel) {
        this.selectedService = selectedService;
        Services services = new Services();
        services.setId(serviceListModel.getId());
        services.setPrice(serviceListModel.getPrice());
        selectedServices.add(services);
        seekBar.setProgress(++availableService);
        lblCount.setText(availableService + "/" + serviceListModels.size() + "");
        btnSave.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
        ShowSubserviceList(serviceListModel.getsubservices(),serviceListModel);
    }

    private void ShowSubserviceList(String subservices, final ServiceListModel serviceListModel) {
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.subservice_dialog, null);
        dialogBuilder.setView(dialogView);

//        subRecycler.getAdapter().notifyDataSetChanged();
//        setupSubRecyclerView();
        selectedSubSubService.clear();

        subserviceListModels = new ArrayList<>();
        subRecycler=(RecyclerView) dialogView.findViewById(R.id.selectSubServiceRecyclerview);
       subserviceupdate = (Button) dialogView.findViewById(R.id.subserviceupdate);
       subservicecancel = (Button) dialogView.findViewById(R.id.subservicecancel);
        try {
            JSONArray SubJArray = new JSONArray(subservices);
            for (int j=0;j<SubJArray.length();j++) {
                SubServiceListModel subserviceListModel = new SubServiceListModel();
                subserviceListModel.setId(SubJArray.optJSONObject(j).optString("id"));
                subserviceListModel.setName(SubJArray.optJSONObject(j).optString("name"));
                subserviceListModel.setAvailable(SubJArray.optJSONObject(j).optString("available"));
                subserviceListModels.add(subserviceListModel);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

//        subserviceListModels.get(0).getId();

        subServiceListAdapter = new SettingsSubServiceListAdapter(subserviceListModels, getContext());
        subServiceListAdapter.setServiceClickListener(this);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 3);
        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(getContext(), R.dimen._5sdp);
        subRecycler.addItemDecoration(itemDecoration);
        subRecycler.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        subRecycler.setAdapter(subServiceListAdapter);





        dialogBuilder.setTitle(getResources().getString(R.string.Choose_sub_service_type));
        final AlertDialog b = dialogBuilder.create();
        b.show();

        subserviceupdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(context,"Ok",Toast.LENGTH_LONG).show();

//                if (selectedSubSubService.isEmpty()){
//                    Toast.makeText(context,"Select Atleast one session",Toast.LENGTH_LONG).show();
//                }else {
                    sendSubServiceUpdateRequest();

//                }


                b.dismiss();
            }
        });

        subservicecancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(context,"cancel",Toast.LENGTH_LONG).show();
                b.dismiss();

                selectedSubSubService.clear();
                setupRecyclerView();
//        setupSubRecyclerView();
                getServices();

//                getServices();
//                recyclerView.getAdapter().notifyDataSetChanged();
//                onServiceUnSelect(new boolean[]{false},serviceListModel);
            }
        });



    }

    @Override
    public void onServiceUnSelect(boolean[] selectedService, ServiceListModel serviceListModel) {
        this.selectedService = selectedService;
        Services services = new Services();
        services.setId(serviceListModel.getId());
        services.setPrice(serviceListModel.getPrice());
        selectedServices.remove(services);
        seekBar.setProgress(--availableService);
        lblCount.setText(availableService + "/" + serviceListModels.size() + "");
        if (selectedServices.size() == 0) {
            btnSave.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.offline));
        }
    }

    @Override
    public void onClick(View v) {
        if (v == editTxt) {
            if (editTxt.getText().toString().equalsIgnoreCase(getString(R.string.edit))) {
                serviceListAdapter.setSelect(true);
                editTxt.setText(getString(R.string.save));
            } else {
                sendSubServiceUpdateRequest();
            }
        }
        if (v == backImg) {
            getFragmentManager().popBackStackImmediate();
            listener.onBackClick();
        }

        if (v == userImg){
            Intent intent = new Intent(context, EditProfile.class);
            intent.putExtra(Keyname.EDIT_PROFILE, true);
            startActivity(intent);
        }

    }

    @Override
    public void onSubServiceClick(boolean[] selectedService, SubServiceListModel subServiceListModel) {
        this.selectedSubService = selectedService;
        SubServices services = new SubServices();
        services.setId(subServiceListModel.getId());
        services.setPrice(subServiceListModel.getPrice());
        selectedSubSubService.add(services);
        lstSettings.add(subServiceListModel.getId());
        selectedSubServiceList.add(subServiceListModel.getId());
        seekBar.setProgress(++availableService);
        lblCount.setText(availableService + "/" + serviceListModels.size() + "");
        btnSave.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
//        ShowSubserviceList(subServiceListModel.getsubservices());
        setButtonColor();
    }

    private void setButtonColor() {
        if (lstSettings.size()<1){
//                    Toast.makeText(context,"Select Atleast one session",Toast.LENGTH_LONG).show();
            subserviceupdate.setClickable(false);
            subserviceupdate.setAlpha((float) 0.5);
        }else {
            subserviceupdate.setClickable(true);
            subserviceupdate.setAlpha((float) 1.0);
        }
    }

    @Override
    public void onSubServiceUnSelect(boolean[] selectedService, SubServiceListModel subServiceListModel) {
        this.selectedSubService = selectedService;
        SubServices services = new SubServices();
        services.setId(subServiceListModel.getId());
        services.setPrice(subServiceListModel.getPrice());
        selectedSubSubService.remove(services);
        lstSettings.remove(subServiceListModel.getId());
        selectedSubServiceList.remove(subServiceListModel.getId());
        seekBar.setProgress(--availableService);
        lblCount.setText(availableService + "/" + serviceListModels.size() + "");
        setButtonColor();
        subServiceListAdapter.notifyDataSetChanged();
        if (selectedSubSubService.size() == 0) {
            btnSave.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.offline));
        }
    }



    public interface SettingsFgmtListener {
        void onBackClick();
    }

    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.settings, container, false);

        //samplerequest();

        btnSave = (Button) view.findViewById(R.id.btnSave);
        seekBar = (SeekBar) view.findViewById(R.id.seekBar);
        lblCount = (TextView) view.findViewById(R.id.lblCount);
        userImg = (ImageView) view.findViewById(R.id.profile_image);
        lblName = (TextView) view.findViewById(R.id.lblName);
        editTxt = (TextView) view.findViewById(R.id.edit_txt);
        backImg = (ImageView) view.findViewById(R.id.back_img);

        backImg.setOnClickListener(this);
        editTxt.setOnClickListener(this);
        userImg.setOnClickListener(this);

        /*backImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().popBackStackImmediate();

            }
        });*/

        setProviderDetails();
        selectedServices = new ArrayList<>();
        selectedSubSubService = new ArrayList<>();

        recyclerView = (RecyclerView) view.findViewById(R.id.selectServiceRecyclerview);
        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(getContext(), R.dimen._5sdp);
        recyclerView.addItemDecoration(itemDecoration);
//        subRecycler=(RecyclerView) view.findViewById(R.id.selectSubServiceRecyclerview);
//        subserviceListModels = new ArrayList<>();
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendSubServiceUpdateRequest();
            }
        });

        loadingDialog = new CustomDialog(getContext());
        loadingDialog.setCancelable(false);

        setupRecyclerView();
//        setupSubRecyclerView();
        getServices();

        return view;
    }

    private void setupSubRecyclerView() {
//        subserviceListModels = new ArrayList<>();
        subServiceListAdapter = new SettingsSubServiceListAdapter(subserviceListModels, getContext());
        subServiceListAdapter.setServiceClickListener(this);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 3);
        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(getContext(), R.dimen._5sdp);
        subRecycler.addItemDecoration(itemDecoration);
        subRecycler.setLayoutManager(gridLayoutManager);
        subRecycler.setAdapter(subServiceListAdapter);
    }

    public void setProviderDetails() {
        String name = SharedHelper.getKey(getContext(), KeyHelper.KEY_FIRST_NAME);
        if (name != null && !name.equalsIgnoreCase("null") && name.length() > 0) {
            String lName = SharedHelper.getKey(getContext(), "last_name");
            if (lName != null && !lName.equalsIgnoreCase("null") && lName.length() > 0)
                lblName.setText(name + " " + lName);
            else
                lblName.setText(name);
        }
        if (!SharedHelper.getKey(getContext(), "picture").equalsIgnoreCase("")) {
            Picasso.with(getContext()).load(SharedHelper.getKey(getContext(), "picture"))
                    .fit().centerCrop().memoryPolicy(MemoryPolicy.NO_CACHE).
                    placeholder(R.drawable.ic_dummy_user).error(R.drawable.ic_dummy_user).into(userImg);
        }
    }

    private void sendSubServiceUpdateRequest() {
        loadingDialog.show();
        JSONObject object = new JSONObject();
        List<SubServiceListModel> service = subServiceListAdapter.getServiceListModel();
//        selectedServices = new ArrayList<>();
        selectedSubSubService = new ArrayList<>();
        for (SubServiceListModel services : service) {
            if (services.getAvailable().equalsIgnoreCase("true")) {
                SubServices select = new SubServices();
                select.setId(services.getId());
//                select.setPrice(services.getPricePerHour());
                select.setName(services.getName());
                selectedSubSubService.add(select);
            }
        }

        ArrayList<String> subServiceArraylist = new ArrayList<>();
        String strSubService ="";
        for (int i = 0; i < selectedSubServiceList.size(); i++) {
            HashMap<String, String> services = new HashMap<String, String>();
//            subServiceArraylist.add(selectedSubSubService.get(i).getId());
            strSubService += (selectedSubServiceList.get(i).toString())+",";
        }

        Log.e(TAG, "services_arraylist: " + subServiceArraylist);


        try {
            JSONArray jsonarray = new JSONArray(subServiceArraylist);
            if (strSubService.endsWith(",")) {
                strSubService = strSubService.substring(0, strSubService.length() - 1);
            }
            object.put("services", strSubService.trim());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e(TAG, "sendServiceUpdateRequest: Req body" + object.toString());
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, URLHelper.UPDATE_SERVICE,
                object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                loadingDialog.dismiss();
                Log.d(TAG, "onResponse: ");
                Toast.makeText(context, getResources().getString(R.string.services_updated), Toast.LENGTH_SHORT).show();
//                listener.onBackClick();
//                recyclerView.getAdapter().notifyDataSetChanged();
                setupRecyclerView();
//        setupSubRecyclerView();
                getServices();


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loadingDialog.dismiss();
                errorResponse(error);
//                ShowSubserviceList();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("X-Requested-With", "XMLHttpRequest");
                headers.put("Authorization", "" + "Bearer" + " " + SharedHelper.getKey(getContext(), "access_token"));
                return headers;
            }
        };
        TutorApplication.getInstance().addToRequestQueue(request);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (SettingsFgmtListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement SettingsFgmtListener in Main Activity");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //KeyboardDismisser.useWith(this);
    }

    private void setupRecyclerView() {
        serviceListModels = new ArrayList<>();
        serviceListAdapter = new SettingsServiceListAdapter(serviceListModels, getContext());
        serviceListAdapter.setServiceClickListener(this);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 3);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(serviceListAdapter);
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

    private void getServices() {
        loadingDialog.show();
        final JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET,
                URLHelper.GET_SERVICES, new JSONArray(), new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                if (response != null && response.length() > 0)
                    loadRecyclerView(response);

                selectedSubServiceList = new ArrayList<>();
                for (int i = 0; i < response.length(); i++) {

                    JSONArray SubArray = response.optJSONObject(i).optJSONArray("subservice");
                    for (int j =0;j<SubArray.length();j++ ){
                        if (SubArray.optJSONObject(j).optString("available").equalsIgnoreCase("true")){
//                        strSubService += (serviceListModels.get(i).getId()).toString()+",";
                            selectedSubServiceList.add(SubArray.optJSONObject(j).optString("id"));
                        }
                    }

                }

                Log.e("selectedSubServiceList : ",selectedSubServiceList.toString() );


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loadingDialog.dismiss();
                errorResponse(error);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("X-Requested-With", "XMLHttpRequest");
                headers.put("Authorization", "" + "Bearer" + " " + SharedHelper.getKey(getContext(), KeyHelper.KEY_ACCESS_TOKEN));
                return headers;
            }
        };
        TutorApplication.getInstance().addToRequestQueue(jsonArrayRequest);
    }

    private void loadRecyclerView(JSONArray jsonArray) {
        int cnt = jsonArray.length();
        try {
            seekBar.setMax(cnt);
            availableService = 0;
            for (int i = 0; i < cnt; i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                serviceListModel = new ServiceListModel();
                serviceListModel.setId(jsonObject.optString("id"));
                serviceListModel.setName(jsonObject.optString("name"));
                serviceListModel.setImage(jsonObject.optString("image"));
                serviceListModel.setDescription(jsonObject.optString("description"));
                serviceListModel.setAvailable(jsonObject.optString("available"));
                serviceListModel.setPricePerHour(jsonObject.optString("price_per_hour"));
                for (int j=0;j<jsonObject.optJSONArray("subservice").length();j++){
                    serviceListModel.setsubservices(jsonObject.optJSONArray("subservice").toString());
                }
                if (serviceListModel.getAvailable().equalsIgnoreCase("true")) {
                    availableService++;
                    serviceListModels.add(serviceListModel);
                }
            }
            if (!serviceListModel.getName().equalsIgnoreCase("Others")) {
                serviceListModel.setName("Others");
//                serviceListModels.add(serviceListModel);
            }
            loadingDialog.dismiss();
            recyclerView.getAdapter().notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        seekBar.setProgress(availableService);
        lblCount.setText(availableService + "/" + cnt + "");
    }

    private void errorResponse(VolleyError error) {
        String json;
        NetworkResponse response = error.networkResponse;
        if (response != null && response.data != null) {
            Log.e("MyTest", "" + error);
            Log.e("MyTestError", "" + error.networkResponse);
            Log.e("MyTestError1", "" + response.statusCode);
            try {
                JSONObject errorObj = new JSONObject(new String(response.data));
                Log.d(TAG, "onErrorResponse: " + errorObj);

                if (response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500 || response.statusCode == 401) {
                    try {
                        if (errorObj.optString("error") != null && errorObj.optString("error").length() > 0)
                            displayMessage(errorObj.optString("error"));
                        else
                            displayMessage(getString(R.string.something_went_wrong));
                    } catch (Exception e) {
                        displayMessage(getString(R.string.something_went_wrong));
                    }
                } else if (response.statusCode == 422) {
                    json = trimMessage(new String(response.data));
                    if (json != null && !json.equals("")) {
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

    public void displayMessage(String toastString) {
        try {
            Snackbar snackbar = Snackbar.make(getView(), toastString, Snackbar.LENGTH_SHORT);
            View snackBarView = snackbar.getView();
            snackBarView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.black));
            TextView textView = (TextView) snackBarView.findViewById(R.id.snackbar_text);
            textView.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
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
    public void onAttach(Activity activity) {
        mActivity = (FragmentActivity) activity;
        super.onAttach(activity);
    }

    private class Services {
        String id, price;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }
    }

    private class SubServices {
        String id, price,name;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return id;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }
    }


}
