package com.astudent.partner.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.snackbar.Snackbar;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.astudent.partner.Activity.ActivityPassword;
import com.astudent.partner.Activity.HistoryDetails;
import com.astudent.partner.Constant.URLHelper;
import com.astudent.partner.Helper.AppHelper;
import com.astudent.partner.Helper.ConnectionHelper;
import com.astudent.partner.Helper.CustomDialog;
import com.astudent.partner.Helper.SharedHelper;
import com.astudent.partner.R;
import com.astudent.partner.TutorApplication;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static com.astudent.partner.TutorApplication.trimMessage;

public class PastTrips extends Fragment {
    public static final String TAG = "PastTrips";
    Activity activity;
    static Context context;
    Boolean isInternet;
    PostAdapter postAdapter;
    RecyclerView recyclerView;
    RelativeLayout errorLayout;
    LinearLayout lnrList;
    ConnectionHelper helper;
    CustomDialog customDialog;
    View rootView;
    ImageView backArrow;
    PastTripsListener mListener;

    LinearLayout toolbar;

    public PastTrips() {
        // Required empty public constructor
    }

    public static PastTrips newInstance() {
        return new PastTrips();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_past_trips, container, false);
        findViewByIdAndInitialize();
        if (activity == null) {
            activity = getActivity();
        }

        if (context == null) {
            context = getContext();
        }

        if (isInternet) {
            getHistoryList();
        }

        Bundle bundle = getArguments();
        String toolbar = null;
        if (bundle != null)
            toolbar = bundle.getString("toolbar");

        if (toolbar != null && toolbar.length() > 0) {
            this.toolbar.setVisibility(View.VISIBLE);
        }

