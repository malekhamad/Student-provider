package com.astudent.partner.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.snackbar.Snackbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.astudent.partner.Activity.Home;
import com.astudent.partner.R;


public class ActivityCities extends Fragment {
    public static final String TAG = "ActivityCities";
    Activity activity;
    Context context;
    Boolean isInternet;
    RecyclerView recyclerView;
    RelativeLayout errorLayout;
    LinearLayout lnrList;
    View rootView;
    ImageView backArrow;


    public ActivityCities() {
        // Required empty public constructor
    }


    public static ActivityCities newInstance() {
        ActivityCities fragment = new ActivityCities();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_cities, container, false);
        findViewByIdAndInitialize();

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GoToMainActivity();
            }
        });


        return rootView;
    }

    public void GoToMainActivity(){
        Intent mainIntent = new Intent(activity, Home.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(mainIntent);
        activity.finish();
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

    public void findViewByIdAndInitialize() {
//        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);
//        errorLayout = (RelativeLayout) rootView.findViewById(R.id.errorLayout);
//        lnrList = (LinearLayout) rootView.findViewById(R.id.lnrList);
        backArrow = (ImageView) rootView.findViewById(R.id.backArrow);
//        errorLayout.setVisibility(View.GONE);
//        helper = new ConnectionHelper(getActivity());
//        isInternet = helper.isConnectingToInternet();

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


    public void displayMessage(String toastString) {
        try {
            Snackbar.make(getView(), toastString, Snackbar.LENGTH_SHORT)
                    .setAction("Action", null).show();
        } catch (Exception e) {
            try {
                Toast.makeText(context, "" + toastString, Toast.LENGTH_SHORT).show();
            } catch (Exception ee) {
                ee.printStackTrace();
            }
        }

    }


    public void GoToBeginActivity() {
        Toast.makeText(getContext(), getString(R.string.session_timeout), Toast.LENGTH_SHORT).show();
        Intent mainIntent = new Intent(activity, ActivityCities.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(mainIntent);
        activity.finish();
    }

    @Override
    public void onResume() {
        super.onResume();
    }


}
