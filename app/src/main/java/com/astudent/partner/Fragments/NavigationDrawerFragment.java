package com.astudent.partner.Fragments;


import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;
import com.astudent.partner.Activity.ActivityPassword;
import com.astudent.partner.Helper.SharedHelper;
import com.astudent.partner.Listener.NavUpdateListener;
import com.astudent.partner.Models.NavMenu;
import com.astudent.partner.R;
import com.astudent.partner.Utils.Utilities;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * NavigationDrawerFragment used in Admi Mode.
 * usage : used to navigate between screens within the App.
 * the listener is implement.
 * Dealer Navigation Drawer
 */
public class NavigationDrawerFragment extends Fragment implements View.OnClickListener, NavUpdateListener {

    public static final String TAG = "NavDrawerFgmt";

    private CircleImageView mUserProfileImg;
    private TextView mNameTxt;
    private TextView mEmailTxt;

    private Button imgprofile_btn, homeBtn, subsettingsBtn,languagebtn, share_btn, sharetocontact_btn, logoutBtn, historyBtn, earningsBtn, summaryBtn, helpBtn, City_btn, revolution_btn, legal;

    private RelativeLayout headerLayout;
    private ProgressBar mImgProgressBar;
    // private RelativeLayout headerLayout;

    private NavMenu mNavMenuItems = NavMenu.HOME;

    private NavDrawerFgmtListener mListener;
    private DrawerLayout mDrawerLayout;
    private View mNavigationView;

    public NavigationDrawerFragment() {
    }

