package com.example.acadbudv2;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class notif extends AppCompatActivity {

    // Notification channel ID and name
    private static final String CHANNEL_ID = "my_channel_id";
    private static final String CHANNEL_NAME = "My Channel";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notif);

        RecyclerView recyclerView = findViewById(R.id.notif);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        // Fetch meeting details for the current user
        fetchMeetingDetailsForCurrentUser();
        createNotificationChannel();
        // Retrieve meetingsList from intent
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("meetingsList")) {
            ArrayList<meetings> meetingsList = intent.getParcelableArrayListExtra("meetingsList");

            // Populate the notifications list based on meetingsList
            List<notif_item> notifications = createNotifications(meetingsList);

            // Update RecyclerView adapter with the new notifications
            notif_adapter adapter = new notif_adapter(this, notifications);
            recyclerView.setAdapter(adapter);
        }

        // Other methods and functionalities for notifications

        Button home = findViewById(R.id.home_btn_notif);
        Button meeting = findViewById(R.id.meeting_btn_notif);
        Button profile = findViewById(R.id.profile_btn_notif);

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent feed = new Intent(notif.this, home_user.class);
                startActivity(feed);
            }
        });

        meeting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mu = new Intent(notif.this, meeting_user.class);
                startActivity(mu);
            }
        });

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pu = new Intent(notif.this, profile_user.class);
                startActivity(pu);
            }
        });
    }

    private void fetchMeetingDetailsForCurrentUser() {
        String currentUserName = getCurrentUserName();

        FirebaseDatabaseHelper.getMeetingDetailsForCurrentUser(currentUserName, new FirebaseDatabaseHelper.MeetingDetailsCallback() {
            @Override
            public void onMeetingDetailsFetched(List<meetings> meetingsList) {
                // Do something with the fetched meeting details
                // Update your RecyclerView or UI as needed
                updateRecyclerView(meetingsList);
            }

            @Override
            public void onMeetingDetailsFetchError(String errorMessage) {
                // Handle errors if any
                Log.e("Firebase", "Failed to fetch meeting details: " + errorMessage);
            }
        });
    }

    private void updateRecyclerView(List<meetings> meetingsList) {
        // Implement the logic to update your RecyclerView
        // For example, you can create an adapter and set it to your RecyclerView
        RecyclerView recyclerView = findViewById(R.id.notif);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        notif_adapter adapter = new notif_adapter(notif.this, createNotifications(meetingsList));
        recyclerView.setAdapter(adapter);
    }

    private String getCurrentUserName() {
        // Implement your logic to get the current user's name
        // This might involve SharedPreferences or some other method
        // For example, you can reuse the method you used in your meeting_user class
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        return sharedPreferences.getString("current_user", "");
    }

    private List<notif_item> createNotifications(List<meetings> meetingsList) {
        List<notif_item> notifications = new ArrayList<>();

        for (meetings meeting : meetingsList) {
            // Notification for meeting addition
            String meetingMessage = "You have been added to a meeting.\n" + meeting.getTopic() + " | " + meeting.getSubject();
            notifications.add(new notif_item(meetingMessage, ""));

            // Compare the current time with the meeting's date and time
            String timeString = meeting.getTime();

            // Parse the time string into milliseconds
            long meetingDateTimeInMillis = parseTimeStringToMillis(timeString);

            long currentTime = System.currentTimeMillis();
            long threeHoursInMillis = 3 * 60 * 60 * 1000; // 3 hours in milliseconds

            if (meetingDateTimeInMillis > currentTime) {
                long delay = meetingDateTimeInMillis + threeHoursInMillis - currentTime;
                scheduleRatingNotification(meetingMessage, delay);
            }
        }
        return notifications;
    }

    private long parseTimeStringToMillis(String timeString) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
            Date date = sdf.parse(timeString);
            if (date != null) {
                return date.getTime();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }



    private void scheduleRatingNotification(String message, long delay) {
        Intent notificationIntent = new Intent(this, reminder_receiver.class);
        notificationIntent.putExtra("message", message);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                0,
                notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE // Add FLAG_IMMUTABLE here
        );

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        long futureInMillis = System.currentTimeMillis() + delay;
        if (alarmManager != null) {
            alarmManager.set(AlarmManager.RTC_WAKEUP, futureInMillis, pendingIntent);
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setDescription("My Channel Description");

            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }
}