        return rootView;
    }


    public void getHistoryList() {

        customDialog = new CustomDialog(context);
        customDialog.setCancelable(false);
        customDialog.show();

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(URLHelper.GET_HISTORY_REQUEST, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                if (response != null) {
                    if (response.length() > 0) {


                        postAdapter = new PostAdapter(response);
                        recyclerView.setHasFixedSize(true);
                        recyclerView.setLayoutManager(new LinearLayoutManager(activity) {
                            @Override
                            public RecyclerView.LayoutParams generateDefaultLayoutParams() {
                                return new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                        ViewGroup.LayoutParams.WRAP_CONTENT);
                            }
                        });
                        if (postAdapter != null && postAdapter.getItemCount() > 0) {
                            errorLayout.setVisibility(View.GONE);
                            lnrList.setVisibility(View.VISIBLE);
                            recyclerView.setAdapter(postAdapter);
                        } else {
                            errorLayout.setVisibility(View.VISIBLE);
                            lnrList.setVisibility(View.GONE);
                        }

                    } else {
                        lnrList.setVisibility(View.GONE);
                        errorLayout.setVisibility(View.VISIBLE);
                    }
                } else {
                    lnrList.setVisibility(View.GONE);
                    errorLayout.setVisibility(View.VISIBLE);
                }

                customDialog.dismiss();

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
                            GoToBeginActivity();
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
                    displayMessage(getString(R.string.please_try_again));

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

        TutorApplication.getInstance().addToRequestQueue(jsonArrayRequest);
    }


    private class PostAdapter extends RecyclerView.Adapter<PostAdapter.MyViewHolder> {
        JSONArray jsonArray;

        public PostAdapter(JSONArray array) {
            this.jsonArray = array;
        }

        public void append(JSONArray array) {
            try {
                for (int i = 0; i < array.length(); i++) {
                    this.jsonArray.put(array.get(i));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public PostAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.history_list_item, parent, false);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(PostAdapter.MyViewHolder holder, int position) {
            Glide.with(activity).load(jsonArray.optJSONObject(position).optString("static_map")).placeholder(R.drawable.placeholder).error(R.drawable.placeholder).into(holder.tripImg);
            if (jsonArray.optJSONObject(position).optJSONObject("payment") != null) {
                if (jsonArray.optJSONObject(position).optJSONObject("payment").optInt("total")==0) {
                    JSONObject paymentObj = jsonArray.optJSONObject(position).optJSONObject("payment");
                    int fixed = paymentObj.optInt("fixed");
                    int distance = paymentObj.optInt("distance");
                    int tax = paymentObj.optInt("tax");
                    int total = fixed + distance + tax;
                    holder.tripAmount.setText(SharedHelper.getKey(context, "currency") + "" + total);
                } else
                holder.tripAmount.setText(SharedHelper.getKey(context, "currency") + "" + jsonArray.optJSONObject(position).optJSONObject("payment").optString("total"));
            } else {
                holder.tripAmount.setText(SharedHelper.getKey(context, "currency") + "0.00");
            }
            if (jsonArray.optJSONObject(position).optJSONObject("user") != null) {
                if (!jsonArray.optJSONObject(position).optJSONObject("user").optString("picture").equalsIgnoreCase("")) {
                    Glide.with(activity).load(AppHelper.getImageUrl(jsonArray.optJSONObject(position).optJSONObject("user").optString("picture"))).placeholder(R.drawable.ic_dummy_user).error(R.drawable.ic_dummy_user).into(holder.driver_image);
                }
            }

            if (jsonArray.optJSONObject(position).optJSONObject("service_type")!=null)
            holder.serviceType.setText(jsonArray.optJSONObject(position).optJSONObject("service_type").optString("name"));
            holder.updateMapContents(new LatLng(jsonArray.optJSONObject(position).optDouble("s_latitude"),
                    jsonArray.optJSONObject(position).optDouble("s_longitude")));

            try {
                if (!jsonArray.optJSONObject(position).optString("assigned_at", "").isEmpty()) {
                    String form = jsonArray.optJSONObject(position).optString("assigned_at");
                    try {
                        holder.tripDate.setText(getDate(form) + " " + getMonth(form) + " " + getYear(form) + " at " + getTime(form));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            return jsonArray.length();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder implements OnMapReadyCallback {

            TextView tripDate, serviceType, tripAmount;
            ImageView tripImg, driver_image;
            private SupportMapFragment mapFragment;
            private GoogleMap mMap;
            public MapView mapView;
            Context context;
            LatLng latLng;


            protected void updateMapContents(LatLng latLng) {
                this.latLng = latLng;
                if (mMap != null) {
                    // Since the mapView is re-used, need to remove pre-existing mapView features.
                    mMap.clear();

                    MarkerOptions markerOptions = new MarkerOptions()
                            .position(latLng)
                            .rotation(0.0f)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.user_marker));

                    // Update the mapView feature data and camera position.
                    mMap.addMarker(markerOptions);

                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 14.0f);
                    mMap.moveCamera(cameraUpdate);
                }
            }


            public MyViewHolder(View itemView) {
                super(itemView);
                tripDate = (TextView) itemView.findViewById(R.id.tripDate);
                tripAmount = (TextView) itemView.findViewById(R.id.tripAmount);
                tripImg = (ImageView) itemView.findViewById(R.id.tripImg);
                serviceType = (TextView) itemView.findViewById(R.id.car_name);
                driver_image = (ImageView) itemView.findViewById(R.id.driver_image);
                mapView = (MapView) itemView.findViewById(R.id.provider_map);
                this.context = PastTrips.context;
                mapView.onCreate(null);
                mapView.getMapAsync(this);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(activity, HistoryDetails.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("post_value", jsonArray.optJSONObject(getAdapterPosition()).toString());
                        intent.putExtra("tag", "past_trips");
                        activity.startActivity(intent);
                    }
                });

            }

            @Override
            public void onMapReady(GoogleMap googleMap) {

                mMap = googleMap;
                MapsInitializer.initialize(context);
                mMap.getUiSettings().setMapToolbarEnabled(false);

                try {
                    googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(context, R.raw.style_json));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (latLng != null) {
                    updateMapContents(latLng);
                }
            }
        }

        private String getMonth(String date) throws ParseException {
            Date d = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).parse(date);
            Calendar cal = Calendar.getInstance();
            cal.setTime(d);
            String monthName = new SimpleDateFormat("MMM").format(cal.getTime());
            return monthName;
        }

        private String getDate(String date) throws ParseException {
            Date d = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).parse(date);
            Calendar cal = Calendar.getInstance();
            cal.setTime(d);
            String dateName = new SimpleDateFormat("dd").format(cal.getTime());
            return dateName;
        }

        private String getYear(String date) throws ParseException {
            Date d = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).parse(date);
            Calendar cal = Calendar.getInstance();
            cal.setTime(d);
            String yearName = new SimpleDateFormat("yyyy").format(cal.getTime());
            return yearName;
        }

        private String getTime(String date) throws ParseException {
            Date d = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).parse(date);
            Calendar cal = Calendar.getInstance();
            cal.setTime(d);
            String timeName = new SimpleDateFormat("hh:mm a").format(cal.getTime());
            String str = timeName.replace("a.m", "AM").replace("p.m", "PM");
            return str;
        }
    }

    public void findViewByIdAndInitialize() {
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);
        errorLayout = (RelativeLayout) rootView.findViewById(R.id.errorLayout);
        lnrList = (LinearLayout) rootView.findViewById(R.id.lnrList);

        backArrow = (ImageView) rootView.findViewById(R.id.backArrow);
        toolbar = (LinearLayout) rootView.findViewById(R.id.toolbar);

        errorLayout.setVisibility(View.GONE);
        helper = new ConnectionHelper(activity);
        isInternet = helper.isConnectingToInternet();

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStack();
            }
        });

    }

    @Override
    public void onDetach() {
        super.onDetach();
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

    public void GoToBeginActivity() {
        Toast.makeText(context, getString(R.string.session_timeout), Toast.LENGTH_SHORT).show();
        SharedHelper.putKey(context, "current_status", "");
        SharedHelper.putKey(activity, "loggedIn", getString(R.string.False));
        Intent mainIntent = new Intent(activity, ActivityPassword.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(mainIntent);
        activity.finish();
    }

    public interface PastTripsListener {
        public void moveToServiceFlowFgmt();
    }


    @Override
    public void onResume() {
        super.onResume();
    }
}
