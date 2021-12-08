package com.astudent.partner.Activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.pubnub.api.Callback;
import com.pubnub.api.Pubnub;
import com.pubnub.api.PubnubError;
import com.pubnub.api.PubnubException;
import com.astudent.partner.FCM.MyFirebaseMessagingService;
import com.astudent.partner.Helper.CustomDialog;
import com.astudent.partner.Helper.SharedHelper;
import com.astudent.partner.Models.ChatMessage;
import com.astudent.partner.R;
import com.astudent.partner.Utils.ChatView;
import com.astudent.partner.Utils.ClanProTextView;
import com.astudent.partner.keys.PubnubKeys;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by RAJKUMAR
 * on 03-Oct-17.
 */
public class ChatActivity extends AppCompatActivity /*implements View.OnClickListener */{

    public static final String TAG = "ChatActivity";
    SharedPreferences sharedPreferences;
//    CustomCallback callback;
    Context context;
    Pubnub pubnub;
    ImageView imgBack;
    ChatView chatView;
    ClanProTextView username_txt;
    ArrayList<ChatMessage> chatMessageList;
    Gson gson;
    JSONObject messageObject;
    JSONArray msgHistoryObjArray;
    String request_id;
    CustomDialog customDialog;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_fragment);
        context = ChatActivity.this;
        customDialog = new CustomDialog(context);
        request_id = SharedHelper.getKey(context,"request_id");
        Log.e(TAG, "onCreate: Request_id" + request_id);
        MyFirebaseMessagingService.value = 0;
        sharedPreferences = context.getSharedPreferences("details", Context.MODE_PRIVATE);
        gson = new Gson();
        chatView = (ChatView)findViewById(R.id.chat_view);
        username_txt = (ClanProTextView) findViewById(R.id.username_txt);
        username_txt.setText(String.format(context.getString(R.string.chat_name),SharedHelper.getKey(context,"user_name")));
        imgBack = (ImageView) findViewById(R.id.backArrow);
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedHelper.putKey(context,"is_open_chat","true");
              finish();
            }
        });
        chatView.setOnSentMessageListener(new ChatView.OnSentMessageListener(){
            @Override
            public boolean sendMessage(ChatMessage chatMessage){
                // perform actual message sending
                Log.e(TAG, "sendMessage: Click" + chatMessage);
                String message = chatMessage.getMessage();
                if (chatMessage.getMessage().length() != 0) {
                    message = gson.toJson(new ChatMessage(message, System.currentTimeMillis(),ChatMessage.Type.SENT,
                            Integer.parseInt(SharedHelper.getKey(context,"id"))));
                    Log.e(TAG, "onClick: Message" + message);
                    try {
                        messageObject = new JSONObject(message);
                    } catch (JSONException je) {
                        Log.d(TAG, je.toString());
                    }
                    pubnub.publish(request_id, publishPushNotification(messageObject), new Callback() {
                        @Override
                        public void successCallback(String channel, Object message) {
                            super.successCallback(channel, message);
                            Log.d("successCallback", "message " + message);
                        }

                        @Override
                        public void errorCallback(String channel, PubnubError error) {
                            super.errorCallback(channel, error);
                            Log.d("errorCallback", "error " + error);
                        }
                    });
                } else {
                    Toast.makeText(context, "Please enter message", Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });

        pubnub = new Pubnub(PubnubKeys.PUBLISH_KEY, PubnubKeys.SUBSCRIBE_KEY);

        try {
            try {
                pubnub.subscribe(request_id, new Callback() {
                    @Override
                    public void successCallback(String channel, Object message) {
                        super.successCallback(channel, message);
                        Log.e(TAG, "successCallback: Message Subscribe" + message);
                        if (message!= null) {
                            try {
                                messageObject = new JSONObject(message.toString());
                            } catch (JSONException je) {
                                Log.d(TAG, je.toString());
                            }
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Log.e(TAG, "run: Receive msg" + messageObject);
                                if (messageObject != null ){
                                    JSONObject msgObject = messageObject.optJSONObject("pn_gcm");
                                    JSONObject dataObject = msgObject.optJSONObject("data");
                                    if (SharedHelper.getKey(context, "id").equalsIgnoreCase(String.valueOf(dataObject.optInt("userId")))) {
                                        chatView.addMessage(new ChatMessage(dataObject.optString("message"), System.currentTimeMillis(),
                                                ChatMessage.Type.SENT, Integer.parseInt(SharedHelper.getKey(context, "id"))));
                                    } else {
                                        chatView.addMessage(new ChatMessage(dataObject.optString("message"), System.currentTimeMillis(),
                                                ChatMessage.Type.RECEIVED, Integer.parseInt(SharedHelper.getKey(context, "id"))));
                                    }
                                }
                            }
                        });
                    }

                    @Override
                    public void successCallback(String channel, Object message, String timetoken) {
                        super.successCallback(channel, message, timetoken);
                    }

                    @Override
                    public void errorCallback(String channel, PubnubError error) {
                        super.errorCallback(channel, error);
                        Log.d("errorCallback", "error " + error);
                    }

                    @Override
                    public void connectCallback(String channel, Object message) {
                        super.connectCallback(channel, message);
                        Log.d("connectCallback", "message " + message);
                    }

                    @Override
                    public void reconnectCallback(String channel, Object message) {
                        super.reconnectCallback(channel, message);
                        Log.d("reconnectCallback", "message " + message);
                    }

                    @Override
                    public void disconnectCallback(String channel, Object message) {
                        super.disconnectCallback(channel, message);
                        Log.d("disconnectCallback", "message " + message);
                    }
                });
            } catch (PubnubException e) {
                e.printStackTrace();
            }
        } catch (Exception pe) {
            Log.d(TAG, pe.toString());
        }

        //Chat History
        pubnub.history(request_id, true, 100, new Callback() {
            @Override
            public void successCallback(String channel, Object message) {
                super.successCallback(channel, message);
                customDialog.dismiss();
                Log.e(TAG, "successCallback: History Messages" + message);
                if (message!= null) {
                    try {
                        JSONArray msgObjArray = new JSONArray(message.toString());
                        for (int i=0;i<1;i++){
                            msgHistoryObjArray  = msgObjArray.getJSONArray(0);
                            for (int j=0;j<msgHistoryObjArray.length();j++){
                                JSONObject msgObj = msgHistoryObjArray.getJSONObject(j);
                                final JSONObject msgDataObj = msgObj.optJSONObject("message");
                                JSONObject msgObject = msgDataObj.optJSONObject("pn_gcm");
                                final JSONObject dataObject = msgObject.optJSONObject("data");
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Log.e(TAG, "run: Receive msg" + dataObject);
                                        if (dataObject != null ){
                                            if (SharedHelper.getKey(context, "id").equalsIgnoreCase(String.valueOf(dataObject.optInt("userId")))) {
                                                chatView.addMessage(new ChatMessage(dataObject.optString("message"), System.currentTimeMillis(),
                                                        ChatMessage.Type.SENT, Integer.parseInt(SharedHelper.getKey(context, "id"))));
                                            } else {
                                                chatView.addMessage(new ChatMessage(dataObject.optString("message"), System.currentTimeMillis(),
                                                        ChatMessage.Type.RECEIVED, Integer.parseInt(SharedHelper.getKey(context, "id"))));
                                            }
                                        }
                                    }
                                });
                            }
                        }
                    } catch (JSONException je) {
                        Log.d(TAG, je.toString());
                    }
                }
            }

            @Override
            public void errorCallback(String channel, PubnubError error) {
                super.errorCallback(channel, error);
                customDialog.dismiss();
                Log.e(TAG, "successCallback: History Messages error" + error);
            }
        });

        pubnub.enablePushNotificationsOnChannel(request_id, SharedHelper.getKey(ChatActivity.this, "device_token"));
    }

    private JSONObject publishPushNotification(JSONObject messageObject) {
        String messageString = messageObject.optString("message");
        long timestamp = messageObject.optLong("timestamp");
        int userId = messageObject.optInt("userId");
        String type = messageObject.optString("type");
        Log.e(TAG, "publishPushNotification: " + messageString);
        try{
            JSONObject pushPayload = new JSONObject();
            JSONObject data = new JSONObject();
            JSONObject notification = new JSONObject();

            notification.put("body",messageString);
            notification.put("title","You got a new message");

            data.put("message",messageString);
            data.put("timestamp",timestamp);
            data.put("userId",userId);
            data.put("type",type);

            pushPayload.put("data",data);
            pushPayload.put("notification",notification);

            JSONObject pngcm = new JSONObject();
            pngcm.put("pn_gcm",pushPayload);
            return pngcm;
        }catch (Exception e){
            Log.e(TAG, "publishPushNotification: "+ "No push generate" );
        }
        return messageObject;
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedHelper.putKey(context,"is_open_chat","false");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharedHelper.putKey(context,"is_open_chat","true");
        pubnub.unsubscribe(request_id);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        SharedHelper.putKey(context,"is_open_chat","true");
    }
}