    public static NavigationDrawerFragment newInstance() {
        NavigationDrawerFragment fragment = new NavigationDrawerFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof NavDrawerFgmtListener) {
            mListener = (NavDrawerFgmtListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    private void setBtnDrawables() {
        Drawable img = ContextCompat.getDrawable(getContext(), R.drawable.userplaceholder);
        img.setBounds(0, 0, 35, 35);
        img.setColorFilter(ContextCompat.getColor(getContext(), R.color.colorPrimaryDark), PorterDuff.Mode.SRC_ATOP);
        imgprofile_btn.setCompoundDrawables(img, null, null, null);
        img = ContextCompat.getDrawable(getContext(), R.drawable.home);
        img.setBounds(0, 0, 35, 35);
        img.setColorFilter(ContextCompat.getColor(getContext(), R.color.colorPrimaryDark), PorterDuff.Mode.SRC_ATOP);
        homeBtn.setCompoundDrawables(img, null, null, null);
        img = ContextCompat.getDrawable(getContext(), R.drawable.service_history);
        img.setBounds(0, 0, 35, 35);
        img.setColorFilter(ContextCompat.getColor(getContext(), R.color.colorPrimaryDark), PorterDuff.Mode.SRC_ATOP);
        historyBtn.setCompoundDrawables(img, null, null, null);
        img = ContextCompat.getDrawable(getContext(), R.drawable.settings);
        img.setBounds(0, 0, 35, 35);
        img.setColorFilter(ContextCompat.getColor(getContext(), R.color.colorPrimaryDark), PorterDuff.Mode.SRC_ATOP);
        subsettingsBtn.setCompoundDrawables(img, null, null, null);

        img = ContextCompat.getDrawable(getContext(), R.drawable.settings);
        img.setBounds(0, 0, 35, 35);
        img.setColorFilter(ContextCompat.getColor(getContext(), R.color.colorPrimaryDark), PorterDuff.Mode.SRC_ATOP);
        languagebtn.setCompoundDrawables(img, null, null, null);

        img = ContextCompat.getDrawable(getContext(), R.drawable.share);
        img.setBounds(0, 0, 35, 35);
        img.setColorFilter(ContextCompat.getColor(getContext(), R.color.colorPrimaryDark), PorterDuff.Mode.SRC_ATOP);
        share_btn.setCompoundDrawables(img, null, null, null);
        img = ContextCompat.getDrawable(getContext(), R.drawable.share);
        img.setBounds(0, 0, 35, 35);
        img.setColorFilter(ContextCompat.getColor(getContext(), R.color.colorPrimaryDark), PorterDuff.Mode.SRC_ATOP);
        sharetocontact_btn.setCompoundDrawables(img, null, null, null);
        img = ContextCompat.getDrawable(getContext(), R.drawable.logout);
        img.setBounds(0, 0, 35, 35);
        img.setColorFilter(ContextCompat.getColor(getContext(), R.color.colorPrimaryDark), PorterDuff.Mode.SRC_ATOP);
        logoutBtn.setCompoundDrawables(img, null, null, null);
        img = ContextCompat.getDrawable(getContext(), R.drawable.about);
        img.setBounds(0, 0, 35, 35);
        img.setColorFilter(ContextCompat.getColor(getContext(), R.color.colorPrimaryDark), PorterDuff.Mode.SRC_ATOP);
        City_btn.setCompoundDrawables(img, null, null, null);
        img = ContextCompat.getDrawable(getContext(), R.drawable.emergecy_contact);
        img.setBounds(0, 0, 35, 35);
        img.setColorFilter(ContextCompat.getColor(getContext(), R.color.colorPrimaryDark), PorterDuff.Mode.SRC_ATOP);
        revolution_btn.setCompoundDrawables(img, null, null, null);
        img = ContextCompat.getDrawable(getContext(), R.drawable.help_select);
        img.setBounds(0, 0, 35, 35);
        img.setColorFilter(ContextCompat.getColor(getContext(), R.color.colorPrimaryDark), PorterDuff.Mode.SRC_ATOP);
        helpBtn.setCompoundDrawables(img, null, null, null);
        img = ContextCompat.getDrawable(getContext(), R.drawable.summary_select);
        img.setBounds(0, 0, 35, 35);
        img.setColorFilter(ContextCompat.getColor(getContext(), R.color.colorPrimaryDark), PorterDuff.Mode.SRC_ATOP);
        summaryBtn.setCompoundDrawables(img, null, null, null);
        img = ContextCompat.getDrawable(getContext(), R.drawable.earnings_select);
        img.setBounds(0, 0, 35, 35);
        img.setColorFilter(ContextCompat.getColor(getContext(), R.color.colorPrimaryDark), PorterDuff.Mode.SRC_ATOP);
        earningsBtn.setCompoundDrawables(img, null, null, null);
    }


    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_navigation_drawer, container, false);
        findViewsById(view);
        setClickListeners();
        setBasicDetails();
        if (savedInstanceState == null)
            setBtnStates(homeBtn);
        else
            checkBtnStates();
        setBtnDrawables();
        if (SharedHelper.getKey(getContext(), Utilities.skip_login).equalsIgnoreCase(Utilities.skipped)) {
            imgprofile_btn.setVisibility(View.GONE);
            homeBtn.setVisibility(View.GONE);
            historyBtn.setVisibility(View.GONE);
            earningsBtn.setVisibility(View.GONE);
            summaryBtn.setVisibility(View.GONE);
            helpBtn.setVisibility(View.GONE);
            share_btn.setVisibility(View.GONE);
            sharetocontact_btn.setVisibility(View.GONE);
            logoutBtn.setText("Exit");
        } else {
            imgprofile_btn.setVisibility(View.VISIBLE);
            homeBtn.setVisibility(View.VISIBLE);
            historyBtn.setVisibility(View.VISIBLE);
            earningsBtn.setVisibility(View.VISIBLE);
            summaryBtn.setVisibility(View.VISIBLE);
            helpBtn.setVisibility(View.GONE);
            share_btn.setVisibility(View.GONE);
            sharetocontact_btn.setVisibility(View.GONE);
            logoutBtn.setVisibility(View.VISIBLE);
        }
        return view;
    }


