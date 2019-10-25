package com.example.jlccustomer.Activities;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.jlccustomer.R;
import com.example.jlccustomer.Utils.Constants;
import com.example.jlccustomer.Utils.Event;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;


public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "FirebaseMsgService";
    int notifyID = 1;
    String CHANNEL_ID = "my_channel_01";// The id of the channel.
    CharSequence name = "plago_notification";// The user-visible name of the channel.
    int importance = NotificationManager.IMPORTANCE_HIGH;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d("NotiifcationCheckFirst", remoteMessage.getData() + "");
        Map<String, String> notiifcationData = remoteMessage.getData();
        if (notiifcationData.containsKey("moredata")) {
            String completeData = notiifcationData.get("moredata");
            String message = null;
            String key = "";
            String driverName = "";
            String driverID = "";
            String ride_id = "";
            String vehi_number = "";
            String driver_image = "";
            try {
                JSONObject jsonComplete = new JSONObject(completeData);
                key = jsonComplete.getString("key");
                if (key != null && key.equalsIgnoreCase("driver_accept")) {
                    message = "Your Request is Accepted";
                    MainActivity.isDriverAccept = true;
                    driverName = jsonComplete.getString("driver_name");
                    driverID = jsonComplete.getString("driver_id");
                    ride_id = jsonComplete.getString("ride_id");
                    vehi_number = jsonComplete.getString("vehi_number");
                    driver_image = jsonComplete.getString("driver_image");

                    if (MainActivity.isFindTripOpen) {
                        EventBus.getDefault().post(new Event(Constants.DRIVER_ACCEPT, message, driverID, driver_image, ride_id, vehi_number, driverName));
                    }
                } else if (key.equalsIgnoreCase("driver_arrived")) {
                    message = "Driver is Arrived to Your location";
                    EventBus.getDefault().post(new Event(Constants.DRIVER_ARRIVED, message));


                } else if (key.equalsIgnoreCase("trip_start")) {
                    message = "Your trip Start";
                    EventBus.getDefault().post(new Event(Constants.TRIP_START, message));


                } else if (key.equalsIgnoreCase("trip_stopped")) {
                    EventBus.getDefault().post(new Event(Constants.TRIP_COMPLETE, message));
                } else if (key.equalsIgnoreCase("trip_completed")) {
                    message = "Your ride Completed";
                    EventBus.getDefault().post(new Event(Constants.PAYMENT_COLLECT, message));
                } else if (key.equalsIgnoreCase("driver_cancel")) {
                    message = "Driver is Cancel your ride";
                    EventBus.getDefault().post(new Event(Constants.DRIVER_CANCEL_RIDE, ride_id, driverName));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (isAppOnForeground(MyFirebaseMessagingService.this)) {
                if (Build.VERSION.SDK_INT <= 25) {
                    Intent intent = new Intent(this, MainActivity.class);
                    PendingIntent pendingIntent = PendingIntent.getActivity(MyFirebaseMessagingService.this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
                    NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
                    builder.setSmallIcon(R.mipmap.plago_mark).setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.plago_mark))
                            .setContentTitle(message)
                            .setContentIntent(pendingIntent)
                            .setDefaults(Notification.DEFAULT_ALL)
                            .setPriority(NotificationManager.IMPORTANCE_HIGH);
                    builder.setAutoCancel(true);
                    NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    mNotificationManager.notify(001, builder.build());
                } else {
                    sendNotification(message);
                }


            } else {
                if (Build.VERSION.SDK_INT <= 25) {
                    Intent intent = new Intent(this, MainActivity.class);
                    PendingIntent pendingIntent = PendingIntent.getActivity(MyFirebaseMessagingService.this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
                    NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
                    builder.setSmallIcon(R.mipmap.plago_mark)
                            .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.plago_mark))
                            .setContentTitle(message)
                            .setContentIntent(pendingIntent)
                            .setDefaults(Notification.DEFAULT_ALL)
                            .setPriority(NotificationManager.IMPORTANCE_HIGH);
                    builder.setAutoCancel(true);

                    NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    mNotificationManager.notify(001, builder.build());
                } else {
                    sendNotification(message);
                }
            }
        } else {
            Log.d("NotiifcationCheckFirst", "wrong format");

        }
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void sendNotification(String messageBody) {
        NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(MyFirebaseMessagingService.this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)

                .setContentTitle("New Message")
                .setContentText(messageBody)
                .setSmallIcon(R.mipmap.plago_mark)
                .setChannelId(CHANNEL_ID)
                .setSound(soundUri)
                .setContentIntent(pendingIntent);
        notificationBuilder.setAutoCancel(true);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.createNotificationChannel(mChannel);
        mNotificationManager.notify(notifyID, notificationBuilder.build());

    }

    private boolean isAppOnForeground(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        if (appProcesses == null) {
            return false;
        }
        final String packageName = context.getPackageName();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND && appProcess.processName.equals(packageName)) {
                return true;
            }
        }
        return false;
    }
}