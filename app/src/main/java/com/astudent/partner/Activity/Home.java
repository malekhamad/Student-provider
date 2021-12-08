package com.astudent.partner.Activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.snackbar.Snackbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.astudent.partner.About_Us_Activity;
import com.astudent.partner.Constant.URLHelper;
import com.astudent.partner.Contactt_Us_Activity;
import com.astudent.partner.Fragments.ContactShareFragment;
import com.astudent.partner.Fragments.EarningsFragment;
import com.astudent.partner.Fragments.Help;
import com.astudent.partner.Fragments.NavigationDrawerFragment;
import com.astudent.partner.Fragments.Offline;
import com.astudent.partner.Fragments.PastTrips;
import com.astudent.partner.Fragments.ServiceFlowFragment;
import com.astudent.partner.Fragments.SettingsFragment;
import com.astudent.partner.Fragments.SummaryFragment;
import com.astudent.partner.Helper.CustomDialog;
import com.astudent.partner.Helper.SharedHelper;
import com.astudent.partner.Listener.NavigationCallBack;
import com.astudent.partner.Models.NavMenu;
import com.astudent.partner.R;
import com.astudent.partner.Utils.KeyHelper;
import com.astudent.partner.Utils.Keyname;
import com.astudent.partner.Utils.Utilities;
import com.astudent.partner.TutorApplication;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.astudent.partner.TutorApplication.trimMessage;

public class Home extends AppCompatActivity implements NavigationDrawerFragment.NavDrawerFgmtListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, Offline.OfflineFgmtListener,
        NavigationCallBack, ServiceFlowFragment.ServiceFlowFgmtListener, SettingsFragment.SettingsFgmtListener,
        PastTrips.PastTripsListener,
        EarningsFragment.EarningsFragmentInterface,
        SummaryFragment.SummaryFragmentInterface, Help.HelpFragmentInterface {


    public static String TAG = "Home";
    Context context = Home.this;
    Activity activity = Home.this;
    NavigationDrawerFragment mNavigationDrawerFragment;
    Utilities utils = new Utilities();
    GoogleApiClient mGoogleApiClient;
    String current_lat = "", current_lng = "";
    CustomDialog loadingDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        Log.e(TAG, "access_token: "+SharedHelper.getKey(context, "access_token") );

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
            getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
        }
        setContentView(R.layout.home);

        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }


        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);

        if (mNavigationDrawerFragment != null)
            mNavigationDrawerFragment.setupDrawer(R.id.navigation_drawer, (DrawerLayout) findViewById(R.id
                    .drawer_layout));

        ServiceFlowFragment fragment = ServiceFlowFragment.newInstance();
        switchContent(fragment, ServiceFlowFragment.TAG);