    private void findViewsById(View view) {
        mUserProfileImg = (CircleImageView) view.findViewById(R.id.circleView);
        mNameTxt = (TextView) view.findViewById(R.id.name);
        if (SharedHelper.getKey(getContext(), Utilities.skip_login).equalsIgnoreCase(Utilities.skipped)) {
            mNameTxt.setText("Guest Provider");
            if (!SharedHelper.getKey(getContext(), "picture").equalsIgnoreCase("")) {
                Picasso.with(getContext()).load(R.drawable.ic_dummy_user)
                        .error(R.drawable.ic_dummy_user).memoryPolicy(MemoryPolicy.NO_CACHE)
                        .placeholder(R.drawable.ic_dummy_user)
                        .centerCrop()
                        .fit()
                        .into(mUserProfileImg);
            }
        } else {
            String strName = SharedHelper.getKey(getContext(), "first_name") + " " + SharedHelper.getKey(getContext(), "last_name");
            mNameTxt.setText(strName);

            if (!SharedHelper.getKey(getContext(), "picture").equalsIgnoreCase("")) {
                Picasso.with(getContext()).load(SharedHelper.getKey(getContext(), "picture"))
                        .error(R.drawable.ic_dummy_user)
                        .placeholder(R.drawable.ic_dummy_user)
                        .centerCrop().memoryPolicy(MemoryPolicy.NO_CACHE)
                        .fit()
                        .into(mUserProfileImg);
            }
        }
        imgprofile_btn = (Button) view.findViewById(R.id.imgprofile_btn);
        homeBtn = (Button) view.findViewById(R.id.home_btn);
        subsettingsBtn = (Button) view.findViewById(R.id.sub_settings_btn);
        languagebtn = (Button) view.findViewById(R.id.language_btn);
        sharetocontact_btn = (Button) view.findViewById(R.id.contact_btn);
        share_btn = (Button) view.findViewById(R.id.share_btn);
        logoutBtn = (Button) view.findViewById(R.id.logout_btn);
        historyBtn = (Button) view.findViewById(R.id.history_btn);
        earningsBtn = (Button) view.findViewById(R.id.earnings_btn);
        City_btn = (Button) view.findViewById(R.id.city_btn);
        legal = (Button) view.findViewById(R.id.legal_btn);
        revolution_btn = (Button) view.findViewById(R.id.revolution_btn);
        summaryBtn = (Button) view.findViewById(R.id.summary_btn);
        helpBtn = (Button) view.findViewById(R.id.help_btn);
        headerLayout = (RelativeLayout) view.findViewById(R.id.navigation_header);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUserProfileImg = null;
        mNameTxt = null;
        mEmailTxt = null;
        homeBtn = null;
        subsettingsBtn = null;
        languagebtn = null;
        imgprofile_btn = null;
        sharetocontact_btn = null;
        share_btn = null;
        logoutBtn = null;
        historyBtn = null;
        headerLayout = null;
        //mImgProgressBar = null;
    }

