package com.example.demofirebasecloudmessaging;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

public class FirebaseMsgService extends FirebaseMessagingService {
    public static final String TAG = "FCMservice";

    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "Refreshed token: " + token);// unique key generated when

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // sending broadcast message
        Log.d(TAG, "From: " + remoteMessage.getFrom());
        Log.d(TAG, "messageType: " + remoteMessage.getMessageType());
        Log.d(TAG, "messageId: " + remoteMessage.getMessageId());
        Log.d(TAG, "To : " + remoteMessage.getTo());
        Log.d(TAG, "CollapseKey: " + remoteMessage.getCollapseKey());
        Log.d(TAG, "sentTime: " + remoteMessage.getSentTime());
        Log.d(TAG, "getOriginalPriority: " + remoteMessage.getOriginalPriority());
        Log.d(TAG, "getPriority: " + remoteMessage.getPriority());
        Log.d(TAG, "getTtl: " + remoteMessage.getTtl());
        Log.d(TAG, "getData: "+ remoteMessage.getData());

        int count=0;
         for (Map.Entry<String,String>  d: remoteMessage.getData().entrySet()){
             Log.d(TAG, count+ "  key : " + d.getKey() + " value : " + d.getValue());
         count++;
         }
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction("com.example.demofirebasecloudmessaging.NEW_Notification");
        sendBroadcast(broadcastIntent);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, broadcastIntent, 0);

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());

            if (/* Check if data needs to be processed by long running job */ true) {
                // For long-running tasks (10 seconds or more) use Firebase Job Dispatcher.
               // scheduleJob();
            } else {
                // Handle message within 10 seconds
               // handleNow();
            }

        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
             String title = remoteMessage.getNotification().getTitle();
             String body = remoteMessage.getNotification().getBody();
             String url ="";
             if (remoteMessage.getData()!= null){
                     url = remoteMessage.getData().get("image");}
        makeNotificationChannel(pendingIntent,title,body,url);
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    void makeNotificationChannel(PendingIntent pendingIntent, String title , String text,String url)
    {
        NotificationCompat.Builder builder = new NotificationCompat.Builder
                (this, "channel_1");
        builder.setContentTitle(title);
        builder.setContentText(text);
        Bitmap bitmap = getBitmapFromURL(url);
        builder.setLargeIcon(bitmap);
         builder.setSmallIcon(R.drawable.service_icon);
        builder.setTicker("Service is running.. :)");
        builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        builder.setContentIntent(pendingIntent);
        NotificationChannel channel = new NotificationChannel("channel_1", "channel_1", NotificationManager.IMPORTANCE_HIGH);
        channel.setDescription("this is a default channel");
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.createNotificationChannel(channel);
        manager.notify(1, builder.build());
    }


    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            // Log exception
            return null;
        }
    }


}
