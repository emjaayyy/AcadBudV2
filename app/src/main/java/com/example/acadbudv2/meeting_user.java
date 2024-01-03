package com.example.acadbudv2;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
    private List<String> participantsList;
    private String currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.meeting_user);

        mdatabaseReference = FirebaseDatabase.getInstance().getReference("Meetings");

        // Retrieve the current user's name from SharedPreferences
        currentUser = getCurrentUserName();

        // Set up RecyclerView with a layout manager
        RecyclerView recyclerView = findViewById(R.id.recyclerView_meeting_user);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        // Fetch meetings data and set up RecyclerView
        fetchMeetingsData();
    }

    private String getCurrentUserName() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        return sharedPreferences.getString("current_user", "");
    }

    private void fetchMeetingsData() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefsFile", Context.MODE_PRIVATE);
        String currentUserName = sharedPreferences.getString("userName", "");

        mdatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<meetings> meetingsList = new ArrayList<>();

                for (DataSnapshot sessionSnapshot : dataSnapshot.getChildren()) {
                    DataSnapshot participantsSnapshot = sessionSnapshot.child("Participants");

                    // Check if the current user's name is among the participants in this session
                    if (participantsSnapshot.exists() && participantsSnapshot.hasChild(currentUserName)) {
                        DataSnapshot detailsSnapshot = sessionSnapshot.child("Details");
                        meetings meeting = detailsSnapshot.getValue(meetings.class);

                        // Retrieve the list of participants
                        List<String> participantsList = new ArrayList<>();
                        for (DataSnapshot participantSnapshot : participantsSnapshot.getChildren()) {
                            participantsList.add(participantSnapshot.getKey());
                        }

                        if (meeting != null) {
                            meeting.setParticipants(participantsList);
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
                Toast.makeText(meeting_user.this, "Failed to retrieve meetings", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void updateRecyclerView(List<meetings> meetingsList) {
        // Corrected instantiation of meeting_adapter_user
        adapter = new meeting_adapter_user(meeting_user.this, meetingsList, currentUser);

        RecyclerView recyclerView = findViewById(R.id.recyclerView_meeting_user);
        recyclerView.setAdapter(adapter);
    }

}