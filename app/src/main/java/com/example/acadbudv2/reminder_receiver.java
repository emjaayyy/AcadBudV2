package com.example.acadbudv2;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

public class reminder_receiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String meetingTopic = intent.getStringExtra("meetingTopic");

        // Handle the reminder action here, for example, show a notification
        showNotification(context, "Meeting Reminder", meetingTopic);
    }

    private void showNotification(Context context, String title, String message) {
        // Create a notification channel for Android Oreo and higher
        String channelId = "meeting_channel";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "Meeting Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        Notification notification = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            notification = new Notification.Builder(context, channelId)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setSmallIcon(R.drawable.notification_button) // Customize with your own icon
                    .build();
        }

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notification);
    }
}
