package com.example.chatapp.Notifications;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.core.app.NotificationCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessaging extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        String sented = remoteMessage.getData().get("sented");
        String user = remoteMessage.getData().get("user");

        SharedPreferences preferences = getSharedPreferences("PREFS", MODE_PRIVATE);
        String currentUser = preferences.getString("currentuser", "none");

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        //  if (firebaseUser != null && sented.equals(firebaseUser.getUid())){
        if (!currentUser.equals(user)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                sendOreoNotification(remoteMessage);
            } else {
                sendNotification(remoteMessage);

            }
        }
    }

    private void sendOreoNotification(RemoteMessage remoteMessage) {
        String user = remoteMessage.getData().get("user");
        String icon = remoteMessage.getData().get("icon");
        String title = remoteMessage.getData().get("title");
        String body = remoteMessage.getData().get("body");

        RemoteMessage.Notification notification = remoteMessage.getNotification();
        int j = Integer.parseInt(user.replaceAll("[\\D]", ""));
        Intent intent = new Intent(this, com.example.chatapp.MainActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("userid", user);
        intent.putExtras(bundle);
        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        com.example.chatapp.Notifications.OreoNotification oreoNotification = new com.example.chatapp.Notifications.OreoNotification(this);
        Notification.Builder builder = oreoNotification.getOreoNotification(title, body, pendingIntent,
                defaultSound, icon);

        int i = 0;
        if (j > 0) {
            i = j;
        }

        oreoNotification.getManager().notify(1, builder.build());

    }

    private void sendNotification(RemoteMessage remoteMessage) {

        String user = remoteMessage.getData().get("user");
        String icon = remoteMessage.getData().get("icon");
        String title = remoteMessage.getData().get("title");
        String body = remoteMessage.getData().get("body");

        RemoteMessage.Notification notification = remoteMessage.getNotification();
        int j = Integer.parseInt(user.replaceAll("[\\D]", ""));
        Intent intent = new Intent(this, com.example.chatapp.MainActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("userid", user);
        intent.putExtras(bundle);
        //  intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);f
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(MyFirebaseMessaging.this)
                .setSmallIcon(Integer.parseInt(icon))
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setSound(defaultSound)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setContentIntent(pendingIntent);
        NotificationManager noti = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        int i = 0;
        if (j > 0) {
            i = j;
        }

        noti.notify(1, builder.build());
    }
}
