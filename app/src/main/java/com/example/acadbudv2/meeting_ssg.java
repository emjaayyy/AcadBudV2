package com.example.acadbudv2;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
// Import statements...

// Import statements...

public class meeting_ssg extends AppCompatActivity {
    private DatabaseReference mdatabaseReference;
    private String channelName = "Math";
    private Context context;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.meeting_ssg);
        FirebaseApp.initializeApp(this);
        mdatabaseReference = FirebaseDatabase.getInstance().getReference("Meetings");

        context = this;

        // Create meeting button click listener
        Button createMeetingBtn = findViewById(R.id.create_meeting_btn_user);
        createMeetingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Show a dialog to input meeting details
                showCreateMeetingDialog();
            }
        });

        // Fetch meetings data and set up RecyclerView
        fetchMeetingsData();
    }

    private void fetchMeetingsData() {
        // Fetch data from Firebase and update RecyclerView
        // (Your existing code for fetching and updating meetings data)
    }

    private void showCreateMeetingDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Create Meeting");

        // Inflate the layout for the dialog
        View dialogView = LayoutInflater.from(context).inflate(R.layout.meeting_dialog, null);
        builder.setView(dialogView);

        // Get references to EditText fields in the dialog
        EditText subjectEditTextDialog = dialogView.findViewById(R.id.editTextSubject);
        EditText topicEditTextDialog = dialogView.findViewById(R.id.editTextTopic);
        EditText dateEditTextDialog = dialogView.findViewById(R.id.date);
        EditText timeEditTextDialog = dialogView.findViewById(R.id.time);

        // Set positive button click listener
        builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Retrieve values from the dialog
                String subject = subjectEditTextDialog.getText().toString();
                String topic = topicEditTextDialog.getText().toString();
                String date = dateEditTextDialog.getText().toString();
                String time = timeEditTextDialog.getText().toString();

                // Create a new meeting object
                meetings newMeeting = new meetings(subject, topic, date, time, new ArrayList<>());

                // Save the new meeting to Firebase
                mdatabaseReference.child("Channels").child(channelName).child("Session 1").push().setValue(newMeeting);

                // Inform the user that the meeting has been created
                Toast.makeText(context, "Meeting created successfully", Toast.LENGTH_SHORT).show();
            }
        });

        // Set negative button click listener
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Cancel the dialog
            }
        });

        // Display the dialog
        builder.show();
    }
}

