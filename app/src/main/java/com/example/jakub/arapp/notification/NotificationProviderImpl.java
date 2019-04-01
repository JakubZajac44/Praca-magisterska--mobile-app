package com.example.jakub.arapp.notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.example.jakub.arapp.MainActivity;
import com.example.jakub.arapp.R;
import com.example.jakub.arapp.utility.Utils;

import javax.inject.Inject;

public class NotificationProviderImpl implements NotificationProvider {
    public static final int NOTIFICATION_ID = 12345;
    private final String CHANNEL_ID = "SERVICE_CHANNEL";
    private final String CHANNEL_NAME = "SERVICE_NOTIFICATION";

    @Inject
    NotificationManager notificationManager;

    @Inject
    Context context;


    @Inject
    public NotificationProviderImpl() {
    }

    @Override
    public Notification getNotificationProvider() {
        String notificationText = context.getResources().getString(R.string.notification_text);

        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);

        notificationManager.createNotificationChannel(channel);

        Notification.Builder builder = new Notification.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_ble_notification)
                .setContentText(Utils.getCharSequence(notificationText))
                .setContentTitle(context.getResources().getString(R.string.notification_title))
                .setAutoCancel(true)
                .setContentIntent(createPendingIntent());

        Notification notification = builder.build();
        return notification;
    }

    private PendingIntent createPendingIntent() {
        Intent notificationIntent = new Intent(context, MainActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent intent = PendingIntent.getActivity(context, 0,
                notificationIntent, 0);

        return intent;
    }
}
