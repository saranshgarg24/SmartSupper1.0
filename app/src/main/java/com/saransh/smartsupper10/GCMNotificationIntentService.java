package com.saransh.smartsupper10;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;


public class GCMNotificationIntentService extends IntentService {

    public static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;

    public GCMNotificationIntentService() {
        super("GcmIntentService");
    }

    public static final String TAG = "GCMNotificationIntentService";

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);

        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty()) {
            if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR
                    .equals(messageType)) {
                sendNotification("Send error: " , extras.toString(),"");
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED
                    .equals(messageType)) {
                sendNotification("Deleted messages on server: "
                        , extras.toString(),"");
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE
                    .equals(messageType)) {

                for (int i = 0; i < 3; i++) {
                    Log.i(TAG,
                            "Working... " + (i + 1) + "/5 @ "
                                    + SystemClock.elapsedRealtime());
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                    }

                }
                Log.i(TAG, "Completed work @ " + SystemClock.elapsedRealtime());

                String name = extras.getString("food_name");
                String desc = extras.getString("desc");
                String rate = extras.getString("rate");

                sendNotification(name, desc, rate);
                Log.i(TAG, "Received: " + extras.toString());
            }
        }
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

       private void sendNotification(String name, String desc, String rate) {
        Log.d(TAG, "Preparing to send notification...: " + name);
        mNotificationManager = (NotificationManager) this
                .getSystemService(Context.NOTIFICATION_SERVICE);

        //To be changed, pending

        Intent details = new Intent(getApplicationContext(), MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                details, 0);

        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        long[] vibrate = { 0, 100, 200, 300 };

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                this).setSmallIcon(R.drawable.ic_logo)
                .setContentTitle(name)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(desc))
                .setContentText(desc)
                .setVibrate(vibrate)
                .setAutoCancel(true)
                .setSound(alarmSound);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
        Log.d(TAG, "Notification sent successfully.");
    }
}