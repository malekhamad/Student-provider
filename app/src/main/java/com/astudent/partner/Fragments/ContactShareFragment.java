package com.astudent.partner.Fragments;


import android.Manifest;
import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.astudent.partner.Activity.ActivityPassword;
import com.astudent.partner.Adapter.ContactShareListAdapter;
import com.astudent.partner.Bean.Contact;
import com.astudent.partner.Constant.URLHelper;
import com.astudent.partner.Helper.CustomDialog;
import com.astudent.partner.Helper.SharedHelper;
import com.astudent.partner.Helper.VolleyMultipartRequest;
import com.astudent.partner.R;
import com.astudent.partner.TutorApplication;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class ContactShareFragment extends Fragment implements View.OnClickListener {

    public static final String TAG = "ContactShareFragment";
    public static final int PERMISSION_REQUEST_CONTACT = 100;
    RecyclerView recyclerView;
    Button shareBtn;
    RelativeLayout errorLayout;
    ContactShareListAdapter adapter;
    List<Contact> contacts = new ArrayList<>();
    HashMap<Integer, String> selectedContact = new HashMap<>();
    CustomDialog customDialog;
    private ImageView backArrow;

    public ContactShareFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        customDialog = new CustomDialog(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_contact_share, container, false);
        findViewById(view);
        setOnClickListener();
        askForContactPermission();
        return view;
    }

    private void setOnClickListener() {
        shareBtn.setOnClickListener(this);
        backArrow.setOnClickListener(this);
    }

    private void findViewById(View view) {
        recyclerView = view.findViewById(R.id.recyclerView);
        shareBtn = view.findViewById(R.id.share_btn);
        errorLayout = view.findViewById(R.id.errorLayout);
        backArrow = view.findViewById(R.id.backArrow);
    }

    private void displayContacts() {
        customDialog.show();
        ContentResolver cr = getContext().getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);
        if (cur.getCount() > 0) {
            while (cur.moveToNext()) {
                String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                if (Integer.parseInt(cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        String phoneNo = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        Contact contact = new Contact();
                        contact.setPhone(phoneNo);
                        contact.setName(name);
                        contacts.add(contact);
                        Log.e("Contact", "displayContacts: Name" + name + " Number : " + phoneNo);
                    }
                    pCur.close();
                    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                    adapter = new ContactShareListAdapter(contacts, ContactShareFragment.this);
                    recyclerView.setAdapter(adapter);
                    customDialog.dismiss();
                }
            }
        }
    }

    public void setValueForContactSelection(int position, String phone) {
        Log.e("Phone", "setValueForContactSelection: " + phone);
        selectedContact.put(position, phone);
    }

    public void askForContactPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (getContext().checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Contacts access needed");
                builder.setPositiveButton(android.R.string.ok, null);
                builder.setMessage(getString(R.string.contact_permission_msg));
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @TargetApi(Build.VERSION_CODES.M)
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        requestPermissions(
                                new String[]
                                        {Manifest.permission.READ_CONTACTS}
                                , PERMISSION_REQUEST_CONTACT);
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
                Button pbutton = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
                pbutton.setBackgroundColor(Color.YELLOW);
            } else {
                requestPermissions(new String[]{Manifest.permission.READ_CONTACTS},
                        PERMISSION_REQUEST_CONTACT);
            }
        } else {
            displayContacts();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CONTACT: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    displayContacts();
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {
                    //ToastMaster.showMessage(getActivity(),"No permission for contacts");
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private void sendShareReq() {
        customDialog = new CustomDialog(getContext());
        customDialog.setCancelable(false);
        customDialog.show();
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST,
                URLHelper.SHARE_TO_CONTACT, new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                customDialog.dismiss();

                String res = new String(response.data);
                try {
                    displayMessage(getString(R.string.share_update_success));
                } catch (Exception e) {
                    displayMessage(getString(R.string.something_went_wrong));
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                customDialog.dismiss();
                Log.e(TAG, "" + error);
                if (error.networkResponse == null) {
                    if (error.getClass().equals(TimeoutError.class)) {
                        // Show timeout error message
                        //updateProfileWithoutImage();
                    } else {
                        displayMessage(getString(R.string.something_went_wrong));
                    }
                } else {
                    displayMessage(getString(R.string.something_went_wrong));
                }
            }
        }) {
            @Override
            public Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                for (Map.Entry m : selectedContact.entrySet()) {
                    params.put("mobile[" + m.getKey() + "]", String.valueOf(m.getValue()).trim());
                }
                params.put("type", "provider");
                Log.e(TAG, "getParams: " + params);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("X-Requested-With", "XMLHttpRequest");
                headers.put("Authorization", "Bearer " + SharedHelper.getKey(getContext(), "access_token"));
                return headers;
            }
        };
        TutorApplication.getInstance().addToRequestQueue(volleyMultipartRequest);
    }

    private void goToBeginActivity() {
        Intent intent = new Intent(getActivity(), ActivityPassword.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        getActivity().finish();
    }

    private void displayMessage(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.share_btn:
                if (selectedContact.isEmpty()) {
                    Toast.makeText(getContext(), "Choose atleast one contact to share", Toast.LENGTH_SHORT).show();
                } else {
                    sendShareReq();
                }
                break;
            case R.id.backArrow:
                getFragmentManager().popBackStackImmediate();
                break;
            default:
                break;
        }
    }
}