    private void setClickListeners() {
        homeBtn.setOnClickListener(this);
        subsettingsBtn.setOnClickListener(this);
        languagebtn.setOnClickListener(this);
        sharetocontact_btn.setOnClickListener(this);
        share_btn.setOnClickListener(this);
        logoutBtn.setOnClickListener(this);
        historyBtn.setOnClickListener(this);
        headerLayout.setOnClickListener(this);
        imgprofile_btn.setOnClickListener(this);
        summaryBtn.setOnClickListener(this);
        helpBtn.setOnClickListener(this);
        City_btn.setOnClickListener(this);
        revolution_btn.setOnClickListener(this);
        legal.setOnClickListener(this);
        earningsBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (isDrawerOpen())
            closeDrawer();
        if (v == homeBtn) {
            setBtnStates(homeBtn);
            mNavMenuItems = NavMenu.HOME;
        } else if (v == imgprofile_btn) {
            if (SharedHelper.getKey(getContext(), Utilities.skip_login).equalsIgnoreCase(Utilities.skipped)) {
                closeDrawer();
                Toast.makeText(getContext(), "Please login/signin to continue", Toast.LENGTH_SHORT).show();
            } else {
                setBtnStates(imgprofile_btn);
                mNavMenuItems = NavMenu.PROFILE;
            }
        } else if (v == subsettingsBtn) {
            /*if(SharedHelper.getKey(getContext(),Utilities.skip_login).equalsIgnoreCase(Utilities.skipped)) {
                closeDrawer();
                Toast.makeText(getContext(), "Please login/signin to continue", Toast.LENGTH_SHORT).show();
            } else {

            }*/
            setBtnStates(homeBtn);
            mNavMenuItems = NavMenu.SETTINGS;
        } else if (v == languagebtn) {
            /*if(SharedHelper.getKey(getContext(),Utilities.skip_login).equalsIgnoreCase(Utilities.skipped)) {
                closeDrawer();
                Toast.makeText(getContext(), "Please login/signin to continue", Toast.LENGTH_SHORT).show();
            } else {

            }*/
            setBtnStates(languagebtn);
            mNavMenuItems = NavMenu.LANGUAGE;
        } else if (v == share_btn) {
//            setBtnStates(share_btn);
            mNavMenuItems = NavMenu.SHARE;
        } else if (v == sharetocontact_btn) {
            setBtnStates(share_btn);
            mNavMenuItems = NavMenu.CONTACT_SHARE;
        } else if (v == logoutBtn) {
            if (SharedHelper.getKey(getContext(), Utilities.skip_login).equalsIgnoreCase(Utilities.skipped)) {
                closeDrawer();
                SharedHelper.putKey(getContext(), "current_status", "");
                SharedHelper.putKey(getContext(), Utilities.skip_login, "");
                SharedHelper.putKey(getContext(), "loggedIn", getString(R.string.False));
                SharedHelper.putKey(getContext(), "login_by", "");
                Intent mainIntent = new Intent(getContext(), ActivityPassword.class);
                mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(mainIntent);
                getActivity().finish();
            } else {
                mNavMenuItems = NavMenu.LOGOUT;
            }
        } else if (v == historyBtn) {
            if (SharedHelper.getKey(getContext(), Utilities.skip_login).equalsIgnoreCase(Utilities.skipped)) {
                closeDrawer();
                Toast.makeText(getContext(), "Please login/signin to continue", Toast.LENGTH_SHORT).show();
            } else {
                setBtnStates(historyBtn);
                mNavMenuItems = NavMenu.HISTORY;
            }
        } else if (v == helpBtn) {
            setBtnStates(historyBtn);
            mNavMenuItems = NavMenu.HELP;
        } else if (v == City_btn) {
            setBtnStates(City_btn);
            mNavMenuItems = NavMenu.CITIES;
        } else if (v == revolution_btn) {
            setBtnStates(revolution_btn);
            mNavMenuItems = NavMenu.REVOLUTION;
        } else if (v == legal) {
            setBtnStates(legal);
            mNavMenuItems = NavMenu.LEGAL;
        } else if (v == summaryBtn) {
            if (SharedHelper.getKey(getContext(), Utilities.skip_login).equalsIgnoreCase(Utilities.skipped)) {
                closeDrawer();
                Toast.makeText(getContext(), "Please login/signin to continue", Toast.LENGTH_SHORT).show();
            } else {
                setBtnStates(historyBtn);
                mNavMenuItems = NavMenu.SUMMARY;
            }
        } else if (v == earningsBtn) {
            if (SharedHelper.getKey(getContext(), Utilities.skip_login).equalsIgnoreCase(Utilities.skipped)) {
                closeDrawer();
                Toast.makeText(getContext(), "Please login/signin to continue", Toast.LENGTH_SHORT).show();
            } else {
                setBtnStates(historyBtn);
                mNavMenuItems = NavMenu.EARNINGS;
            }
        }
        if (v == headerLayout) {
            if (SharedHelper.getKey(getContext(), "skip_login").equalsIgnoreCase("skipped")) {
                Toast.makeText(getContext(), "Please login/signin to continue", Toast.LENGTH_SHORT).show();
            } else {
                mListener.headerClicked();
                mNavMenuItems = null;
            }
        }
        if (mNavMenuItems != null && mListener != null)
            mListener.menuClicked(mNavMenuItems);
    }

