package com.astudent.partner.Fragments;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.provider.Settings;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.snackbar.Snackbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.appcompat.app.AlertDialog;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;
import com.astudent.partner.Activity.ActivityPassword;
import com.astudent.partner.Activity.ShowPicture;
import com.astudent.partner.Activity.ShowProfile;
import com.astudent.partner.Activity.WaitingForApproval;
import com.astudent.partner.BuildConfig;
import com.astudent.partner.Constant.URLHelper;
import com.astudent.partner.Helper.AppHelper;
import com.astudent.partner.Helper.ConnectionHelper;
import com.astudent.partner.Helper.CustomDialog;
import com.astudent.partner.Helper.SharedHelper;
import com.astudent.partner.Helper.VolleyMultipartRequest;
import com.astudent.partner.R;
import com.astudent.partner.Service.FloatingViewService;
import com.astudent.partner.TutorApplication;
import com.astudent.partner.Utils.DirectionsJSONParser;
import com.astudent.partner.Utils.Utilities;
import com.yalantis.ucrop.UCrop;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.NOTIFICATION_SERVICE;
import static com.astudent.partner.TutorApplication.trimMessage;

public class ServiceFlowFragment extends Fragment implements OnMapReadyCallback, LocationListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    public static final String TAG = "ServiceFlowFragment";
    public static final int FLOW_HOME = 1;
    public static final int FLOW_ACCEPT_REJECT = 2;
    public static final int FLOW_STARTED = 3;
    public static final int FLOW_ARRIVED = 4;
    public static final int FLOW_SERVICE_PICKED = 5;
    public static final int FLOW_INVOICE = 6;
    public static final int FLOW_RATING = 7;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    public static final int MY_PERMISSIONS_ACCESS_FINE = 3;
    public static final int MY_PERMISSIONS_PHONE_CALL = 2;
    public static final int CAPTURE_IMAGE_REQ_CODE = 200;
    private static final int REQUEST_TAKE_PHOTO = 1;
    private static final int REQUEST_LOCATION = 1450;
    private static final int CAMERA_REQUEST = 0x78;
    private static final int CODE_DRAW_OVER_OTHER_APP_PERMISSION = 2084;
    public final int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 1;
    public int SERVICE_FLOW = 1;
    public String PreviousStatus = "";
    public String CurrentStatus = "";
    public String feedBackRating = "1", request_id = "", feedBackComment = "";
    public boolean mCheckOnline;
    Context context;
    Utilities utils = new Utilities();
    Activity thisActivity;
    View view;
    Marker userMarker, providerMarker;
    int value = 0;
    ObjectAnimator scaleDown;
    Uri image_uri;
    ServiceFlowFgmtListener mListener;
    //Internet
    ConnectionHelper helper;
    Boolean isInternet;
    AlertDialog alert;
    Handler handleCheckStatus;
    int method;
    String isPaid = "", paymentMode = "", strTag = "";
    boolean isRunning = false, timerCompleted = false;
    MediaPlayer mPlayer;
    CountDownTimer countDownTimer;
    CustomDialog customDialog;
    String source_lat = "", source_lng = "", source_address = "", dest_lat = "", dest_lng = "", dest_address = "";
    String crt_lat = "", crt_lng = "";
    String mCurrentPhotoPath = "", strServiceType = "";
    File mFileBefore, mFileAfter;
    Double crtLat, crtLng;
    ImageView offlineImg;
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    LinearLayout lnrMap, lnrGoOffline, lnrWorkStatus, lnrAcceptOrReject, lnrCancelTrip, lnrSchedule, lnrServiceFlow, lnrInvoice, lnrRateProvider, lnrCall, lnrCall1, lnrServicePhoto, lnrAfterService, lnrBeforeService, offlineLayout, description_layout;
    RelativeLayout toolbar;
    Button btnAccept, btnReject, btnStatus, btnPayNow, btnSubmitReview, btnGoOffline, btnServiceStatus;
    TextView txtUserName01, txtSubServiceType, txtDesc, txtTimer, txtUserName02, txtUserName03, lblBasePrice, lblHourlyFareInvoice, lblTaxFare, lblWalletDetection, lblPromotionApplied,
            lblTotal, lblAmountPaid, lblPaymentTypeInvoice, lblProviderNameRate, txtScheduleType, txtScheduleAddress, txtSchedule, lblBeforeService, lblAfterService, lblAfterTxtComments, lblBeforeTxtComments;
    TextView payableText, payableAmountText;
    Chronometer lblTimerText;
    TextView lblTimerTxt1;
    EditText txtComments, txtServiceComments;

    //UI Elements
    ImageView imgUser01, imgUser02, imgUser03, imgPaymentTypeInvoice, imgPaymentType, imgProviderRate,
            imgGotoPhoto, imgBeforeService, imgCall1, imgChat, imgAfterService, imgCancelTrip, backArrow, imgMenu, imgCurrentLocation; /*imgBeforeComments, imgAfterComments*/
    RatingBar ratingUser01, ratingUser02, ratingUser03, ratingProviderRate;
    ProgressBar after_progress_bar, before_progress_bar;
    //Animation
    Animation slide_down, slide_up, slide_up_top, slide_up_down;
    GoogleMap mMap;
    SupportMapFragment mapFragment;
    Marker current_marker;
    ImageView imgNavigationToSource;
    TextView txtServiceAddress;
    ;
    LinearLayout destinationLayer;
    LinearLayout rytCurrent;
    int PERMISSION_ALL = 1;
    String[] PERMISSIONS = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private double srcLatitude = 0;
    private double srcLongitude = 0;
    private double destLatitude = 0;
    private double destLongitude = 0;
    private LatLng sourceLatLng;
    private LatLng destLatLng;
    // service flow
    private String token;
    private String count = "", strUserId = "";
    private JSONArray statusResponses;
    private Object previous_request_id = " ";
    /**
     * Hold a reference to the current animator, so that it can be canceled mid-way.
     */
    private Animator mCurrentAnimator;
    /**
     * The system "short" animation time duration, in milliseconds. This duration is ideal for
     * subtle animations or animations that occur very frequently.
     */
    private int mShortAnimationDuration;
    private String imgPath;

    public static ServiceFlowFragment newInstance() {
        ServiceFlowFragment fragment = new ServiceFlowFragment();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    public static Long getFormatedDateTime(String dateStr) {

        long diff = 0;

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date1 = null, date2 = null;
        try {
            date1 = df.parse(dateStr);

            Calendar c = Calendar.getInstance();
            System.out.println("Current time => " + c.getTime());

            SimpleDateFormat cdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String currentDate = cdf.format(c.getTime());

            date2 = df.parse(currentDate);

            diff = date2.getTime() - date1.getTime();

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return diff;
    }

    public static String getDiffDateTime(String dateStr, String currentDate) {

        long diff = 0;
        String date = "";
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date1 = null, date2 = null;
        try {
            date1 = df.parse(dateStr);

            Calendar c = Calendar.getInstance();
            System.out.println("Current time => " + c.getTime());

            //SimpleDateFormat cdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            //String currentDate = cdf.format(c.getTime());

            date2 = df.parse(currentDate);

            diff = date2.getTime() - date1.getTime();

            long diffSeconds = diff / 1000 % 60;
            long diffMinutes = diff / (60 * 1000) % 60;
            long diffHours = diff / (60 * 60 * 1000);

            if (diffHours >= 1) {
                if (diffHours < 10) {
                    date = "0" + diffHours + ":";
                } else {
                    date = diffHours + ":";
                }
            }
            if (diffMinutes >= 1) {
                if (diffMinutes < 10) {
                    date = date + "0" + diffMinutes + ":";
                } else {
                    date = date + diffMinutes + ":";
                }
            } else {
                date = date + "00:";
            }
            if (diffSeconds >= 1) {
                if (diffSeconds < 10) {
                    date = date + "0" + diffSeconds;
                } else {
                    date = date + diffSeconds + "";
                }
            } else {
                date = date + "00";
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        thisActivity = getActivity();
    }

    private void showCancelDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage(getString(R.string.cancel_confirm));

        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                cancelRequest(request_id);
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

    private void showCallDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage(getString(R.string.call_confirm));

        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, MY_PERMISSIONS_PHONE_CALL);
                } else {
                    if (SharedHelper.getKey(context, "provider_mobile_no") != null && SharedHelper.getKey(context, "provider_mobile_no") != "") {
                        Intent intentCall = new Intent(Intent.ACTION_CALL);
                        intentCall.setData(Uri.parse("tel:" + SharedHelper.getKey(context, "provider_mobile_no")));
                        startActivity(intentCall);
                    } else {
                        Toast.makeText(context, "" + getString(R.string.no_mobile), Toast.LENGTH_SHORT).show();
                    }
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

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_service_flow, container, false);
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                findViewById(view);
                //permission to access location
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // Android M Permission check
                    if (isAdded()) {
                        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_ACCESS_FINE);
                    }
                } else {
                    initMap();
                    if (getActivity() != null)
                        MapsInitializer.initialize(getActivity());
                }
            }
        }, 500);

        return view;
    }

    private void initMap() {
        if (isAdded()) {
            if (mMap == null && this != null) {
                FragmentManager fm = getChildFragmentManager();
                mapFragment = ((SupportMapFragment) fm.findFragmentById(R.id.provider_map));
                if (mapFragment != null)
                    mapFragment.getMapAsync(this);
            }

            setupMap();
        }

    }

    // initializing all views
    @SuppressWarnings("MissingPermission")
    private void findViewById(View view) {

        token = SharedHelper.getKey(context, "access_token");


        lnrMap = (LinearLayout) view.findViewById(R.id.lnrMap);
        lnrAcceptOrReject = (LinearLayout) view.findViewById(R.id.lnrAcceptOrReject);
        lnrSchedule = (LinearLayout) view.findViewById(R.id.lnrSchedule);
        lnrServiceFlow = (LinearLayout) view.findViewById(R.id.lnrServiceFlow);
        lnrWorkStatus = (LinearLayout) view.findViewById(R.id.lnrWorkStatus);
        lnrCancelTrip = (LinearLayout) view.findViewById(R.id.lnrCancelTrip);
        lnrInvoice = (LinearLayout) view.findViewById(R.id.lnrInvoice);
        lnrRateProvider = (LinearLayout) view.findViewById(R.id.lnrRateProvider);
        lnrGoOffline = (LinearLayout) view.findViewById(R.id.lnrGoOffline);
        lnrCall = (LinearLayout) view.findViewById(R.id.lnrCall);
        lnrCall1 = (LinearLayout) view.findViewById(R.id.lnrCall1);
        lnrServicePhoto = (LinearLayout) view.findViewById(R.id.lnrServicePhoto);
        lnrAfterService = (LinearLayout) view.findViewById(R.id.lnrAfterService);
        lnrBeforeService = (LinearLayout) view.findViewById(R.id.lnrBeforeService);
        toolbar = (RelativeLayout) view.findViewById(R.id.toolbar);

        btnAccept = (Button) view.findViewById(R.id.btnAccept);
        btnReject = (Button) view.findViewById(R.id.btnReject);
        btnStatus = (Button) view.findViewById(R.id.btnStatus);
        btnPayNow = (Button) view.findViewById(R.id.btnPayNow);
        btnSubmitReview = (Button) view.findViewById(R.id.btnSubmitReview);
        btnGoOffline = (Button) view.findViewById(R.id.btnGoOffline);
        btnServiceStatus = (Button) view.findViewById(R.id.btnServiceStatus);


        txtUserName01 = (TextView) view.findViewById(R.id.txtUserName01);
        txtUserName02 = (TextView) view.findViewById(R.id.txtUserName02);
        txtUserName03 = (TextView) view.findViewById(R.id.txtUserName03);
        txtSchedule = (TextView) view.findViewById(R.id.txtSchedule);
        txtScheduleAddress = (TextView) view.findViewById(R.id.txtScheduleAddress);
        txtScheduleType = (TextView) view.findViewById(R.id.txtScheduleType);
        txtTimer = (TextView) view.findViewById(R.id.txtTimer);
        lblBasePrice = (TextView) view.findViewById(R.id.lblBasePrice);
        lblHourlyFareInvoice = (TextView) view.findViewById(R.id.lblHourlyFareInvoice);
        lblTaxFare = (TextView) view.findViewById(R.id.lblTaxFare);
        lblWalletDetection = (TextView) view.findViewById(R.id.lblWalletDetection);
        lblPromotionApplied = (TextView) view.findViewById(R.id.lblPromotionApplied);
        lblAmountPaid = (TextView) view.findViewById(R.id.lblAmountPaid);
        payableText = view.findViewById(R.id.text_payable);
        payableAmountText = view.findViewById(R.id.text_payable_amount);
        lblTotal = (TextView) view.findViewById(R.id.lblTotal);
        lblPaymentTypeInvoice = (TextView) view.findViewById(R.id.lblPaymentTypeInvoice);
        lblProviderNameRate = (TextView) view.findViewById(R.id.lblProviderNameRate);
        lblBeforeService = (TextView) view.findViewById(R.id.lblBeforeService);
        lblAfterService = (TextView) view.findViewById(R.id.lblAfterService);

        txtComments = (EditText) view.findViewById(R.id.txtComments);
        txtComments.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);

        txtServiceComments = (EditText) view.findViewById(R.id.txtServiceComments);

