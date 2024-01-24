package com.example.acadbudv2;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.Objects;

public class reminder_receiver extends BroadcastReceiver {

    private static final String CHANNEL_ID = "my_channel_id";
    private static final int PERMISSION_REQUEST_CODE = 123; // Replace with your desired request code

    @Override
    public void onReceive(Context context, Intent intent) {
        // Handle the notification when it is received
        String message = intent.getStringExtra("message");

        // Show the meeting reminder notification
        showNotification(context, message);

        // Schedule the rating reminder notification after 3 hours
        scheduleRatingReminder(context);
    }

    private void showNotification(Context context, String message) {
        createNotificationChannel(context);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.notification_button)
                .setContentTitle("Meeting Reminder")
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        // Check if the app has the necessary permissions for notifications
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (context.checkSelfPermission(Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED ||
                    context.checkSelfPermission(Manifest.permission.VIBRATE) != PackageManager.PERMISSION_GRANTED) {
                // Request the necessary permissions
                ActivityCompat.requestPermissions(Objects.requireNonNull((Activity) context),
                        new String[]{Manifest.permission.INTERNET, Manifest.permission.VIBRATE},
                        PERMISSION_REQUEST_CODE);
                return;
            }
        }

        // Check if notification channel is required (for Android 8.0 and above)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "My Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setDescription("My Channel Description");

            notificationManager.createNotificationChannel(channel);
        }

        // Show the meeting reminder notification
        notificationManager.notify(0, builder.build());
    }

    private void scheduleRatingReminder(Context context) {
        // Schedule the rating reminder notification after 3 hours
        long threeHoursInMillis = 3 * 60 * 60 * 1000; // 3 hours in milliseconds

        // You might want to customize the message for the rating reminder
        String ratingReminderMessage = "Don't forget to rate your tutor!";

        // Schedule the notification after 3 hours
        scheduleNotification(context, ratingReminderMessage, threeHoursInMillis);
    }

    private void scheduleNotification(Context context, String message, long delay) {
        Intent ratingReminderIntent = new Intent(context, reminder_receiver.class);
        ratingReminderIntent.putExtra("message", message);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                1, // Use a unique request code for each notification
                ratingReminderIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE // Add FLAG_IMMUTABLE here
        );

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        long futureInMillis = System.currentTimeMillis() + delay;
        if (alarmManager != null) {
            alarmManager.set(AlarmManager.RTC_WAKEUP, futureInMillis, pendingIntent);
        }
    }

    private void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "My Channel";
            String description = "My Channel Description";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }
}
