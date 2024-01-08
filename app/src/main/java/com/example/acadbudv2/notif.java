package com.example.acadbudv2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

public class notif extends AppCompatActivity {
    private TextView notifTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notif);


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


        // Find the TextView in your layout
        notifTextView = findViewById(R.id.notif_tv_1);


        // Get the notification message from the intent
        String notificationMessage = getIntent().getStringExtra("notification_message");


        // Set the message to the TextView
        if (notificationMessage != null) {
            notifTextView.setText(notificationMessage);
        } else {
            notifTextView.setText("You have been added to a meeting."); // Set a default message if needed
        }
    }
}