//        imgAfterComments = (ImageView) view.findViewById(R.id.imgAfterComments);
//        imgBeforeComments = (ImageView) view.findViewById(R.id.imgBeforeComments);
        imgUser01 = (ImageView) view.findViewById(R.id.imgUser01);
        imgUser02 = (ImageView) view.findViewById(R.id.imgUser02);
        imgUser03 = (ImageView) view.findViewById(R.id.imgUser03);
        imgCancelTrip = (ImageView) view.findViewById(R.id.imgCancelTrip);
        imgPaymentTypeInvoice = (ImageView) view.findViewById(R.id.imgPaymentTypeInvoice);
        imgProviderRate = (ImageView) view.findViewById(R.id.imgProviderRate);
        imgGotoPhoto = (ImageView) view.findViewById(R.id.imgGotoPhoto);
        imgBeforeService = (ImageView) view.findViewById(R.id.imgBeforeService);
        imgAfterService = (ImageView) view.findViewById(R.id.imgAfterService);
        backArrow = (ImageView) view.findViewById(R.id.backArrow);
        imgMenu = (ImageView) view.findViewById(R.id.imgMenu);
        imgChat = (ImageView) view.findViewById(R.id.imgChat);
        imgCurrentLocation = (ImageView) view.findViewById(R.id.imgCurrentLocation);

        ratingUser01 = (RatingBar) view.findViewById(R.id.ratingUser01);
        ratingUser02 = (RatingBar) view.findViewById(R.id.ratingUser02);
        ratingUser03 = (RatingBar) view.findViewById(R.id.ratingUser03);
        ratingProviderRate = (RatingBar) view.findViewById(R.id.ratingProviderRate);

        txtSubServiceType = view.findViewById(R.id.txtSubServiceType);
        txtDesc = view.findViewById(R.id.txtDesc);
        //Load animation
        if (getActivity() != null) {
            slide_down = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_down);
            slide_up = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_up);
            slide_up_top = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_up_top);
            slide_up_down = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_up_down);
        }
        lblTimerText = (Chronometer) view.findViewById(R.id.lblTimerText);
        lblTimerTxt1 = (TextView) view.findViewById(R.id.lblTimerText1);

        lblTimerText.setBase(SystemClock.elapsedRealtime());
        lblTimerTxt1.setText("00:00");

        imgNavigationToSource = (ImageView) view.findViewById(R.id.imgNavigationToSource);
        txtServiceAddress = (TextView) view.findViewById(R.id.txtServiceAddress);
        destinationLayer = (LinearLayout) view.findViewById(R.id.destinationLayer);
        offlineLayout = (LinearLayout) view.findViewById(R.id.offline_layout);
        description_layout = (LinearLayout) view.findViewById(R.id.description_layout);
        rytCurrent = (LinearLayout) view.findViewById(R.id.rytCurrent);
        offlineImg = (ImageView) view.findViewById(R.id.offline_img);

        lblBeforeTxtComments = (TextView) view.findViewById(R.id.lblBeforeTxtComment);
        lblAfterTxtComments = (TextView) view.findViewById(R.id.lblAfterTxtComment);

        scaleDown = ObjectAnimator.ofPropertyValuesHolder(txtTimer,
                PropertyValuesHolder.ofFloat("scaleX", 1.2f),
                PropertyValuesHolder.ofFloat("scaleY", 1.2f));
        scaleDown.setDuration(500);

        scaleDown.setRepeatCount(ObjectAnimator.INFINITE);
        scaleDown.setRepeatMode(ObjectAnimator.REVERSE);

        btnAccept.setOnClickListener(new OnClick());
        btnReject.setOnClickListener(new OnClick());
        lnrCancelTrip.setOnClickListener(new OnClick());
        btnPayNow.setOnClickListener(new OnClick());
        btnStatus.setOnClickListener(new OnClick());
        btnSubmitReview.setOnClickListener(new OnClick());
        lnrCall.setOnClickListener(new OnClick());
        lnrCall1.setOnClickListener(new OnClick());
        btnGoOffline.setOnClickListener(new OnClick());
        imgGotoPhoto.setOnClickListener(new OnClick());
        imgBeforeService.setOnClickListener(new OnClick());
        imgAfterService.setOnClickListener(new OnClick());
        backArrow.setOnClickListener(new OnClick());
        imgMenu.setOnClickListener(new OnClick());
        imgChat.setOnClickListener(new OnClick());
        imgNavigationToSource.setOnClickListener(new OnClick());
        btnServiceStatus.setOnClickListener(new OnClick());
        imgCurrentLocation.setOnClickListener(new OnClick());
//        imgBeforeComments.setOnClickListener(new OnClick());
//        imgAfterComments.setOnClickListener(new OnClick());
        imgUser01.setOnClickListener(new OnClick());
        imgUser03.setOnClickListener(new OnClick());

        lnrServicePhoto.setOnClickListener(new OnClick());
        lnrServiceFlow.setOnClickListener(new OnClick());
        lnrAcceptOrReject.setOnClickListener(new OnClick());
        lnrGoOffline.setOnClickListener(new OnClick());
        lnrWorkStatus.setOnClickListener(new OnClick());
        lnrInvoice.setOnClickListener(new OnClick());
        lnrRateProvider.setOnClickListener(new OnClick());
        offlineLayout.setOnClickListener(new OnClick());

        // set the layout
        serviceFlow(FLOW_HOME);

        // handler for checking the status
        startCheckStatus();

        // enabling location icon
        if (getActivity() != null)
            statusCheck();

        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() != KeyEvent.ACTION_DOWN)
                    return true;

                if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
                    return false;
                }

                if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
                    return false;
                }

                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    utils.print("", "Back key pressed!");
                    if (lnrServicePhoto.getVisibility() == View.VISIBLE) {
                        lnrServicePhoto.setVisibility(View.GONE);
                        rytCurrent.setVisibility(View.VISIBLE);
                        if (SERVICE_FLOW == FLOW_INVOICE) {
                            lnrInvoice.setVisibility(View.VISIBLE);
                            imgGotoPhoto.setVisibility(View.GONE);
                        } else if (SERVICE_FLOW == FLOW_ARRIVED || SERVICE_FLOW == FLOW_SERVICE_PICKED) {
                            lnrServiceFlow.setVisibility(View.VISIBLE);
                            imgGotoPhoto.setVisibility(View.GONE);
                        }
                    } else {
                        showExitDialog();
                    }
                    return true;
                } else {
                    showExitDialog();
                }

                return false;
            }
        });
    }

    private void showExitDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(thisActivity);
        builder.setMessage(getString(R.string.exit_app_confirm));
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                thisActivity.finish();
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
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface arg) {
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
            }
        });
        dialog.show();
    }

    public void statusCheck() {
        final LocationManager manager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            enableLoc();
        }
    }

    private void enableLoc() {
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle bundle) {

                    }

                    @Override
                    public void onConnectionSuspended(int i) {
                        mGoogleApiClient.connect();
                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult connectionResult) {

                        Log.d("Location error", "Location error " + connectionResult.getErrorCode());
                    }
                }).build();
        mGoogleApiClient.connect();

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(30 * 1000);
        locationRequest.setFastestInterval(5 * 1000);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(getActivity(), REQUEST_LOCATION);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                }
            }
        });
//	        }

    }

    // on map ready
    @Override
    public void onMapReady(GoogleMap googleMap) {
        try {
            // Customise the styling of the BASE_URL map using a JSON object defined
            // in a raw resource file.
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            getActivity(), R.raw.style_json));

            if (!success) {
                Log.e("Map:Style", "Style parsing failed.");
            } else {
                Log.e("Map:Style", "Style Applied.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e("Map:Style", "Can't find style. Error: ", e);
        }
        mMap = googleMap;
        setupMap();

        if (!SharedHelper.getKey(context, "current_lat").equalsIgnoreCase("") &&
                !SharedHelper.getKey(context, "current_lng").equalsIgnoreCase("")) {
            LatLng location = new LatLng(Double.parseDouble(SharedHelper.getKey(context, "current_lat")),
                    Double.parseDouble(SharedHelper.getKey(context, "current_lng")));

            if (location != null) {
                CameraPosition cameraPosition = new CameraPosition.Builder().target(location).zoom(16).build();
                mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
        }

        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                //Location Permission already granted
                buildGoogleApiClient();
//                mMap.setMyLocationEnabled(true);
            } else {
                //Request Location Permission
                checkLocationPermission();
            }
        } else {
            buildGoogleApiClient();
//            mMap.setMyLocationEnabled(true);
        }
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    // on location changed
    @Override
    public void onLocationChanged(Location location) {
        crt_lat = "" + location.getLatitude();
        crt_lng = "" + location.getLongitude();
        crtLat = location.getLatitude();
        crtLng = location.getLongitude();

        LatLng currentLoc = new LatLng(crtLat, crtLng);

        if (providerMarker != null) {
            providerMarker.remove();
        }

        MarkerOptions markerOptions = new MarkerOptions()
                .position(currentLoc)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.provider));
        providerMarker = mMap.addMarker(markerOptions);


       /* if (value == 0) {
            value = 1;*/
            LatLng loc = new LatLng(location.getLatitude(), location.getLongitude());
            CameraPosition cameraPosition = new CameraPosition.Builder().target(loc).zoom(16).build();
            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
