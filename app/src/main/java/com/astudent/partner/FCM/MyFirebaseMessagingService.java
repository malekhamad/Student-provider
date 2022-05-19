package com.astudent.partner.FCM;

/**
 * Created by jayakumar on 16/02/17.
 */

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.astudent.partner.Activity.ChatActivity;
import com.astudent.partner.Activity.Home;
import com.astudent.partner.Helper.SharedHelper;
import com.astudent.partner.R;
import com.astudent.partner.Utils.Utilities;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "MyFirebaseMsgService";
    private static final String ACTION_ACCEPT = "accept";
    private static final String ACTION_CANCEL = "cancel";
    String channelId = "tutors_around_partner_channel_id";
    Utilities utils = new Utilities();
    public static int value = 0;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        value++;
        RemoteMessage message = remoteMessage ;
        if (remoteMessage.getData() != null) {
            utils.print(TAG, "From: " + remoteMessage.getFrom());
            utils.print(TAG, "Notification Message Body: " + remoteMessage.getData());
            //Calling method to generate notification
            if (remoteMessage.getData().get("userId") != null){
                if (!SharedHelper.getKey(getApplicationContext(), "id")
                        .equalsIgnoreCase(String.valueOf(remoteMessage.getData().get("userId")))&&
                        SharedHelper.getKey(getApplicationContext(),"is_open_chat").equalsIgnoreCase("true")) {
                    sendChatNotification(remoteMessage.getData().get("message"));
                }
            }else {
                if(message.getData().get("status") != null && Utilities.isAppIsInBackground(getApplicationContext())){
                    if(remoteMessage.getData().get("status").equals("request")){
                        sendRequestNotification();
                    }
                }else {
                    sendNotification(remoteMessage.getData().get("message"));
                }
            }
        } else {
            utils.print(TAG, "FCM Notification failed");
        }
    }

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        SharedHelper.putKey(getApplicationContext(),"device_token",""+s);
        Log.e(TAG,""+s);
    }

    private void sendChatNotification(String messageBody) {
        if (!Utilities.isAppIsInBackground(getApplicationContext())) {
            // app is in foreground, broadcast the push message
            utils.print(TAG, "foreground");
            Intent intent = new Intent(this, ChatActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("push", true);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                    PendingIntent.FLAG_ONE_SHOT);
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this,channelId)
                    .setContentTitle(getString(R.string.app_name))
                    .setContentText(messageBody)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(messageBody))
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setAutoCancel(true)
                    .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                    .setContentIntent(pendingIntent);
            notificationBuilder.setSmallIcon(getNotificationIcon(notificationBuilder), 1);
            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(channelId,
                        "App Notifications",
                        NotificationManager.IMPORTANCE_DEFAULT);
                notificationManager.createNotificationChannel(channel);
            }
            notificationManager.notify(0, notificationBuilder.build());
        } else {
            utils.print(TAG, "background");
            // app is in background, show the notification in notification tray
            if (messageBody.equalsIgnoreCase("New Incoming Service")) {
                Intent intent = new Intent(this, Home.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                        PendingIntent.FLAG_ONE_SHOT);
                NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this,channelId)
                        .setContentTitle(getString(R.string.app_name))
                        .setContentText(messageBody)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(messageBody))
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setAutoCancel(true)
                        .setSound(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.alert_tone))
                        .setContentIntent(pendingIntent);
                notificationBuilder.setSmallIcon(getNotificationIcon(notificationBuilder), 1);
                NotificationManager notificationManager =
                        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    NotificationChannel channel = new NotificationChannel(channelId,
                            "App Notifications",
                            NotificationManager.IMPORTANCE_DEFAULT);
                    notificationManager.createNotificationChannel(channel);
                }
                notificationManager.notify(0, notificationBuilder.build());
            } else {
                Intent intent = new Intent(this, Home.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("push", true);
                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                        PendingIntent.FLAG_ONE_SHOT);
                NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this,channelId)
                        .setContentTitle(getString(R.string.app_name))
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(messageBody))
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                        .setContentIntent(pendingIntent);
                notificationBuilder.setSmallIcon(getNotificationIcon(notificationBuilder), 1);
                NotificationManager notificationManager =
                        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    NotificationChannel channel = new NotificationChannel(channelId,
                            "App Notifications",
                            NotificationManager.IMPORTANCE_DEFAULT);
                    notificationManager.createNotificationChannel(channel);
                }
                notificationManager.notify(0, notificationBuilder.build());
            }
        }
    }

    private void sendRequestNotification() {

            // app is in background, show the notification in notification tray
                Intent intent = new Intent(this, Home.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                        PendingIntent.FLAG_ONE_SHOT);
                NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this,"request_id")
                        .setContentTitle(getString(R.string.app_name))
                        .setContentText("New Incoming Request")
                        .setStyle(new NotificationCompat.BigTextStyle().bigText("New Incoming Request"))
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setAutoCancel(true)
                        .setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 })
                        .setSound(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.alert_tone))
                        .setContentIntent(pendingIntent);
                notificationBuilder.setSmallIcon(getNotificationIcon(notificationBuilder), 1);
                NotificationManager notificationManager =
                        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    AudioAttributes attributes = new AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                            .build();
                    NotificationChannel channel = new NotificationChannel("request_id",
                            "request_notification",
                            NotificationManager.IMPORTANCE_DEFAULT);
                    channel.enableLights(true);
                    channel.enableVibration(true);
                    channel.setSound(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.alert_tone),attributes);
                    notificationManager.createNotificationChannel(channel);
                }
                notificationManager.notify(0, notificationBuilder.build());


    }
    //This method is only generating push notification
    //It is same as we did in earlier posts
    private void sendNotification(String messageBody) {
        if (!Utilities.isAppIsInBackground(getApplicationContext())) {
            // app is in foreground, broadcast the push message
            utils.print(TAG, "foreground");
            Intent intent = new Intent(this, Home.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("push", true);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                    PendingIntent.FLAG_ONE_SHOT);
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this,channelId)
                    .setContentTitle(getString(R.string.app_name))
                    .setContentText(messageBody)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(messageBody))
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setAutoCancel(true)
                    .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                    .setContentIntent(pendingIntent);
            notificationBuilder.setSmallIcon(getNotificationIcon(notificationBuilder), 1);
            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(channelId,
                        "App Notifications",
                        NotificationManager.IMPORTANCE_DEFAULT);
                notificationManager.createNotificationChannel(channel);
            }
            notificationManager.notify(0, notificationBuilder.build());
        } else {
            utils.print(TAG, "background");
            // app is in background, show the notification in notification tray
            if (messageBody.equalsIgnoreCase("New Incoming Service")) {
                Intent intent = new Intent(this, Home.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                        PendingIntent.FLAG_ONE_SHOT);
                NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this,channelId)
                        .setContentTitle(getString(R.string.app_name))
                        .setContentText(messageBody)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(messageBody))
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setAutoCancel(true)
                        .setSound(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.alert_tone))
                        .setContentIntent(pendingIntent);
                notificationBuilder.setSmallIcon(getNotificationIcon(notificationBuilder), 1);
                NotificationManager notificationManager =
                        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    NotificationChannel channel = new NotificationChannel(channelId,
                            "App Notifications",
                            NotificationManager.IMPORTANCE_DEFAULT);
                    notificationManager.createNotificationChannel(channel);
                }
                notificationManager.notify(0, notificationBuilder.build());
            } else {
                Intent intent = new Intent(this, Home.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("push", true);
                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                        PendingIntent.FLAG_ONE_SHOT);
                NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this,channelId)
                        .setContentTitle(getString(R.string.app_name))
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(messageBody))
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                        .setContentIntent(pendingIntent);
                notificationBuilder.setSmallIcon(getNotificationIcon(notificationBuilder), 1);
                NotificationManager notificationManager =
                        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    NotificationChannel channel = new NotificationChannel(channelId,
                            "App Notifications",
                            NotificationManager.IMPORTANCE_DEFAULT);
                    notificationManager.createNotificationChannel(channel);
                }
                notificationManager.notify(0, notificationBuilder.build());
            }
        }
    }

    private int getNotificationIcon(NotificationCompat.Builder notificationBuilder) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            notificationBuilder.setColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
            return R.drawable.push_icon;
        } else {
            return R.mipmap.ic_launcher;
        }
    }
}