//      getServices();
    }

    public void switchContent(Fragment fragment, String tag) {
        FragmentManager fragmentManager = getSupportFragmentManager();
//        fragmentManager.popBackStack();
        if (fragment != null) {
            FragmentTransaction transaction = fragmentManager
                    .beginTransaction();
            transaction.replace(R.id.container, fragment, tag);
            if (!(fragment instanceof ServiceFlowFragment))
                transaction.addToBackStack(tag);
            transaction.commit();
        }
    }

    private void showLogoutDialog() {
        if (!isFinishing()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setIcon(R.mipmap.ic_launcher);
            builder.setTitle(getString(R.string.app_name));
            builder.setMessage(getString(R.string.exit_confirm));
            builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if(SharedHelper.getKey(getApplicationContext(),Utilities.skip_login).equalsIgnoreCase(Utilities.skipped)) {
                        appLogout();
                    } else {
                        logout();
                    }
                }
            });
            builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //Reset to previous seletion menu in navigation
                    dialog.dismiss();
                }
            });

            builder.setCancelable(false);
            final AlertDialog dialog = builder.create();
            //dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
            dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface arg) {
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
                    dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
                }
            });
            dialog.show();
        }
    }

    public void logout() {
        final CustomDialog customDialog = new CustomDialog(context);
        customDialog.setCancelable(false);
        JSONObject object = new JSONObject();
        customDialog.show();
        try {
            object.put("id", SharedHelper.getKey(context, "id"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLHelper.LOGOUT, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                customDialog.dismiss();
                if (SharedHelper.getKey(context,"login_by").equals("facebook")) {
                    LoginManager.getInstance().logOut();
                }else {

                }
                appLogout();
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
                                displayMessage(errorObj.getString("message"));
                            } catch (Exception e) {
                                displayMessage(getString(R.string.something_went_wrong));
                            }
                        } else if (response.statusCode == 401) {
                                /*refreshAccessToken();*/
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

                }
            }
        }) {
            @Override
            public java.util.Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("X-Requested-With", "XMLHttpRequest");
                headers.put("Authorization", "" + "Bearer" + " " + SharedHelper.getKey(context, "access_token"));
                return headers;
            }
        };

        TutorApplication.getInstance().addToRequestQueue(jsonObjectRequest);
    }

    public void displayMessage(String toastString) {
        try {
            Snackbar snackbar = Snackbar.make(getCurrentFocus(), toastString, Snackbar.LENGTH_SHORT);
            View snackBarView = snackbar.getView();
            snackBarView.setBackgroundColor(ContextCompat.getColor(context, R.color.black));
            TextView textView = (TextView) snackBarView.findViewById(R.id.snackbar_text);
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


    private void appLogout() {
        SharedHelper.putKey(context, "current_status", "");
        SharedHelper.putKey(context, Utilities.skip_login, "");
        SharedHelper.putKey(context, "loggedIn", getString(R.string.False));
        SharedHelper.putKey(context,"login_by","");
        Intent mainIntent = new Intent(context, ActivityPassword.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(mainIntent);
        activity.finish();

    }

    @Override
    public void onServiceFlowLogout() {
        showLogoutDialog();
    }

    @Override
    public void moveToOfflineFragment() {
        Offline fragment = Offline.newInstance();
        switchContent(fragment, Offline.TAG);
    }

    @Override
    public void moveToChatFragment() {
       Intent intent = new Intent(this,ChatActivity.class);
        startActivity(intent);
    }

    @Override
    public void handleDrawer() {
        if (mNavigationDrawerFragment != null) {
            if (mNavigationDrawerFragment.isDrawerOpen()) {
                mNavigationDrawerFragment.closeDrawer();
            } else {
                mNavigationDrawerFragment.openDrawer();
            }
        }
    }

    @Override
    public void menuClicked(NavMenu navMenu) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = null;
        Fragment currentFgmt = fragmentManager.findFragmentById(R.id.container);
        utils.print(TAG, "menuClicked: CurrentFgmt" + currentFgmt);
        String tag = "";
        switch (navMenu) {
            case HOME:
                fragment = fragmentManager.findFragmentByTag(ServiceFlowFragment.TAG);
                tag = ServiceFlowFragment.TAG;
                if (fragment == null) {
                    utils.print(TAG, "ServiceFlowFragment fragment");
                    fragment = ServiceFlowFragment.newInstance();
                } else {
                    utils.print(TAG, "home fragment, no need to add");
                }
                break;
            case SETTINGS:
                fragment = fragmentManager.findFragmentByTag(SettingsFragment.TAG);
                tag = SettingsFragment.TAG;
                if (fragment == null) {
                    utils.print(TAG, "Payment fragment");
                    fragment = SettingsFragment.newInstance();
                } else {
                    utils.print(TAG, "home fragment, no need to add");
                }
                break;

            case LANGUAGE:
                tag = null;

                Intent contact = new Intent(context,SettingsActivity.class);
                startActivity(contact);
                break;
            case SHARE:
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, URLHelper.APP_LINK + " -via " + getString(R.string.app_name));
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
                break;
            case CONTACT_SHARE:
                fragment = fragmentManager.findFragmentByTag(ContactShareFragment.TAG);
                tag = ContactShareFragment.TAG;
                if (fragment == null) {
                    fragment = new ContactShareFragment();
                } else {
                    utils.print(TAG, "ContactShare fragment, no need to add");
                }
                break;
            case HISTORY:
                startActivity(new Intent(context, HistoryActivity.class));
                break;
            case SUMMARY:
                fragment = fragmentManager.findFragmentByTag(SummaryFragment.TAG);
                tag = SummaryFragment.TAG;
                if (fragment == null) {
                    fragment = new SummaryFragment();
                } else {
                    utils.print(TAG, "Summary fragment, no need to add");
                }
                break;
            case HELP:
                /*fragment = fragmentManager.findFragmentByTag(Help.TAG);
                tag = Help.TAG;
                if (fragment == null) {
                    fragment = new Help();
                } else {
                    utils.print(TAG, "Summary fragment, no need to add");
                }*/

                startActivity(new Intent(context, HelpActivity.class));

                break;
            case LEGAL:
                Toast.makeText(context,"Legal",Toast.LENGTH_SHORT).show();
                break;
            case REVOLUTION:
//                Toast.makeText(context,"REVOLUTION",Toast.LENGTH_SHORT).show();

//                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(URLHelper.REVOLUTION_URL));

//                startActivity(browserIntent);

                startActivity(new Intent(context, Contactt_Us_Activity.class));

                break;
            case PROFILE:
                headerClicked();
            break;
            case CITIES:
                /*Toast.makeText(context,"CITIES",Toast.LENGTH_SHORT).show();
//                fragment = fragmentManager.findFragmentByTag(ActivityCities.TAG);
//                tag = ActivityCities.TAG;
//                if (fragment == null) {
//                    fragment = new ActivityCities();
//                } else {
//                    utils.print(TAG, "ActivityCities, no need to add");
                }*/

                startActivity(new Intent(context, About_Us_Activity.class));


                break;
            case EARNINGS:
                fragment = fragmentManager.findFragmentByTag(EarningsFragment.TAG);
                tag = EarningsFragment.TAG;
                if (fragment == null) {
                    fragment = new EarningsFragment();
                } else {
                    utils.print(TAG, "EarningsFragment, no need to add");
                }
                break;
            case LOGOUT:
                showLogoutDialog();
                break;
        }

        utils.print(TAG, "menuClicked: 1" + fragment);
        if (tag != null) {
            Fragment existingFragment = getSupportFragmentManager().findFragmentById(R.id.container);
            if (existingFragment != null) {
                if (existingFragment.getTag() != null) {
                    if (!(existingFragment.getTag().equals(tag)))
                        switchContent(fragment, tag);
                }
            } else
                switchContent(fragment, tag);
        }
    }

    @Override
    public void headerClicked() {
        Intent intent = new Intent(this, EditProfile.class);
        intent.putExtra(Keyname.EDIT_PROFILE, true);
        startActivity(intent);
    }

    @Override
    public void headerProfileClicked() {

    }

    @Override
    public void handleNavigationDrawer() {
        if (mNavigationDrawerFragment != null) {
            if (mNavigationDrawerFragment.isDrawerOpen()) {
                mNavigationDrawerFragment.closeDrawer();
            } else {
                mNavigationDrawerFragment.openDrawer();
            }
        }
    }

    @Override
    public void enableDisableNavigationDrawer(boolean isEnable) {
        if (mNavigationDrawerFragment != null) {
            mNavigationDrawerFragment.enableDisableDrawer(isEnable);
        }
    }

    public void addFragment(Fragment fragment, String tag, boolean popBackStack, boolean hideCurrent, boolean addToBackStack) {
        FragmentManager fragmentManager = getSupportFragmentManager();

        if (fragment != null) {
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.add(R.id.container, fragment, tag);
            if (popBackStack) {
                fragmentManager.popBackStack();
            }
            if (hideCurrent) {
                if (fragmentManager.getBackStackEntryCount() > 0) {
                    String fragmentTag = fragmentManager.getBackStackEntryAt(fragmentManager.getBackStackEntryCount()
                            - 1).getName();
                    transaction.hide(fragmentManager.findFragmentByTag(fragmentTag));
                } else {
                    Fragment currentFgmt = fragmentManager.findFragmentById(R.id.container);
                    if (currentFgmt != null) {
                        transaction.hide(currentFgmt);
                    }
                }
            }
            if (addToBackStack) {
                transaction.addToBackStack(tag);
            }
            transaction.commit();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
            current_lat = "" + String.valueOf(mLastLocation.getLatitude());
            current_lng = "" + String.valueOf(mLastLocation.getLongitude());
            SharedHelper.putKey(context, "current_lat", current_lat);
            SharedHelper.putKey(context, "current_lng", current_lng);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }


    @Override
    protected void onResume() {
        if (mNavigationDrawerFragment != null) {
            mNavigationDrawerFragment.setNavMenuItems(NavMenu.HOME);
        }
        super.onResume();
    }

    @Override
    public void moveToServiceFlowFgmt() {
        ServiceFlowFragment fragment = ServiceFlowFragment.newInstance();
        switchContent(fragment, ServiceFlowFragment.TAG);
    }

    private void getServices() {
        loadingDialog = new CustomDialog(context);
        loadingDialog.setCancelable(false);
        loadingDialog.show();
        final JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET,
                URLHelper.GET_SERVICES, new JSONArray(), new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                loadingDialog.dismiss();
                if (response != null && response.length() > 0) {
                    mNavigationDrawerFragment = (NavigationDrawerFragment)
                            getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);

                    if (mNavigationDrawerFragment != null)
                        mNavigationDrawerFragment.setupDrawer(R.id.navigation_drawer, (DrawerLayout) findViewById(R.id
                                .drawer_layout));

                    ServiceFlowFragment fragment = ServiceFlowFragment.newInstance();
                    switchContent(fragment, ServiceFlowFragment.TAG);
                } else {
                    SettingsFragment setfragment = SettingsFragment.newInstance();
                    switchContent(setfragment, ServiceFlowFragment.TAG);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loadingDialog.dismiss();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("X-Requested-With", "XMLHttpRequest");
                headers.put("Authorization", "" + "Bearer" + " " + SharedHelper.getKey(context, KeyHelper.KEY_ACCESS_TOKEN));
                return headers;
            }
        };
        TutorApplication.getInstance().addToRequestQueue(jsonArrayRequest);
    }

    private void showExitDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setTitle(getString(R.string.app_name));
        builder.setMessage(getString(R.string.exit_app_confirm));
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Home.super.onBackPressed();
            }
        });
        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Reset to previous seletion menu in navigation
                dialog.dismiss();
            }
        });
        builder.setCancelable(false);
        final AlertDialog dialog = builder.create();
        //dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface arg) {
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
            }
        });
        dialog.show();
    }


    @Override
    public void onBackPressed() {
        Log.w(TAG, "onBackPressed: Backstack Count" + getSupportFragmentManager().getBackStackEntryCount());
        if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
            if (mNavigationDrawerFragment != null) {
                mNavigationDrawerFragment.setNavMenuItems(NavMenu.HOME);
                super.onBackPressed();
//                onBackClick();
                onBackClick();
            }else {
                onBackClick();
            }
        }else if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
            if (mNavigationDrawerFragment != null) {
                mNavigationDrawerFragment.setNavMenuItems(NavMenu.HOME);
                super.onBackPressed();
            }
        }else {
            showExitDialog();
        }

    }

    @Override
    public void onBackClick() {
        ServiceFlowFragment serviceFlowFragment = ServiceFlowFragment.newInstance();
        switchContent(serviceFlowFragment, ServiceFlowFragment.TAG);
    }

    @Override
    public void backclick() {
        onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
            ServiceFlowFragment mInstance = (ServiceFlowFragment) getSupportFragmentManager().findFragmentByTag(ServiceFlowFragment.TAG);
            mInstance.onActivityResult(requestCode, resultCode, data);
    }
}