//        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(3000);
        mLocationRequest.setFastestInterval(3000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private Dialog showGuestDialog() {
        final Dialog guestDialog = new Dialog(getContext());
        guestDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        guestDialog.setCancelable(false);
        guestDialog.setCanceledOnTouchOutside(true);
        guestDialog.setContentView(R.layout.activity_guest_dialog);
        Button guestCancel = guestDialog.findViewById(R.id.guestCancel);
        Button guestLogin = guestDialog.findViewById(R.id.guestLogin);
        Window window = guestDialog.getWindow();
        assert window != null;
        WindowManager.LayoutParams param = window.getAttributes();
        param.gravity = Gravity.CENTER | Gravity.CENTER_HORIZONTAL;
        window.setAttributes(param);
        guestDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        guestCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                guestDialog.dismiss();
            }
        });
        guestLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedHelper.putKey(getContext(), "current_status", "");
                SharedHelper.putKey(getContext(), "loggedIn", getString(R.string.False));
                SharedHelper.putKey(getContext(), "login_by", "");
                Intent mainIntent = new Intent(getContext(), ActivityPassword.class);
                mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(mainIntent);
                getActivity().finish();
            }
        });
        return guestDialog;
    }

    void uploadServicePhoto(final String strTag, final String CurrentStatus, String request_id) {
        customDialog = new CustomDialog(context);
        customDialog.setCancelable(false);
        customDialog.show();

        try {

            if (helper.isConnectingToInternet()) {
                String url = URLHelper.BASE_URL + "/api/provider/trip/" + request_id;
                VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, url,
                        new Response.Listener<NetworkResponse>() {
                            @Override
                            public void onResponse(NetworkResponse response) {
                                customDialog.dismiss();
                                lnrServicePhoto.setVisibility(View.GONE);
                                imgCurrentLocation.setVisibility(View.VISIBLE);
                                imgGotoPhoto.setVisibility(View.GONE);
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        customDialog.dismiss();
                        Log.e(TAG, "" + error);
                        displayMessage(getString(R.string.something_went_wrong));

                    }
                }) {
                    @Override
                    public Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        params.put("_method", "PATCH");
                        params.put("status", CurrentStatus);
                        if (SERVICE_FLOW == FLOW_ARRIVED) {
                            params.put("before_comment", "" + txtServiceComments.getText().toString());
                        } else if (SERVICE_FLOW == FLOW_SERVICE_PICKED) {
                            params.put("after_comment", "" + txtServiceComments.getText().toString());
                        }
                        return params;
                    }

                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        HashMap<String, String> headers = new HashMap<String, String>();
                        headers.put("X-Requested-With", "XMLHttpRequest");
                        headers.put("Authorization", "Bearer " + SharedHelper.getKey(context, "access_token"));
                        utils.print("Token Access", "" + SharedHelper.getKey(context, "token_type") + " " + SharedHelper.getKey(context, "access_token"));
                        return headers;
                    }

                    @Override
                    protected Map<String, VolleyMultipartRequest.DataPart> getByteData() throws AuthFailureError {
                        Map<String, VolleyMultipartRequest.DataPart> dataparams = new HashMap<>();
                        if (strTag.equalsIgnoreCase("before")) {
                            dataparams.put("before_image", new VolleyMultipartRequest.DataPart("userImage.jpg",
                                    AppHelper.getFileDataFromDrawable(imgBeforeService.getDrawable()), "image/jpeg"));
                        } else {
                            dataparams.put("after_image", new VolleyMultipartRequest.DataPart("userImage.jpg",
                                    AppHelper.getFileDataFromDrawable(imgAfterService.getDrawable()), "image/jpeg"));
                        }
                        return dataparams;
                    }
                };
                TutorApplication.getInstance().addToRequestQueue(volleyMultipartRequest);
            } else {
                displayMessage(getString(R.string.oops_connect_your_internet));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    // method for initializing start check status
    private void startCheckStatus() {
        handleCheckStatus = new Handler();
        helper = new ConnectionHelper(context);
        //check status every 3 sec
        handleCheckStatus.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (helper.isConnectingToInternet()) {
                    if (!isAdded()) {
                        return;
                    }
                    utils.print("Handler", "Called");
                    if (!SharedHelper.getKey(getContext(), Utilities.skip_login).equalsIgnoreCase(Utilities.skipped)) {
                        checkStatus();
                    }
                    if (alert != null && alert.isShowing()) {
                        alert.dismiss();
                        alert = null;
                    }
                } else {
                    showDialog();
                }
                handleCheckStatus.postDelayed(this, 3000);
            }
        }, 3000);
    }

    // Service Flow Layout changes
    void serviceFlow(final int strTag) {
        SERVICE_FLOW = strTag;

        imgUser01.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                JSONObject statusResponse = new JSONObject();
                JSONObject statusServiceType = new JSONObject();
                try {
                    statusResponse = statusResponses.getJSONObject(0).getJSONObject("request");
                    statusServiceType = statusResponse.getJSONObject("service_type");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                strServiceType = statusServiceType.optString("name");
                strUserId = statusResponse.optString("user_id");
                Intent showProfileIntent = new Intent(context, ShowProfile.class);
                showProfileIntent.putExtra("user_id", strUserId);
                showProfileIntent.putExtra("service_type", strServiceType);
                startActivity(showProfileIntent);
            }
        });
        imgUser02.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject statusResponse = new JSONObject();
                JSONObject statusServiceType = new JSONObject();
                try {
                    statusResponse = statusResponses.getJSONObject(0).getJSONObject("request");
                    statusServiceType = statusResponse.getJSONObject("service_type");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                strServiceType = statusServiceType.optString("name");
                strUserId = statusResponse.optString("user_id");
                Intent showProfileIntent = new Intent(context, ShowProfile.class);
                showProfileIntent.putExtra("user_id", strUserId);
                showProfileIntent.putExtra("service_type", strServiceType);
                startActivity(showProfileIntent);
            }
        });
        imgProviderRate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject statusResponse = new JSONObject();
                JSONObject statusServiceType = new JSONObject();
                try {
                    statusResponse = statusResponses.getJSONObject(0).getJSONObject("request");
                    statusServiceType = statusResponse.getJSONObject("service_type");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                strServiceType = statusServiceType.optString("name");
                strUserId = statusResponse.optString("user_id");
                Intent showProfileIntent = new Intent(context, ShowProfile.class);
                showProfileIntent.putExtra("user_id", strUserId);
                showProfileIntent.putExtra("service_type", strServiceType);
                startActivity(showProfileIntent);
            }
        });

        utils.hideKeypad(context, getView());
        if (lnrAcceptOrReject.getVisibility() == View.VISIBLE) {
            lnrAcceptOrReject.startAnimation(slide_down);
            lnrAcceptOrReject.setVisibility(View.GONE);
        } else if (lnrServiceFlow.getVisibility() == View.VISIBLE) {
            lnrServiceFlow.startAnimation(slide_down);
            lnrServiceFlow.setVisibility(View.GONE);
        } else if (lnrInvoice.getVisibility() == View.VISIBLE) {
            lnrInvoice.startAnimation(slide_down);
            lnrInvoice.setVisibility(View.GONE);
        } else if (lnrRateProvider.getVisibility() == View.VISIBLE) {
            lnrRateProvider.startAnimation(slide_down);
            lnrRateProvider.setVisibility(View.GONE);
        } else if (lnrGoOffline.getVisibility() == View.VISIBLE) {
            lnrGoOffline.setVisibility(View.GONE);
            toolbar.setVisibility(View.GONE);
        }

        switch (strTag) {
            case FLOW_HOME:
                imgMenu.setVisibility(View.VISIBLE);
                imgChat.setVisibility(View.GONE);
                if (lnrGoOffline.getVisibility() == View.GONE) {
                    lnrGoOffline.setVisibility(View.VISIBLE);
                    toolbar.setVisibility(View.VISIBLE);
                }
                destinationLayer.setVisibility(View.GONE);
                break;
            case FLOW_ACCEPT_REJECT:
                imgChat.setVisibility(View.GONE);
                lnrAcceptOrReject.startAnimation(slide_up);
                lnrAcceptOrReject.setVisibility(View.VISIBLE);
                break;
            case FLOW_STARTED:
                if (lnrServiceFlow.getVisibility() == View.GONE) {
                    lnrServiceFlow.startAnimation(slide_up);
                }
                imgChat.setVisibility(View.VISIBLE);
                lnrCall.setVisibility(View.VISIBLE);
                lnrCancelTrip.setVisibility(View.VISIBLE);
                lnrWorkStatus.setVisibility(View.GONE);
                lnrServiceFlow.setVisibility(View.VISIBLE);
                break;
            case FLOW_ARRIVED:
                if (lnrServiceFlow.getVisibility() == View.GONE) {
                    lnrServiceFlow.startAnimation(slide_up);
                }
                imgChat.setVisibility(View.VISIBLE);
                lnrCall.setVisibility(View.GONE);
                imgMenu.setVisibility(View.GONE);
                lnrAfterService.setVisibility(View.GONE);
                lnrServiceFlow.setVisibility(View.VISIBLE);
                lblTimerText.setVisibility(View.VISIBLE);
                lblTimerText.setBase(SystemClock.elapsedRealtime());
                imgCurrentLocation.setVisibility(View.VISIBLE);
                lnrWorkStatus.setVisibility(View.VISIBLE);
                lnrCancelTrip.setVisibility(View.GONE);
                break;
            case FLOW_SERVICE_PICKED:
                if (lnrServiceFlow.getVisibility() == View.GONE) {
                    lnrServiceFlow.startAnimation(slide_up);
                }
                imgChat.setVisibility(View.VISIBLE);
                lnrWorkStatus.setVisibility(View.VISIBLE);
                txtServiceComments.setText("");
                imgGotoPhoto.setVisibility(View.GONE);
                imgCurrentLocation.setVisibility(View.VISIBLE);
                lnrCall.setVisibility(View.GONE);
                lnrServiceFlow.setVisibility(View.VISIBLE);
                imgMenu.setVisibility(View.GONE);
                lblTimerText.setVisibility(View.VISIBLE);
                lnrBeforeService.setVisibility(View.VISIBLE);
                lnrAfterService.setVisibility(View.VISIBLE);
                lnrCancelTrip.setVisibility(View.GONE);
                lblTimerText.start();
//                Toast.makeText(context, "Your session is started!", Toast.LENGTH_SHORT).show();
                break;
            case FLOW_INVOICE:
                imgChat.setVisibility(View.GONE);
                lnrInvoice.startAnimation(slide_up);
                lnrInvoice.setVisibility(View.VISIBLE);
                imgGotoPhoto.setVisibility(View.GONE);
                imgCurrentLocation.setVisibility(View.VISIBLE);
                imgMenu.setVisibility(View.GONE);
                lnrAfterService.setVisibility(View.VISIBLE);
                lnrBeforeService.setVisibility(View.VISIBLE);
                lblTimerText.stop();
//                Toast.makeText(context, "Your session is completed!", Toast.LENGTH_SHORT).show();
                break;
            case FLOW_RATING:
                clearServicePage();
                imgChat.setVisibility(View.GONE);
                imgGotoPhoto.setVisibility(View.GONE);
                txtComments.setText("");
                lnrRateProvider.startAnimation(slide_up);
                lnrRateProvider.setVisibility(View.VISIBLE);
                ratingProviderRate.setRating(5.0f);
                feedBackRating = "1";
                ratingProviderRate.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                    @Override
                    public void onRatingChanged(RatingBar ratingBar, float rating, boolean b) {
                        if (rating < 1.0f) {
                            ratingProviderRate.setRating(1.0f);
                            feedBackRating = "1";
                        }
                        feedBackRating = String.valueOf((int) rating);
                    }
                });
                break;
            default:
                break;
        }
    }

    // Check Status
    private void checkStatus() {
        try {
            if (helper.isConnectingToInternet()) {
                String url = URLHelper.BASE_URL + "/api/provider/trip?latitude=" + crt_lat + "&longitude=" + crt_lng;

                utils.print("Destination Current Lat", "" + crt_lat);
                utils.print("Destination Current Lng", "" + crt_lng);

                final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("CheckStatus", "" + response.toString());
                        //SharedHelper.putKey(context, "currency", response.optString("currency"));
                        if (response.optString("account_status").equals("new") || response.optString("account_status").equals("onboarding")) {
                            //handleCheckStatus.removeCallbacksAndMessages(null);
                            Intent intent = new Intent(context, WaitingForApproval.class);
                            context.startActivity(intent);
                            getActivity().finish();
                        } else {
                            if (response.optString("service_status").equals("offline")) {
                                //handleCheckStatus.removeCallbacksAndMessages(null);
                                mCheckOnline = true;
                                goOffline();
                            } else {

                                if (mCheckOnline) {
                                    mCheckOnline = false;
                                    Toast.makeText(context, "You are now online!", Toast.LENGTH_SHORT).show();
                                    offlineImg.setVisibility(View.GONE);
                                    offlineLayout.setVisibility(View.GONE);
                                    btnGoOffline.setText(getString(R.string.go_offline));
                                    imgCurrentLocation.setVisibility(View.VISIBLE);
                                }
                                if (response.optJSONArray("requests") != null &&
                                        response.optJSONArray("requests").length() > 0) {
                                    JSONObject statusResponse = null;
                                    try {
                                        statusResponses = response.optJSONArray("requests");
                                        statusResponse = response.optJSONArray("requests").getJSONObject(0).optJSONObject("request");
                                        request_id = response.optJSONArray("requests").getJSONObject(0).optString("request_id");
                                        SharedHelper.putKey(context, "request_id", request_id);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                    if (!statusResponse.optString("s_latitude").equalsIgnoreCase("")) {
                                        srcLatitude = Double.valueOf(statusResponse.optString("s_latitude"));
                                    }
                                    if (!statusResponse.optString("s_longitude").equalsIgnoreCase("")) {
                                        srcLongitude = Double.valueOf(statusResponse.optString("s_longitude"));
                                    }
                                    if (!crt_lng.equalsIgnoreCase("")) {
                                        destLongitude = Double.valueOf(crt_lng);
                                    }
                                    if (!crt_lat.equalsIgnoreCase("")) {
                                        destLatitude = Double.valueOf(crt_lat);
                                    }
//                                    //noinspection deprecation
//                                    if (srcLatitude != 0 && srcLongitude != 0) {
//                                        getAddress(srcLatitude, srcLongitude);
//                                    }

                                    if (statusResponse.optString("status").equalsIgnoreCase("SEARCHING") ||
                                            statusResponse.optString("status").equalsIgnoreCase("STARTED")) {
                                        if (srcLongitude != 0 && srcLongitude != 0 && destLatitude != 0 && destLongitude != 0) {
                                            mMap.clear();
                                            // draw user and provider marker
                                            setRoutePath();
                                        }
                                    } else {
                                        if (mMap != null) {
                                            mMap.clear();
                                        }
                                        // set user and provider marker without route
                                        setUserProviderMarker();
                                    }


                                    if ((statusResponse != null) && (request_id != null)) {
                                        if ((!previous_request_id.equals(request_id) || previous_request_id.equals(" "))
                                                && mMap != null) {
                                            previous_request_id = request_id;

                                        }
                                        utils.print("Cur_and_New_status :", "" + CurrentStatus + "," + statusResponse.optString("status"));
                                        if (!PreviousStatus.equals(statusResponse.optString("status"))) {
                                            PreviousStatus = statusResponse.optString("status");
                                            txtServiceAddress.setText(statusResponse.optString("s_address"));
//                                            serviceFlow(FLOW_HOME);
                                            utils.print("responseObj(" + request_id + ")", statusResponse.toString());
                                            utils.print("Cur_and_New_status :", "" + CurrentStatus + "," + statusResponse.optString("status"));
                                            if (!statusResponse.optString("status").equals("SEARCHING")) {
                                                timerCompleted = false;
                                                if (mPlayer != null && mPlayer.isPlaying()) {
                                                    mPlayer.stop();
                                                    mPlayer = null;
                                                    countDownTimer.cancel();
                                                }
                                            }
                                            if (statusResponse.optString("status").equals("SEARCHING")) {
                                                SharedHelper.putKey(context, "status", "onride");
                                                if (!scaleDown.isRunning()) {
                                                    scaleDown.start();
                                                }
                                                if (!timerCompleted) {
                                                    setValuesTo_ll_01_contentLayer_accept_or_reject_now(statusResponses);
                                                    serviceFlow(FLOW_ACCEPT_REJECT);
                                                }
                                                CurrentStatus = "STARTED";
                                            } else if (statusResponse.optString("status").equals("STARTED")) {
                                                setValuesTo_ll_03_contentLayer_service_flow(statusResponses);
                                                serviceFlow(FLOW_STARTED);
                                                btnStatus.setText(getString(R.string.tap_when_arrived));
                                                CurrentStatus = "ARRIVED";
                                                destinationLayer.setVisibility(View.VISIBLE);
                                                SharedHelper.putKey(context, "status", "onride");
                                                lnrCancelTrip.setVisibility(View.VISIBLE);
                                                imgGotoPhoto.setVisibility(View.GONE);
                                            } else if (statusResponse.optString("status").equals("ARRIVED")) {
                                                setValuesTo_ll_03_contentLayer_service_flow(statusResponses);
                                                serviceFlow(FLOW_ARRIVED);

                                                destinationLayer.setVisibility(View.GONE);
                                                btnStatus.setText(getString(R.string.tap_when_pickedup));
                                                lnrAfterService.setVisibility(View.GONE);
                                                btnServiceStatus.setText(thisActivity.getResources().getString(R.string.start_service));
                                                SharedHelper.putKey(context, "status", "onride");
                                                txtServiceComments.setVisibility(View.VISIBLE);
                                                btnServiceStatus.setVisibility(View.VISIBLE);
                                                try {
                                                    JSONObject jObj = new JSONObject(statusResponse.toString());
                                                    if (jObj.optString("before_image") != null && !jObj.optString("before_image").equalsIgnoreCase("")) {
                                                        Picasso.with(context).load(AppHelper.getImageUrl(jObj.optString("before_image")))
                                                                .memoryPolicy(MemoryPolicy.NO_CACHE).placeholder(R.drawable.no_image).error(R.drawable.no_image).into(imgBeforeService);
                                                        SharedHelper.putKey(context, "before_image", AppHelper.getImageUrl(jObj.optString("before_image")));
                                                    }

                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                                CurrentStatus = "PICKEDUP";
                                                lnrCancelTrip.setVisibility(View.GONE);
                                                imgGotoPhoto.setVisibility(View.GONE);
                                            } else if (statusResponse.optString("status").equals("PICKEDUP")) {
                                                setValuesTo_ll_03_contentLayer_service_flow(statusResponses);
                                                serviceFlow(FLOW_SERVICE_PICKED);
                                                SharedHelper.putKey(context, "status", "onride");
                                                btnServiceStatus.setText(thisActivity.getResources().getString(R.string.end_service));
                                                txtServiceComments.setVisibility(View.VISIBLE);
                                                btnServiceStatus.setVisibility(View.VISIBLE);
                                                lnrAfterService.setVisibility(View.VISIBLE);
                                                Long diff = getFormatedDateTime(statusResponse.optString("started_at"));
                                                long diffSeconds = diff / 1000 % 60;
                                                long diffMinutes = diff / (60 * 1000) % 60;
                                                long diffHours = diff / (60 * 60 * 1000);

                                                Log.v("", "Hours:" + diffHours + " ==Minutes:" + diffMinutes + " == Seconds" + diffSeconds);
                                                lblTimerText.setBase(SystemClock.elapsedRealtime() - (diffHours * 3600000 + diffMinutes * 60000 + diffSeconds * 1000));

                                                lblBeforeService.setVisibility(View.VISIBLE);
                                                try {
                                                    JSONObject jObj = new JSONObject(statusResponse.toString());
                                                    if (jObj.optString("before_image") != null && !jObj.optString("before_image").equalsIgnoreCase("null")) {
                                                        Picasso.with(context).load(AppHelper.getImageUrl(jObj.optString("before_image")))
                                                                .memoryPolicy(MemoryPolicy.NO_CACHE).placeholder(R.drawable.no_image).error(R.drawable.no_image).into(imgBeforeService);
                                                        SharedHelper.putKey(context, "before_image", AppHelper.getImageUrl(jObj.optString("before_image")));
                                                        lblBeforeService.setVisibility(View.VISIBLE);
                                                    } else {
                                                        SharedHelper.putKey(context, "before_image", "");
                                                    }

                                                    if (!jObj.optString("before_comment").equalsIgnoreCase("null")) {
                                                        SharedHelper.putKey(context, "before_comment", jObj.optString("before_comment"));
                                                        //imgBeforeComments.setVisibility(View.VISIBLE);
                                                        lblBeforeTxtComments.setText(SharedHelper.getKey(context, "before_comment"));
                                                    }

                                                    if (!jObj.optString("after_comment").equalsIgnoreCase("null")) {
                                                        SharedHelper.putKey(context, "after_comment", jObj.optString("after_comment"));
                                                        lblAfterTxtComments.setText(SharedHelper.getKey(context, "after_comment"));
                                                        //imgAfterComments.setVisibility(View.VISIBLE);
                                                    }

                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }

                                                btnStatus.setText(getString(R.string.tap_when_dropped));
                                                CurrentStatus = "DROPPED";
                                                lnrCancelTrip.setVisibility(View.GONE);
                                                imgGotoPhoto.setVisibility(View.GONE);
                                            } else if (statusResponse.optString("status").equals("DROPPED")
                                                    && statusResponse.optString("paid").equals("0")) {
                                                setValuesTo_l2_03_contentLayer_service_flow(statusResponses);
                                                setValuesTo_ll_04_contentLayer_payment(statusResponses);
                                                serviceFlow(FLOW_INVOICE);
                                                lnrBeforeService.setVisibility(View.VISIBLE);
                                                lnrAfterService.setVisibility(View.VISIBLE);
                                                imgGotoPhoto.setVisibility(View.GONE);
                                                imgCurrentLocation.setVisibility(View.VISIBLE);
                                                lblBeforeService.setVisibility(View.VISIBLE);
                                                lblAfterService.setVisibility(View.VISIBLE);
                                                btnServiceStatus.setVisibility(View.GONE);
                                                txtServiceComments.setVisibility(View.GONE);

                                                String started = statusResponse.optString("started_at");
                                                String finished = statusResponse.optString("finished_at");
                                                lblTimerTxt1.setText(getDiffDateTime(started, finished));

                                                try {
                                                    JSONObject jObj = new JSONObject(statusResponse.toString());
                                                    if (jObj.optString("before_image") != null && !jObj.optString("before_image").equalsIgnoreCase("null")) {
                                                        Picasso.with(context).load(AppHelper.getImageUrl(jObj.optString("before_image")))
                                                                .memoryPolicy(MemoryPolicy.NO_CACHE).placeholder(R.drawable.no_image).error(R.drawable.no_image).into(imgBeforeService);
                                                        SharedHelper.putKey(context, "before_image", AppHelper.getImageUrl(jObj.optString("before_image")));
                                                    } else {
                                                        SharedHelper.putKey(context, "before_image", "");
                                                    }

                                                    if (jObj.optString("after_image") != null && !jObj.optString("after_image").equalsIgnoreCase("null")) {
                                                        Picasso.with(context).load(AppHelper.getImageUrl(jObj.optString("after_image")))
                                                                .memoryPolicy(MemoryPolicy.NO_CACHE).placeholder(R.drawable.no_image).error(R.drawable.no_image).into(imgAfterService);
                                                        SharedHelper.putKey(context, "after_image", AppHelper.getImageUrl(jObj.optString("after_image")));
                                                    } else {
                                                        SharedHelper.putKey(context, "after_image", "");
                                                    }

                                                    if (!jObj.optString("before_comment").equalsIgnoreCase("null")) {
                                                        SharedHelper.putKey(context, "before_comment", jObj.optString("before_comment"));
                                                        //imgBeforeComments.setVisibility(View.VISIBLE);
                                                        lblBeforeTxtComments.setText(SharedHelper.getKey(context, "before_comment"));
                                                    }

                                                    if (!jObj.optString("after_comment").equalsIgnoreCase("null")) {
                                                        SharedHelper.putKey(context, "after_comment", jObj.optString("after_comment"));
                                                        //imgAfterComments.setVisibility(View.VISIBLE);
                                                        lblAfterTxtComments.setText(SharedHelper.getKey(context, "after_comment"));
                                                    }
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }

                                                SharedHelper.putKey(context, "status", "onride");
                                                btnStatus.setText(getString(R.string.tap_when_paid));
                                                CurrentStatus = "COMPLETED";
                                            } else if (statusResponse.optString("status").equals("DROPPED") && statusResponse.optString("paid").equals("1")) {
                                                setValuesTo_ll_05_contentLayer_feedback(statusResponses);
                                                serviceFlow(FLOW_RATING);
                                                Long diff = getFormatedDateTime(statusResponse.optString("started_at"));
                                                long diffSeconds = diff / 1000 % 60;
                                                long diffMinutes = diff / (60 * 1000) % 60;
                                                long diffHours = diff / (60 * 60 * 1000);

                                                Log.v("", "Hours:" + diffHours + " ==Minutes:" + diffMinutes + " == Seconds" + diffSeconds);
                                                lblTimerText.setBase(SystemClock.elapsedRealtime() - (diffHours * 3600000 + diffMinutes * 60000 + diffSeconds * 1000));

                                                SharedHelper.putKey(context, "status", "onride");
                                                imgGotoPhoto.setVisibility(View.GONE);
                                                btnStatus.setText(getString(R.string.rate_user));
                                                CurrentStatus = "RATE";
                                            } else if (statusResponse.optString("status").equals("COMPLETED")) {

                                                setValuesTo_ll_05_contentLayer_feedback(statusResponses);
                                                serviceFlow(FLOW_RATING);
                                                imgGotoPhoto.setVisibility(View.GONE);
                                                SharedHelper.putKey(context, "status", "onride");
                                                btnStatus.setText(getString(R.string.rate_user));
                                                CurrentStatus = "RATE";
                                                lblAfterService.setText("");
                                                lblBeforeService.setText("");


                                            } else if (statusResponse.optString("status").equals("SCHEDULED")) {
                                                if (mMap != null) {
                                                    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                                        return;
                                                    }
                                                    mMap.clear();
                                                }
                                                serviceFlow(FLOW_HOME);
                                                CurrentStatus = "ONLINE";
                                                PreviousStatus = "NULL";
                                                lblAfterService.setText("");
                                                lblBeforeService.setText("");
                                                Picasso.with(context).load("").placeholder(R.drawable.no_image).error(R.drawable.no_image).into(imgAfterService);
                                                Picasso.with(context).load("").placeholder(R.drawable.no_image).error(R.drawable.no_image).into(imgBeforeService);
                                                utils.print("statusResponse", "null");
                                            }
                                        }
                                    } else {
                                        if (mMap != null) {
                                            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                                return;
                                            }
                                            timerCompleted = false;
                                            mMap.clear();
                                            if (mPlayer != null && mPlayer.isPlaying()) {
                                                mPlayer.stop();
                                                mPlayer = null;
                                                countDownTimer.cancel();
                                            }

                                        }

                                        serviceFlow(FLOW_HOME);
                                        CurrentStatus = "ONLINE";
                                        PreviousStatus = "NULL";
                                        utils.print("statusResponse", "null");
                                    }

                                } else {
                                    SharedHelper.putKey(context, "status", "completed");
                                    timerCompleted = false;
                                    utils.print("response", "null");
                                    if (mMap != null) {
                                        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                            return;
                                        }
                                    }
                                    if (mPlayer != null && mPlayer.isPlaying()) {
                                        mPlayer.stop();
                                        mPlayer = null;
                                        countDownTimer.cancel();
                                    }

                                    if (SERVICE_FLOW != 1) {
                                        mMap.clear();
                                    }
                                    serviceFlow(FLOW_HOME);
                                    CurrentStatus = "ONLINE";
                                    PreviousStatus = "NULL";
                                    utils.print("statusResponse", "null");
                                }
                            }
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        utils.print("Error", error.toString());
                        errorHandler(error);
                        timerCompleted = false;
                        serviceFlow(FLOW_HOME);
                        CurrentStatus = "ONLINE";
                        PreviousStatus = "NULL";

                        if (mPlayer != null && mPlayer.isPlaying()) {
                            mPlayer.stop();
                            mPlayer = null;
                            countDownTimer.cancel();
                        }
                    }
                }) {
                    @Override
                    public java.util.Map<String, String> getHeaders() throws AuthFailureError {
                        HashMap<String, String> headers = new HashMap<>();
                        headers.put("X-Requested-With", "XMLHttpRequest");
                        headers.put("Authorization", "Bearer " + token);
                        return headers;
                    }
                };
                TutorApplication.getInstance().addToRequestQueue(jsonObjectRequest);
            } else {
                displayMessage(getString(R.string.oops_connect_your_internet));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // dialog for network connection
    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(getString(R.string.connect_to_network))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.connect_to_wifi), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                    }
                });
        if (alert == null) {
            alert = builder.create();
            alert.show();
        }
    }

    // set up settings for map
    @SuppressWarnings("MissingPermission")
    private void setupMap() {

        if (mMap != null) {
//            mMap.setMyLocationEnabled(true);
//            mMap.getUiSettings().setMyLocationButtonEnabled(true);
            mMap.getUiSettings().setCompassEnabled(false);
            mMap.setBuildingsEnabled(true);
            mMap.getUiSettings().setRotateGesturesEnabled(false);
            mMap.getUiSettings().setTiltGesturesEnabled(false);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (context instanceof ServiceFlowFragment.ServiceFlowFgmtListener) {
            mListener = (ServiceFlowFragment.ServiceFlowFgmtListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement ServiceFlowFgmtListener");
        }

        try {

            NotificationManager notificationManager = (NotificationManager) getActivity().getSystemService(NOTIFICATION_SERVICE);
            notificationManager.cancelAll();

            if (handleCheckStatus != null) {
                handleCheckStatus.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //call function
                        if (!SharedHelper.getKey(getContext(), Utilities.skip_login).equalsIgnoreCase(Utilities.skipped)) {
                            checkStatus();
                            handleCheckStatus.postDelayed(this, 3000);
                        }
                    }
                }, 3000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        if (mPlayer != null && mPlayer.isPlaying()) {
            mPlayer.stop();
            mPlayer = null;
        }
    }

    private void update(final String status, String id) {
        customDialog = new CustomDialog(context);
        customDialog.setCancelable(false);
        customDialog.show();
        if (status.equals("ONLINE")) {
            JSONObject param = new JSONObject();
            try {
                param.put("service_status", "offline");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLHelper.UPDATE_AVAILABILITY_API, param, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.e(TAG, "onResponse: " + response);
                    customDialog.dismiss();
                    if (response != null) {
                        if (response.optJSONObject("service").optString("status").equalsIgnoreCase("offline")) {
                            Toast.makeText(context, "You are now offline!", Toast.LENGTH_SHORT).show();
                            goOffline();
                        } else {
                            displayMessage(getString(R.string.something_went_wrong));
                        }
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    customDialog.dismiss();
                    utils.print("Error", error.toString());
                    errorHandler(error);
                }
            }) {
                @Override
                public java.util.Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("X-Requested-With", "XMLHttpRequest");
                    headers.put("Authorization", "Bearer " + token);
                    return headers;
                }
            };
            TutorApplication.getInstance().addToRequestQueue(jsonObjectRequest);
        } else {
            String url;
            JSONObject param = new JSONObject();
            if (status.equals("RATE")) {
                url = URLHelper.BASE_URL + "/api/provider/trip/" + id + "/rate";
                try {
                    if (feedBackRating.equalsIgnoreCase("0")) feedBackRating = "1";


                    param.put("rating", feedBackRating);
                    param.put("comment", txtComments.getText().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                utils.print("Input", param.toString());
            } else {
                url = URLHelper.BASE_URL + "/api/provider/trip/" + id;
                try {
                    param.put("_method", "PATCH");
                    param.put("status", status);
                    if (status.equalsIgnoreCase("DROPPED")) {
                        param.put("latitude", crt_lat);
                        param.put("longitude", crt_lng);
//                        param.put("distance", LocationTracking.distance * 0.001);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, param, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    customDialog.dismiss();
                    if (response.optJSONObject("requests") != null) {
                        utils.print("request", response.optJSONObject("requests").toString());
                    }

                    if (status.equals("RATE")) {
                        serviceFlow(FLOW_HOME);
                        Toast.makeText(context, "Rated successfully!", Toast.LENGTH_SHORT).show();
                        if (!crt_lat.equalsIgnoreCase("") && !crt_lng.equalsIgnoreCase("")) {
                            LatLng myLocation = new LatLng(Double.parseDouble(crt_lat), Double.parseDouble(crt_lng));
                            CameraPosition cameraPosition = new CameraPosition.Builder().target(myLocation).zoom(14).build();
                            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                            mMap.clear();
                        }
                    }
                    if (status.equalsIgnoreCase("DROPPED")) {
//                        Toast.makeText(context, "Your service has completed successfully!", Toast.LENGTH_SHORT).show();
                    }

                    if (status.equalsIgnoreCase("PICKEDUP")) {
//                        Toast.makeText(context, "Your service has started successfully!", Toast.LENGTH_SHORT).show();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    customDialog.dismiss();
                    utils.print("Error", error.toString());
                    //errorHandler(error);
                }
            }) {
                @Override
                public java.util.Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("X-Requested-With", "XMLHttpRequest");
                    headers.put("Authorization", "Bearer " + token);
                    return headers;
                }
            };
            TutorApplication.getInstance().addToRequestQueue(jsonObjectRequest);
        }
    }

    private void clearServicePage() {
        lblAfterService.setText("");
        lblBeforeService.setText("");
        txtServiceComments.setText("");
        SharedHelper.putKey(context, "before_image", "");
        SharedHelper.putKey(context, "before_comment", null);
        SharedHelper.putKey(context, "after_image", "");
        SharedHelper.putKey(context, "after_comment", null);
/*
        Picasso.with(context).load("service").placeholder(R.drawable.no_image).error(R.drawable.no_image)
                .into(imgAfterComments);
*/
        Picasso.with(context).load("service").placeholder(R.drawable.no_image).error(R.drawable.no_image)
                .into(imgAfterService);
        Picasso.with(context).load("service").placeholder(R.drawable.no_image).error(R.drawable.no_image)
                .into(imgBeforeService);
/*
        Picasso.with(context).load("service").placeholder(R.drawable.no_image).error(R.drawable.no_image)
                .into(imgBeforeComments);
*/
        lblBeforeTxtComments.setText("");
        lblAfterTxtComments.setText("");
    }

    public void goOnline() {
        customDialog = new CustomDialog(thisActivity);
        customDialog.setCancelable(false);
        customDialog.show();
        JSONObject param = new JSONObject();
        try {
            param.put("service_status", "active");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLHelper.UPDATE_AVAILABILITY_API, param, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (response != null) {
                    try {
                        mCheckOnline = false;
                        customDialog.dismiss();
                        if (response.optJSONObject("service").optString("status").equalsIgnoreCase("active")) {
                            Toast.makeText(context, "You are now online!", Toast.LENGTH_SHORT).show();
                            offlineImg.setVisibility(View.GONE);
                            offlineLayout.setVisibility(View.GONE);
                            btnGoOffline.setText(getString(R.string.go_offline));
                            imgCurrentLocation.setVisibility(View.VISIBLE);
                        } else {
                            displayMessage(getString(R.string.something_went_wrong));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    customDialog.dismiss();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                customDialog.dismiss();
                utils.print("Error", error.toString());
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
                            json = TutorApplication.trimMessage(new String(response.data));
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
            public java.util.Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("X-Requested-With", "XMLHttpRequest");
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };
        TutorApplication.getInstance().addToRequestQueue(jsonObjectRequest);
    }

    public void cancelRequest(String id) {

        customDialog = new CustomDialog(context);
        customDialog.setCancelable(false);
        customDialog.show();
        JSONObject object = new JSONObject();
        try {
            object.put("id", id);
            Log.e("", "request_id" + id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLHelper.CANCEL_REQUEST_API, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                customDialog.dismiss();
                utils.print("CancelRequestResponse", response.toString());
                mMap.clear();
                serviceFlow(FLOW_HOME);
                CurrentStatus = "ONLINE";
                PreviousStatus = "NULL";
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
                                e.printStackTrace();
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
                        e.printStackTrace();
                    }
                } else {
                    displayMessage(getString(R.string.please_try_again));
                }
            }
        }) {
            @Override
            public java.util.Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("X-Requested-With", "XMLHttpRequest");
                headers.put("Authorization", "Bearer " + SharedHelper.getKey(context, "access_token"));
                Log.e("", "Access_Token" + SharedHelper.getKey(context, "access_token"));
                return headers;
            }
        };
        TutorApplication.getInstance().addToRequestQueue(jsonObjectRequest);
    }

    private void handleIncomingRequest(final String status, String id) {
        customDialog = new CustomDialog(context);
        customDialog.setCancelable(false);
        customDialog.show();
        String url = URLHelper.BASE_URL + "/api/provider/trip/" + id;

        if (status.equals("accept")) {
            method = Request.Method.POST;
        } else {
            method = Request.Method.DELETE;
        }

        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(method, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                customDialog.dismiss();
                if (status.equals("accept")) {
                    Toast.makeText(context, "You have accepted the request!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "You have rejected the request!", Toast.LENGTH_SHORT).show();
                    if (mMap != null) {
                        mMap.clear();
                    }
                }
                if (response.optJSONObject("requests") != null) {
                    utils.print("request", response.optJSONObject("requests").toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                customDialog.dismiss();
                utils.print("Error", error.toString());
                //errorHandler(error);
                mMap.clear();
                //Toast.makeText(context, "You have accepted the request!", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            public java.util.Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("X-Requested-With", "XMLHttpRequest");
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };
        TutorApplication.getInstance().addToRequestQueue(jsonObjectRequest);
    }

    public void errorHandler(VolleyError error) {
        utils.print("Error", error.toString());
        String json = null;
        String Message;
        NetworkResponse response = error.networkResponse;
        if (response != null && response.data != null) {

            try {
                JSONObject errorObj = new JSONObject(new String(response.data));
                utils.print("ErrorHandler", "" + errorObj.toString());
                if (response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500) {
                    try {
                        displayMessage(errorObj.optString("message"));
                    } catch (Exception e) {
                        displayMessage(getString(R.string.something_went_wrong));
                    }
                } else if (response.statusCode == 401) {
                    GoToBeginActivity();
                } else if (response.statusCode == 422) {
                    json = TutorApplication.trimMessage(new String(response.data));
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

    private void setValuesTo_ll_01_contentLayer_accept_or_reject_now(JSONArray status) {
        JSONObject statusResponse = new JSONObject();
        JSONObject statusServiceType = new JSONObject();
        JSONObject statusSubServiceType = new JSONObject();
        try {
            statusResponse = status.getJSONObject(0).getJSONObject("request");
            statusServiceType = statusResponse.getJSONObject("service_type");
            statusSubServiceType = statusResponse.getJSONObject("subservice_type");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        strUserId = statusResponse.optString("user_id");
        try {
            if (!status.getJSONObject(0).optString("time_left_to_respond").equals("")) {
                count = status.getJSONObject(0).getString("time_left_to_respond");
            } else {
                count = "0";
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Double lat = null;
        Double lng = null;
        if (!statusResponse.optString("s_latitude").equalsIgnoreCase("")) {
            lat = Double.parseDouble(statusResponse.optString("s_latitude"));
        }
        if (!statusResponse.optString("s_longitude").equalsIgnoreCase("")) {
            lng = Double.parseDouble(statusResponse.optString("s_longitude"));
        }
        if (statusResponse.optString("s_address") != null) {
            txtScheduleAddress.setText(statusResponse.optString("s_address"));
        } else {
            txtScheduleAddress.setText(statusResponse.optString("s_latitude") + ", " + statusResponse.optString("s_longitude"));
        }
        txtScheduleType.setText(statusServiceType.optString("provider_name"));
        txtSubServiceType.setText(statusSubServiceType.optString("name"));
        if (!statusResponse.optString("description").equals("")) {
            description_layout.setVisibility(View.VISIBLE);
            txtDesc.setText(statusResponse.optString("description"));
        } else {
            description_layout.setVisibility(View.GONE);
            txtDesc.setText("No description");

        }

        strServiceType = statusServiceType.optString("name");

        countDownTimer = new CountDownTimer(Integer.parseInt(count) * 1000, 1000) {

            @SuppressLint("SetTextI18n")
            public void onTick(long millisUntilFinished) {
                txtTimer.setText("00:" + millisUntilFinished / 1000);

                if (mPlayer == null) {
                    mPlayer = MediaPlayer.create(context, R.raw.alert_tone);
                } else {
                    if (!mPlayer.isPlaying()) {
                        mPlayer.start();
                    }
                }
                isRunning = true;
                timerCompleted = false;

            }

            public void onFinish() {
                txtTimer.setText("00:00");
                if (mMap != null) {
                    mMap.clear();
                }
                if (scaleDown.isRunning()) {
                    scaleDown.end();
                }
                if (mPlayer != null && mPlayer.isPlaying()) {
                    mPlayer.stop();
                    mPlayer = null;
                }
                Toast.makeText(context, "You have missed the service!", Toast.LENGTH_SHORT).show();
                serviceFlow(FLOW_HOME);
                isRunning = false;
                timerCompleted = true;
                handleIncomingRequest("Reject", request_id);
            }
        };

        countDownTimer.start();
        try {
            if (!statusResponse.optString("schedule_at").trim().equalsIgnoreCase("") &&
                    !statusResponse.optString("schedule_at").equalsIgnoreCase("null")) {
                lnrSchedule.setVisibility(View.VISIBLE);
                String strSchedule = "";
                try {
                    strSchedule = getDate(statusResponse.optString("schedule_at")) + "th " + getMonth(statusResponse.optString("schedule_at"))
                            + " " + getYear(statusResponse.optString("schedule_at")) + " at " + getTime(statusResponse.optString("schedule_at"));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                txtSchedule.setText("Scheduled at : " + strSchedule);
            } else {
                lnrSchedule.setVisibility(View.GONE);
            }

            JSONObject user = statusResponse.getJSONObject("user");
            if (user != null) {
                if (!user.optString("picture").equals("") && !user.optString("picture").equals("null")) {
                    Picasso.with(context).load(AppHelper.getImageUrl(user.getString("picture")))
                            .memoryPolicy(MemoryPolicy.NO_CACHE).placeholder(R.drawable.ic_dummy_user).error(R.drawable.ic_dummy_user).into(imgUser01);
                } else {
                    imgUser01.setImageResource(R.drawable.ic_dummy_user);
                }

                txtUserName01.setText(user.optString("first_name") + " " + user.optString("last_name"));
                if (statusResponse.getJSONObject("user").getString("rating") != null) {
                    ratingUser01.setRating(Float.valueOf(user.getString("rating")));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setValuesTo_ll_03_contentLayer_service_flow(JSONArray status) {
        JSONObject statusResponse = new JSONObject();
        try {
            statusResponse = status.getJSONObject(0).getJSONObject("request");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            JSONObject user = statusResponse.getJSONObject("user");
            SharedHelper.putKey(context, "user_name", user.optString("first_name") + " " + user.optString("last_name"));
            if (user != null) {
                if (!user.optString("mobile").equals("null")) {
                    SharedHelper.putKey(context, "provider_mobile_no", "" + user.optString("mobile"));
                } else {
                    SharedHelper.putKey(context, "provider_mobile_no", "");
                }

                if (!user.optString("picture").equals("") && !user.optString("picture").equals("null")) {
                    Picasso.with(context).load(AppHelper.getImageUrl(
                            user.getString("picture"))).memoryPolicy(MemoryPolicy.NO_CACHE).placeholder(R.drawable.ic_dummy_user).error(R.drawable.ic_dummy_user)
                            .into(imgUser02);
                } else {
                    imgUser02.setImageResource(R.drawable.ic_dummy_user);
                }
                txtUserName02.setText(user.optString("first_name") + " " + user.optString("last_name"));
                if (statusResponse.getJSONObject("user").getString("rating") != null) {
                    ratingUser02.setRating(Float.valueOf(user.getString("rating")));
                } else {
                    ratingUser02.setRating(0);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setValuesTo_l2_03_contentLayer_service_flow(JSONArray status) {
        JSONObject statusResponse = new JSONObject();
        try {
            statusResponse = status.getJSONObject(0).getJSONObject("request");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JSONObject statusServiceType = new JSONObject();
        try {
            statusResponse = statusResponses.getJSONObject(0).getJSONObject("request");
            statusServiceType = statusResponse.getJSONObject("service_type");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        strServiceType = statusServiceType.optString("name");
        strUserId = statusResponse.optString("user_id");
        try {
            JSONObject user = statusResponse.getJSONObject("user");
            if (user != null) {
                if (!user.optString("mobile").equals("null")) {
                    SharedHelper.putKey(context, "provider_mobile_no", "" + user.optString("mobile"));
                } else {
                    SharedHelper.putKey(context, "provider_mobile_no", "");
                }

                if (!user.optString("picture").equals("") && !user.optString("picture").equals("null")) {
                    Picasso.with(context).load(AppHelper.getImageUrl(
                            user.getString("picture"))).memoryPolicy(MemoryPolicy.NO_CACHE).placeholder(R.drawable.ic_dummy_user).error(R.drawable.ic_dummy_user)
                            .into(imgUser03);
                } else {
                    imgUser03.setImageResource(R.drawable.ic_dummy_user);
                }
                txtUserName03.setText(user.optString("first_name") + " " + user.optString("last_name"));
                if (statusResponse.getJSONObject("user").getString("rating") != null) {
                    ratingUser03.setRating(Float.valueOf(user.getString("rating")));
                } else {
                    ratingUser03.setRating(0);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setValuesTo_ll_04_contentLayer_payment(JSONArray status) {
        JSONObject statusResponse = new JSONObject();
        try {
            statusResponse = status.getJSONObject(0).getJSONObject("request");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            lblBasePrice.setText(SharedHelper.getKey(context, "currency") + "" + statusResponse.getJSONObject("payment").optString("fixed"));
            lblHourlyFareInvoice.setText(SharedHelper.getKey(context, "currency") + "" + statusResponse.getJSONObject("payment").optString("time_price"));
            lblTaxFare.setText(SharedHelper.getKey(context, "currency") + "" + statusResponse.getJSONObject("payment").optString("tax"));
            lblTotal.setText(SharedHelper.getKey(context, "currency") + "" + statusResponse.getJSONObject("payment").optString("total"));

            lblWalletDetection.setText(SharedHelper.getKey(context, "currency") + "" + statusResponse.getJSONObject("payment").optString("wallet"));
            lblAmountPaid.setText(SharedHelper.getKey(context, "currency") + "" + statusResponse.getJSONObject("payment").optString("total"));
            payableText.setVisibility(View.VISIBLE);
            payableAmountText.setText(SharedHelper.getKey(context, "currency") + "" + statusResponse.getJSONObject("payment").optString("payable"));
            payableAmountText.setVisibility(View.VISIBLE);
            lblPaymentTypeInvoice.setText(statusResponse.getString("payment_mode"));
            lblPromotionApplied.setText(SharedHelper.getKey(context, "currency") + "" + statusResponse.getJSONObject("payment").optString("commision"));
            if (statusResponse.getString("payment_mode").equals("CASH")) {
                imgPaymentTypeInvoice.setImageResource(R.drawable.payment_icon);
            } else {
                imgPaymentTypeInvoice.setImageResource(R.drawable.visa);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setValuesTo_ll_05_contentLayer_feedback(JSONArray status) {
        ratingProviderRate.setRating(1.0f);
        feedBackRating = "1";
        ratingProviderRate.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            public void onRatingChanged(RatingBar ratingBar, float rating,
                                        boolean fromUser) {
                utils.print("rating", rating + "");
                if (rating < 1.0f) {
                    ratingProviderRate.setRating(1.0f);
                    feedBackRating = "1";
                }
                feedBackRating = String.valueOf((int) rating);
            }
        });
        JSONObject statusResponse = new JSONObject();
        try {
            statusResponse = status.getJSONObject(0).getJSONObject("request");
            JSONObject user = statusResponse.getJSONObject("user");
            lblProviderNameRate.setText(getString(R.string.rate_provider_lbl) + " " + user.optString("first_name") + " " + user.optString("last_name"));
            if (user != null) {
                if (!user.optString("picture").equals("") && !user.optString("picture").equals("null")) {
//                    new DownloadImageTask(img05User).execute(user.getString("picture"));
                    Picasso.with(context).load(AppHelper.getImageUrl(
                            user.getString("picture"))).memoryPolicy(MemoryPolicy.NO_CACHE).placeholder(R.drawable.ic_dummy_user).error(R.drawable.ic_dummy_user)
                            .into(imgProviderRate);
                } else {
                    imgProviderRate.setImageResource(R.drawable.ic_dummy_user);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        feedBackComment = txtComments.getText().toString();
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
        SharedHelper.putKey(context, "loggedIn", getString(R.string.False));
        Intent mainIntent = new Intent(context, ActivityPassword.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(mainIntent);
        getActivity().finish();
    }

    public void goOffline() {
        btnGoOffline.setText(getString(R.string.go_online));
        offlineImg.setVisibility(View.VISIBLE);
        offlineLayout.setVisibility(View.VISIBLE);
        imgCurrentLocation.setVisibility(View.GONE);
    }

    @Override
    public void onDestroy() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        if (mPlayer != null && mPlayer.isPlaying()) {
            mPlayer.stop();
            mPlayer = null;
        }
        if (handleCheckStatus != null) {
            handleCheckStatus.removeCallbacksAndMessages(null);
        }
        super.onDestroy();
    }

    @Override
    public void onPause() {
        try {
            if (handleCheckStatus != null) {
                handleCheckStatus.removeCallbacksAndMessages(null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onPause();

    }

    private void dispatchTakePictureIntent() {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
            if (!hasPermissions(context, PERMISSIONS)) {
                ActivityCompat.requestPermissions(thisActivity, PERMISSIONS, PERMISSION_ALL);
            } else {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
        } else
            try {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                // Ensure that there's a camera activity to handle the intent
                if (takePictureIntent.resolveActivity(context.getPackageManager()) != null) {
                    // Create the File where the photo should go
                    File photoFile = null;
                    try {
                        photoFile = createImageFile();
                    } catch (IOException ex) {
                        // Error occurred while creating the File
                        return;
                    }
                    // Continue only if the File was successfully created
                    if (photoFile != null) {
                        Uri photoURI = FileProvider.getUriForFile(context,
                                BuildConfig.APPLICATION_ID + ".provider",
                                createImageFile());
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                        startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
       /* Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        File output = new File(dir, "CameraContentDemo.jpg");
        image_uri = Uri.fromFile(output);
        cameraIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(cameraIntent, REQUEST_TAKE_PHOTO);*/
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM), "Camera");
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == MY_PERMISSIONS_REQUEST_LOCATION) {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                // permission was granted, yay! Do the
                // location-related task you need to do.
                if (ContextCompat.checkSelfPermission(context,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {

                    if (mGoogleApiClient == null) {
                        buildGoogleApiClient();
                    }
//                    mMap.setMyLocationEnabled(true);
                }

            } else {

                // permission denied, boo! Disable the
                // functionality that depends on this permission.
                Toast.makeText(context, "permission denied", Toast.LENGTH_LONG).show();
            }
            return;
        }

        if (requestCode == MY_PERMISSIONS_ACCESS_FINE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    //Toast.makeText(SignInActivity.this, "PERMISSION_GRANTED", Toast.LENGTH_SHORT).show();
                    initMap();
                    MapsInitializer.initialize(getActivity());
                } else {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_ACCESS_FINE);
                }
            }
        }

        if (requestCode == PERMISSION_ALL) {
            if (grantResults.length > 0) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED || grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    dispatchTakePictureIntent();
                } else {
                    Toast.makeText(context, "Permission needed!", Toast.LENGTH_SHORT).show();
                }
            }
        }

        if (requestCode == MY_PERMISSIONS_PHONE_CALL) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (SharedHelper.getKey(context, "provider_mobile_no") != null && SharedHelper.getKey(context, "provider_mobile_no") != "") {
                    Intent intentCall = new Intent(Intent.ACTION_CALL);
                    intentCall.setData(Uri.parse("tel:" + SharedHelper.getKey(context, "provider_mobile_no")));
                    startActivity(intentCall);
                } else {
                    Toast.makeText(context, "" + getString(R.string.no_mobile), Toast.LENGTH_SHORT).show();
                }

            }
        }

    }

    private void cropImage(String path) {
        Uri uri = Uri.fromFile(new File(path));

        UCrop.Options options = new UCrop.Options();
        options.setFreeStyleCropEnabled(true);
        options.setToolbarColor(ContextCompat.getColor(thisActivity, R.color.colorPrimary));
        options.setStatusBarColor(ContextCompat.getColor(thisActivity, R.color.colorPrimaryDark));

        UCrop.of(uri, uri)
                .withAspectRatio(1, 1)
                .withMaxResultSize(384, 384)
                .withOptions(options)
                .start(thisActivity);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAPTURE_IMAGE_REQ_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                cropImage(imgPath);
            }
        }
        if (requestCode == UCrop.REQUEST_CROP) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                Uri croppedURI = UCrop.getOutput(data);
                Log.w("Cropped Img", "crop uri" + croppedURI.getPath());
                File file = new File(croppedURI.getPath());
                if (strTag.equalsIgnoreCase("save_before")) {
                    mFileBefore = file;
                    imgBeforeService.setImageURI(croppedURI);
                } else {
                    mFileAfter = file;
                    imgAfterService.setImageURI(croppedURI);
                }
            }
        }
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == Activity.RESULT_OK) {
            Uri imageUri = Uri.parse(mCurrentPhotoPath);
            File file = new File(imageUri.getPath());
            try {
                InputStream ims = null;
                try {
                    ims = new FileInputStream(file);
                    Bitmap bitmap = getScaledBitmap(BitmapFactory.decodeStream(ims));
                    if (bitmap != null) {
                        if (strTag.equalsIgnoreCase("save_before")) {
                            mFileBefore = file;
                            imgBeforeService.setImageBitmap(bitmap);
                        } else {
                            mFileAfter = file;
                            imgAfterService.setImageBitmap(bitmap);
                        }
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

            } catch (Exception e) {
                return;
            }

            // ScanFile so it will be appeared on Gallery
            MediaScannerConnection.scanFile(context,
                    new String[]{imageUri.getPath()}, null,
                    new MediaScannerConnection.OnScanCompletedListener() {
                        public void onScanCompleted(String path, Uri uri) {
                        }
                    });
        }
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            photo.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream);
            if (strTag.equalsIgnoreCase("save_before")) {
                imgBeforeService.setImageBitmap(photo);
            } else {
                imgAfterService.setImageBitmap(photo);
            }
        }
        if (requestCode == CODE_DRAW_OVER_OTHER_APP_PERMISSION) {
            //Check if the permission is granted or not.
            if (resultCode == RESULT_OK) {
                getActivity().startService(new Intent(context, FloatingViewService.class));
                getActivity().finish();
            } else { //Permission is not available
                Toast.makeText(context,
                        "Draw over other app permission not available. Closing the application",
                        Toast.LENGTH_SHORT).show();

                getActivity().finish();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    Bitmap getScaledBitmap(Bitmap mBitmap) {
        Bitmap scaled = null;
        try {
            int nh = (int) (mBitmap.getHeight() * (512.0 / mBitmap.getWidth()));
            scaled = Bitmap.createScaledBitmap(mBitmap, 512, nh, true);
            return scaled;
        } catch (Exception error) {
            error.printStackTrace();
        }
        return scaled;
    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(thisActivity,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(context)
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, please accept to use location functionality")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(thisActivity,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(thisActivity,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
        }

    }


    private void setRoutePath() {
        sourceLatLng = new LatLng(srcLatitude, srcLongitude);
        destLatLng = new LatLng(destLatitude, destLongitude);

        String url = getDirectionsUrl(sourceLatLng, destLatLng);
        DownloadTask downloadTask = new DownloadTask();
        // Start downloading json data from Google Directions API
        downloadTask.execute(url);
    }

    private void setUserProviderMarker() {
        if (mMap != null) {
            sourceLatLng = new LatLng(srcLatitude, srcLongitude);
            destLatLng = new LatLng(destLatitude, destLongitude);
            MarkerOptions markerOptions = new MarkerOptions().title("Source")
                    .position(sourceLatLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.user));
            mMap.addMarker(markerOptions);
            MarkerOptions markerOptions1 = new MarkerOptions().title("Destination")
                    .position(destLatLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.provider));

            if (userMarker != null) {
                userMarker.remove();
            }
            if (providerMarker != null) {
                providerMarker.remove();
            }
            userMarker = mMap.addMarker(markerOptions);
            providerMarker = mMap.addMarker(markerOptions1);
        }
    }

    private String getDirectionsUrl(LatLng sourceLatLng, LatLng destLatLng) {

        // Origin of routelng;
        String str_origin = "origin=" + srcLatitude + "," + srcLongitude;
        String str_dest = "destination=" + destLatitude + "," + destLongitude;
        // Sensor enabled
        String sensor = "sensor=false";
        // Waypoints
        String waypoints = "";
        // Building the parameters to the web service
        String key = "key=" + getString(R.string.google_map_api);

        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&" + key;
        // Output format
        String output = "json";
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;
        Log.e("url", url.toString());
        return url;

    }

    public void getAddress(double latitude, double longitude) {
        try {
            Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && addresses.size() > 0) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder();
                for (int j = 0; j < returnedAddress.getMaxAddressLineIndex(); j++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(j)).append("");
                }

                source_address = strReturnedAddress.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Can't able to get the address!.Please try again", Toast.LENGTH_SHORT).show();
        }
    }

    public String getServiceAddress(double latitude, double longitude) {
        StringBuilder strReturnedAddress = null;
        try {
            Geocoder geocoder = new Geocoder(context, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && addresses.size() > 0) {
                Address returnedAddress = addresses.get(0);
                strReturnedAddress = new StringBuilder();
                for (int j = 0; j < returnedAddress.getMaxAddressLineIndex(); j++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(j)).append("");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Can't able to get the address!.Please try again", Toast.LENGTH_SHORT).show();
        }

        String strAddress = "";
        if (strReturnedAddress != null) {
            strAddress = strReturnedAddress.toString();
        }
        return strAddress;
    }

    private void zoomImageFromThumb(final View thumbView, String strImageURL) {
        // If there's an animation in progress, cancel it immediately and proceed with this one.
        if (mCurrentAnimator != null) {
            mCurrentAnimator.cancel();
        }

        // Load the high-resolution "zoomed-in" image.
        final ImageView expandedImageView = (ImageView) view.findViewById(R.id.imgZoomService);

        if (!strImageURL.equalsIgnoreCase("")) {
            Picasso.with(context).load(strImageURL).memoryPolicy(MemoryPolicy.NO_CACHE)
                    .placeholder(R.drawable.no_image).error(R.drawable.no_image).into(expandedImageView);
        }


        // Calculate the starting and ending bounds for the zoomed-in image. This step
        // involves lots of math. Yay, math.
        final Rect startBounds = new Rect();
        final Rect finalBounds = new Rect();
        final Point globalOffset = new Point();

        // The start bounds are the global visible rectangle of the thumbnail, and the
        // final bounds are the global visible rectangle of the container view. Also
        // set the container view's offset as the origin for the bounds, since that's
        // the origin for the positioning animation properties (X, Y).
        thumbView.getGlobalVisibleRect(startBounds);
        view.findViewById(R.id.container).getGlobalVisibleRect(finalBounds, globalOffset);
        view.findViewById(R.id.container).setBackgroundColor(ContextCompat.getColor(context, R.color.black));
        startBounds.offset(-globalOffset.x, -globalOffset.y);
        finalBounds.offset(-globalOffset.x, -globalOffset.y);

        // Adjust the start bounds to be the same aspect ratio as the final bounds using the
        // "center crop" technique. This prevents undesirable stretching during the animation.
        // Also calculate the start scaling factor (the end scaling factor is always 1.0).
        float startScale;
        if ((float) finalBounds.width() / finalBounds.height()
                > (float) startBounds.width() / startBounds.height()) {
            // Extend start bounds horizontally
            startScale = (float) startBounds.height() / finalBounds.height();
            float startWidth = startScale * finalBounds.width();
            float deltaWidth = (startWidth - startBounds.width()) / 2;
            startBounds.left -= deltaWidth;
            startBounds.right += deltaWidth;
        } else {
            // Extend start bounds vertically
            startScale = (float) startBounds.width() / finalBounds.width();
            float startHeight = startScale * finalBounds.height();
            float deltaHeight = (startHeight - startBounds.height()) / 2;
            startBounds.top -= deltaHeight;
            startBounds.bottom += deltaHeight;
        }

        // Hide the thumbnail and show the zoomed-in view. When the animation begins,
        // it will position the zoomed-in view in the place of the thumbnail.
        thumbView.setAlpha(0f);
        expandedImageView.setVisibility(View.VISIBLE);


        // Set the pivot point for SCALE_X and SCALE_Y transformations to the top-left corner of
        // the zoomed-in view (the default is the center of the view).
        expandedImageView.setPivotX(0f);
        expandedImageView.setPivotY(0f);

        // Construct and run the parallel animation of the four translation and scale properties
        // (X, Y, SCALE_X, and SCALE_Y).
        AnimatorSet set = new AnimatorSet();
        set
                .play(ObjectAnimator.ofFloat(expandedImageView, View.X, startBounds.left,
                        finalBounds.left))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.Y, startBounds.top,
                        finalBounds.top))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_X, startScale, 1f))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_Y, startScale, 1f));
        set.setDuration(mShortAnimationDuration);
        set.setInterpolator(new DecelerateInterpolator());
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mCurrentAnimator = null;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                mCurrentAnimator = null;
            }
        });
        set.start();
        mCurrentAnimator = set;


        // Upon clicking the zoomed-in image, it should zoom back_letter down to the original bounds
        // and show the thumbnail instead of the expanded image.
        final float startScaleFinal = startScale;

        expandedImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mCurrentAnimator != null) {
                    mCurrentAnimator.cancel();
                }

                // Animate the four positioning/sizing properties in parallel, back_letter to their
                // original values.
                AnimatorSet set = new AnimatorSet();
                set
                        .play(ObjectAnimator.ofFloat(expandedImageView, View.X, startBounds.left))
                        .with(ObjectAnimator.ofFloat(expandedImageView, View.Y, startBounds.top))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView, View.SCALE_X, startScaleFinal))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView, View.SCALE_Y, startScaleFinal));
                set.setDuration(mShortAnimationDuration);
                set.setInterpolator(new DecelerateInterpolator());
                set.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        thumbView.setAlpha(1f);
                        expandedImageView.setImageResource(R.drawable.grey_bg);
                        expandedImageView.setVisibility(View.GONE);
                        mCurrentAnimator = null;
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        thumbView.setAlpha(1f);
//						expandedImageView.setImageResource(android.R.color.transparent);
                        expandedImageView.setImageResource(R.drawable.grey_bg);
                        expandedImageView.setVisibility(View.GONE);
                        mCurrentAnimator = null;
                    }
                });
                set.start();
                mCurrentAnimator = set;
            }
        });
    }

    private void showCommentsDialog(View imgView, String strTag) {
        int[] viewCoords = new int[2];
        imgView.getLocationInWindow(viewCoords);
        final Dialog dialog = new Dialog(thisActivity, R.style.CommentsDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        final View view = LayoutInflater.from(thisActivity).inflate(R.layout.comments_layout, null);
        TextView lblComments = (TextView) view.findViewById(R.id.lblComments);
        if (strTag.equalsIgnoreCase("before")) {
            lblComments.setText("" + SharedHelper.getKey(context, "before_comment"));
        } else {
            lblComments.setText("" + SharedHelper.getKey(context, "after_comment"));
        }
        WindowManager.LayoutParams wmlp = dialog.getWindow().getAttributes();
        int imageX = viewCoords[0];
        int imageY = viewCoords[1];
        wmlp.gravity = Gravity.TOP | Gravity.LEFT;
        wmlp.x = (int) imageX;
        wmlp.y = (int) imageY;
        dialog.setCanceledOnTouchOutside(true);
        dialog.setContentView(view);
        dialog.setCancelable(true);
        dialog.setContentView(view);
        if (dialog.isShowing()) {
            dialog.cancel();
        } else {
            dialog.show();
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

    //check for camera and storage access permissions
    @TargetApi(Build.VERSION_CODES.M)
    private void checkMultiplePermissions(int permissionCode, Context context) {

        String[] PERMISSIONS = {android.Manifest.permission.CAMERA, android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (!hasPermissions(context, PERMISSIONS)) {
            ActivityCompat.requestPermissions((Activity) context, PERMISSIONS, permissionCode);
        } else {
            takePhoto();
        }
    }

    private void takePhoto() {
        imgPath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator
                + SystemClock.currentThreadTimeMillis() + ".jpeg";
        File file = new File(imgPath);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Uri imageUri = FileProvider.getUriForFile(
                thisActivity, thisActivity.getPackageName() + ".provider", file);
        //intent.setDataAndType(imageUri,MediaStore.EXTRA_OUTPUT);
        List<ResolveInfo> resInfoList = thisActivity.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        for (ResolveInfo resolveInfo : resInfoList) {
            String packageName = resolveInfo.activityInfo.packageName;
            thisActivity.grantUriPermission(packageName, imageUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setClipData(ClipData.newRawUri(null, imageUri));
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, CAPTURE_IMAGE_REQ_CODE);
    }

    public interface ServiceFlowFgmtListener {

        public void onServiceFlowLogout();

        public void moveToOfflineFragment();

        public void moveToChatFragment();

        public void handleDrawer();

    }

    // on click
    class OnClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.imgCurrentLocation:
                    if (crt_lat != null && !crt_lat.equalsIgnoreCase("0.0") && crt_lat.length() > 0 && crt_lng != null && !crt_lng.equalsIgnoreCase("0.0") && crt_lng.length() > 0) {
                        LatLng loc = new LatLng(crtLat, crtLng);
                        CameraPosition cameraPosition = new CameraPosition.Builder().target(loc).zoom(16).build();
                        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                    }
                    break;
                case R.id.imgMenu:
                    mListener.handleDrawer();
                    break;
                case R.id.imgChat:
//                    mListener.moveToChatFragment();
                    showCallDialog();
                    break;
                case R.id.imgUser01:
                    Intent showProfileIntent = new Intent(context, ShowProfile.class);
                    showProfileIntent.putExtra("user_id", "" + strUserId);
                    showProfileIntent.putExtra("service_type", "" + strServiceType);
                    startActivity(showProfileIntent);
                    break;
                case R.id.imgUser03:
                    Intent profileIntent = new Intent(context, ShowProfile.class);
                    profileIntent.putExtra("user_id", "" + strUserId);
                    profileIntent.putExtra("service_type", "" + strServiceType);
                    startActivity(profileIntent);
                    break;
                /*case R.id.imgBeforeComments:
                    showCommentsDialog(imgBeforeComments, "before");
                    break;
                case R.id.imgAfterComments:
                    showCommentsDialog(imgAfterComments, "after");
                    break;*/
                case R.id.imgNavigationToSource:
                    Log.e(TAG, "onClick: src_lat and src_lan" + crtLat + "," + crtLng);
                    Log.e(TAG, "onClick: src_lat and src_lan" + crt_lat + "," + crt_lng);
                    Uri naviUri2 = Uri.parse("http://maps.google.com/maps?"
                            + "saddr=" + crt_lat + "," + crt_lng
                            + "&daddr=" + srcLatitude + "," + srcLongitude);

                    Intent intentMap = new Intent(Intent.ACTION_VIEW, naviUri2);
                    intentMap.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                    startActivity(intentMap);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(context)) {
                        //If the draw over permission is not available open the settings screen
                        //to grant the permission.
                        Intent fabintent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                Uri.parse("package:" + context.getPackageName()));
                        startActivityForResult(fabintent, CODE_DRAW_OVER_OTHER_APP_PERMISSION);
                    } else {
                        getActivity().startService(new Intent(context, FloatingViewService.class));
                    }
                    break;
                case R.id.btnServiceStatus:
                    if (SERVICE_FLOW == FLOW_SERVICE_PICKED) {
                        uploadServicePhoto("after", CurrentStatus, request_id);
                    } else {
                        uploadServicePhoto("before", CurrentStatus, request_id);
                    }
                    break;
                case R.id.backArrow:
                    lnrServicePhoto.setVisibility(View.GONE);
                    rytCurrent.setVisibility(View.VISIBLE);
                    imgCurrentLocation.setVisibility(View.VISIBLE);
                    if (SERVICE_FLOW == FLOW_INVOICE) {
                        imgGotoPhoto.setVisibility(View.GONE);
                        imgCurrentLocation.setVisibility(View.VISIBLE);
                        lnrInvoice.setVisibility(View.VISIBLE);
                        imgGotoPhoto.setVisibility(View.GONE);
                    } else if (SERVICE_FLOW == FLOW_SERVICE_PICKED || SERVICE_FLOW == FLOW_ARRIVED) {
                        lnrServiceFlow.setVisibility(View.VISIBLE);
                        imgGotoPhoto.setVisibility(View.GONE);
                    }
                    break;
                case R.id.imgGotoPhoto:
                    lnrServicePhoto.setVisibility(View.VISIBLE);
                    if (SERVICE_FLOW == FLOW_INVOICE) {
                        imgGotoPhoto.setVisibility(View.GONE);
                        imgCurrentLocation.setVisibility(View.VISIBLE);
                        lnrInvoice.setVisibility(View.GONE);
                    } else if (SERVICE_FLOW == FLOW_SERVICE_PICKED) {
                        //lnrServiceFlow.setVisibility(View.GONE);
                        lnrAfterService.setVisibility(View.VISIBLE);
                    } else if (SERVICE_FLOW == FLOW_ARRIVED) {
                        lnrAfterService.setVisibility(View.GONE);
                        //lnrServiceFlow.setVisibility(View.GONE);
                    }
                    break;
                case R.id.imgBeforeService:
                    if (SERVICE_FLOW == FLOW_ARRIVED) {
                        strTag = "save_before";
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            checkMultiplePermissions(REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS, thisActivity);
                        } else {
                            takePhoto();
                        }
                    } else {
                        String img = SharedHelper.getKey(context, "before_image");
                        if (img != null && !img.equalsIgnoreCase("null") && img.length() > 0) {
                            Intent intent = new Intent(getContext(), ShowPicture.class);
                            intent.putExtra("image", img);
                            startActivity(intent);
                        }
                    }
                    break;
                case R.id.imgAfterService:
                    if (SERVICE_FLOW == FLOW_SERVICE_PICKED) {
                        strTag = "save_after";
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            checkMultiplePermissions(REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS, thisActivity);
                        } else {
                            takePhoto();
                        }
                    } else {
                        String img = SharedHelper.getKey(context, "after_image");
                        if (img != null && !img.equalsIgnoreCase("null") && img.length() > 0) {
                            Intent intent = new Intent(getContext(), ShowPicture.class);
                            intent.putExtra("image", img);
                            startActivity(intent);
                        }
                    }
                    break;
                case R.id.btnAccept:
                    if (countDownTimer != null) {
                        countDownTimer.cancel();
                    }
                    if (scaleDown != null && scaleDown.isRunning()) {
                        scaleDown.end();
                    }
                    if (mPlayer != null && mPlayer.isPlaying()) {
                        mPlayer.stop();
                        mPlayer = null;
                    }
                    clearServicePage();
                    handleIncomingRequest("accept", request_id);
                    break;
                case R.id.btnReject:
                    if (countDownTimer != null) {
                        countDownTimer.cancel();
                    }
                    if (scaleDown != null && scaleDown.isRunning()) {
                        scaleDown.end();
                    }
                    if (mPlayer != null && mPlayer.isPlaying()) {
                        mPlayer.stop();
                        mPlayer = null;
                    }
                    handleIncomingRequest("Reject", request_id);
                    break;
                case R.id.lnrCancelTrip:
                    showCancelDialog();
                    break;
                case R.id.btnPayNow:
                    update(CurrentStatus, request_id);
                    break;
                case R.id.btnSubmitReview:
                    update(CurrentStatus, request_id);
                    break;
                case R.id.lnrCall:
//                    showCallDialog();
                    showCallDialog();
                    break;
                case R.id.lnrCall1:
                    showCallDialog();
                    break;
                case R.id.btnGoOffline:
                    if (SharedHelper.getKey(getContext(), Utilities.skip_login).equalsIgnoreCase(Utilities.skipped)) {
                        showGuestDialog().show();
                    } else {
                        if (btnGoOffline.getText().toString().equalsIgnoreCase(getString(R.string.go_offline))) {
                            CurrentStatus = "ONLINE";
                            update(CurrentStatus, request_id);
                        } else {
                            goOnline();
                        }
                    }
                    break;
                case R.id.btnStatus:
                    update(CurrentStatus, request_id);
                    break;
            }
        }
    }

    private class DownloadTask extends AsyncTask<String, Void, String> {

        // Downloading data in non-ui thread

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        private String downloadUrl(String strUrl) throws IOException {
            String data = "";
            InputStream iStream = null;
            HttpURLConnection urlConnection = null;
            try {
                Log.e("Entering dowload url", "entrng");
                URL url = new URL(strUrl);

                // Creating an http connection to communicate with url
                urlConnection = (HttpURLConnection) url.openConnection();

                // Connecting to url
                urlConnection.connect();

                // Reading data from url
                iStream = urlConnection.getInputStream();

                BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

                StringBuffer sb = new StringBuffer();

                String line = "";
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }

                data = sb.toString();

                br.close();

            } catch (Exception e) {

            } finally {
                iStream.close();
                urlConnection.disconnect();
            }
            return data;
        }

        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service

            String data = "";

            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
                Log.e("Entering dwnload task", "download task");
            } catch (Exception e) {
                utils.print("Background Task", e.toString());
            }
            return data;
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.e("Resultmap", result);
            ParserTask parserTask = new ParserTask();
            parserTask.execute(result);
        }
    }

    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);

                DirectionsJSONParser parser = new DirectionsJSONParser();

                // Starts parsing data
                routes = parser.parse(jObject);
                utils.print("routes", routes.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {

            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;
            if (result != null) {
                // Traversing through all the routes
                for (int i = 0; i < result.size(); i++) {
                    points = new ArrayList<LatLng>();
                    lineOptions = new PolylineOptions();

                    // Fetching i-th route
                    List<HashMap<String, String>> path = result.get(i);

                    // Fetching all the points in i-th route
                    for (int j = 0; j < path.size(); j++) {
                        HashMap<String, String> point = path.get(j);

                        double lat = Double.parseDouble(point.get("lat"));
                        double lng = Double.parseDouble(point.get("lng"));
                        LatLng position = new LatLng(lat, lng);

                        points.add(position);
                        utils.print("abcde", points.toString());
                    }

                    // Adding all the points in the route to LineOptions
                    lineOptions.addAll(points);
                    lineOptions.width(5);
                    lineOptions.color(Color.BLACK);
                    if (SERVICE_FLOW != 1) {
                        mMap.clear();
                    }
                    MarkerOptions markerOptions = new MarkerOptions().title("Source")
                            .position(sourceLatLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.user));
                    mMap.addMarker(markerOptions);

                    MarkerOptions markerOptions1 = new MarkerOptions().title("Destination")
                            .position(destLatLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.provider));

                    if (userMarker != null) {
                        userMarker.remove();
                    }
                    if (providerMarker != null) {
                        providerMarker.remove();
                    }
                    userMarker = mMap.addMarker(markerOptions);
                    providerMarker = mMap.addMarker(markerOptions1);
//                    Display display =activity.getWindowManager().getDefaultDisplay();
//                    Point size = new Point();
//                    display.getSize(size);
//                    int width = size.x;
//                    int height = size.y;
//
//                    mMap.setPadding(0, 0, 0, height / 2);

                    LatLngBounds.Builder builder = new LatLngBounds.Builder();
                    LatLngBounds bounds;
                    builder.include(sourceLatLng);
                    builder.include(destLatLng);
                    bounds = builder.build();
                    CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 200, 200, 20);
//                    mMap.moveCamera(cu);
                    mMap.getUiSettings().setMapToolbarEnabled(false);
                    //CameraPosition cameraPosition = new CameraPosition.Builder().target(bounds.getCenter()).zoom(15).build();
                    //mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                }
            }
            // Drawing polyline in the Google Map for the i-th route
            if (lineOptions == null) {
                //Toast.makeText(context, "There is no route", Toast.LENGTH_SHORT).show();

            } else {
                mMap.addPolyline(lineOptions);
            }
        }
    }
}
