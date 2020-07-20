package com.example.threadapplication;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.HashMap;
//this service receive's msg sent from remote db and displays it as a notification to user
//this works even if app is not in foreground and running

public class FirebaseTokenMessagingService extends FirebaseMessagingService {
    private static final String CHANNEL_ID ="12";
    DatabaseReference dr;
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        createNotificationChannel();
        showNotification(remoteMessage);

    }

    private void showNotification(RemoteMessage remoteMessage) {
        Intent intent=new Intent(this,MainActivity.class);
        PendingIntent pi=PendingIntent.getActivity(this,0,intent,0);

        //get msg title and body
        String textTitle=remoteMessage.getData().get("title");
        String textContent=remoteMessage.getData().get("text");

        //build notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle(textTitle)
                .setContentText(textContent)
                .setAutoCancel(true)
                .setContentIntent(pi)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        //display them
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(1, builder.build());
    }

    private void createNotificationChannel() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                CharSequence name = "event_notification";
                String description = "notifies about symposium events";
                int importance = NotificationManager.IMPORTANCE_DEFAULT;
                NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
                channel.setDescription(description);
                NotificationManager notificationManager = getSystemService(NotificationManager.class);
                notificationManager.createNotificationChannel(channel);
            }

    }

    @Override
    public void onNewToken(String token) {

        //if new token generated update them to db
        String uid=FirebaseAuth.getInstance().getUid();
        dr=FirebaseDatabase.getInstance().getReference("Symposium/Participants/"+uid+"/fcm");

        if(uid!=null)
            dr.setValue(token);
    }
}
