package com.example.acadbudv2;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
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

public class meeting_user extends AppCompatActivity {
    private DatabaseReference mdatabaseReference;
    private meeting_adapter_user adapter;
    private String currentUser;

    private Context context;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.meeting_user);
        FirebaseApp.initializeApp(this);

        context = this;

        SharedPreferences preferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        currentUser = preferences.getString("current_user", "");

        mdatabaseReference = FirebaseDatabase.getInstance().getReference("Meetings");

        // Set up RecyclerView with a layout manager
        RecyclerView recyclerView = findViewById(R.id.recyclerView_meeting_user);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        // Fetch meetings data and set up RecyclerView
        fetchMeetingsData();
    }

    private void fetchMeetingsData() {
        mdatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<meetings> meetingsList = new ArrayList<>();
                String currentUserName = getCurrentUserName();

                for (DataSnapshot sessionSnapshot : dataSnapshot.getChildren()) {
                    DataSnapshot participantsSnapshot = sessionSnapshot.child("Participants");

                    // Check if the current user is a participant in this session
                    if (participantsSnapshot.exists() && participantsSnapshot.hasChild(currentUserName)) {
                        DataSnapshot detailsSnapshot = sessionSnapshot.child("Details");
                        meetings meeting = detailsSnapshot.getValue(meetings.class);

                        if (meeting != null) {
                            meetingsList.add(meeting);
                        }
                    }
                }

                // Update the RecyclerView adapter with the new data
                updateRecyclerView(meetingsList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors if any
                Log.e("Firebase", "Failed to retrieve meetings", databaseError.toException());
                Toast.makeText(context, "Failed to retrieve meetings", Toast.LENGTH_SHORT).show();
            }


    private void updateRecyclerView(List<meetings> meetingsList) {
                // Corrected instantiation of meeting_adapter_user
                adapter = new meeting_adapter_user(context, meetingsList);

                RecyclerView recyclerView = findViewById(R.id.recyclerView_meeting_user);
        recyclerView.setAdapter(adapter);
            }
        });
    }

    private String getCurrentUserName() {
        SharedPreferences sharedPreferences = getSharedPreferences("userName", Context.MODE_PRIVATE);
        // Replace "user_name_key" with the actual key you use to store the user's name
        return sharedPreferences.getString("userName", "");
    }
}