    public boolean isDrawerOpen() {
        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(mNavigationView);
    }

    public void closeDrawer() {
        if (mDrawerLayout != null && isDrawerOpen()) {
            mDrawerLayout.closeDrawer(mNavigationView);
        }
    }

    public void openDrawer() {
        if (mDrawerLayout != null && !isDrawerOpen()) {
            mDrawerLayout.openDrawer(mNavigationView);
        }
    }

    public void setupDrawer(int fragmentId, final DrawerLayout drawerLayout) {
        mDrawerLayout = drawerLayout;
        mNavigationView = getActivity().findViewById(fragmentId);
        mListener.menuClicked(NavMenu.HOME);
    }

    public void checkBtnStates() {
        if (mNavMenuItems != null) {
            if (mNavMenuItems.equals(NavMenu.HOME)) {
                setBtnStates(homeBtn);
            }
            if (mNavMenuItems.equals(NavMenu.SETTINGS)) {
                setBtnStates(subsettingsBtn);
            } if (mNavMenuItems.equals(NavMenu.LANGUAGE)) {
                setBtnStates(languagebtn);
            }
            if (mNavMenuItems.equals(NavMenu.SHARE)) {
                setBtnStates(share_btn);
            }
            if (mNavMenuItems.equals(NavMenu.HISTORY)) {
                setBtnStates(historyBtn);
            }
            if (mNavMenuItems.equals(NavMenu.LOGOUT)) {
                setBtnStates(logoutBtn);
            }
        }
    }

    private void setBtnStates(Button button) {
        homeBtn.setSelected(button == homeBtn);
        subsettingsBtn.setSelected(button == subsettingsBtn);
        languagebtn.setSelected(button == languagebtn);
        sharetocontact_btn.setSelected(button == share_btn);
        historyBtn.setSelected(button == historyBtn);
        logoutBtn.setSelected(button == logoutBtn);
    }

    public void enableDisableDrawer(boolean isEnable) {
        if (mDrawerLayout != null) {
            int lockMode = (isEnable) ? DrawerLayout.LOCK_MODE_UNLOCKED : DrawerLayout.LOCK_MODE_LOCKED_CLOSED;
            mDrawerLayout.setDrawerLockMode(lockMode);
        }
        checkBtnStates();
    }

    public void setNavMenuItems(NavMenu navMenuItems) {
        this.mNavMenuItems = navMenuItems;
        checkBtnStates();
    }

    public void setBasicDetails() {
       /* String name = String.format(getContext().getString(R.string.name), SharedHelper.getKey(getContext(), "first_name"),
                SharedHelper.getKey(getContext(), "last_name"));
        mNameTxt.setText(name);
        String gender = SharedHelper.getKey(getContext(), "gender");
        if (gender != null && !gender.equalsIgnoreCase("null")) {
            if (gender.equalsIgnoreCase("male")) {
                Picasso.with(getContext()).load(SharedHelper.getKey(getContext(), "avatar"))
                        .error(R.drawable.man_user)
                        .centerCrop()
                        .fit()
                        .into(mUserProfileImg);
            } else if (gender.equalsIgnoreCase("female")) {
                Picasso.with(getContext()).load(SharedHelper.getKey(getContext(), "avatar"))
                        .error(R.drawable.woman_user)
                        .centerCrop()
                        .fit()
                        .into(mUserProfileImg);
            } else {
                Picasso.with(getContext()).load(SharedHelper.getKey(getContext(), "avatar"))
                        .error(R.drawable.man_user)
                        .centerCrop()
                        .fit()
                        .into(mUserProfileImg);
            }
        }*/
    }

    @Override
    public void onProfileUpdateReflect() {
        setBasicDetails();
    }

    public interface NavDrawerFgmtListener {
        void menuClicked(NavMenu navMenuItems);

        void headerClicked();

        void headerProfileClicked();
    }
}
