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
                List<meetings> meetingList = new ArrayList<>();

                for (DataSnapshot sessionSnapshot : dataSnapshot.getChildren()) {
                    // Assuming that each child is a session (Session 1, Session 2, etc.)
                    DataSnapshot detailsSnapshot = sessionSnapshot.child("Details");
                    meetings meeting = detailsSnapshot.getValue(meetings.class);

                    if (meeting != null) {
                        // Assuming that Participants is directly under the session
                        DataSnapshot participantsSnapshot = sessionSnapshot.child("Participants");
                        // Fetch participants data (if needed)

                        // Add the meeting to the list
                        meetingList.add(meeting);
                    }
                }

                // Update the RecyclerView adapter with the new data
                updateRecyclerView(meetingList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Firebase", "Failed to retrieve meetings", databaseError.toException());
                Toast.makeText(context, "Failed to retrieve meetings", Toast.LENGTH_SHORT).show();
            }


            private void updateRecyclerView(List<meetings> meetingsList) {
        adapter = new meeting_adapter_user(context, meetingsList, mdatabaseReference);
        RecyclerView recyclerView = findViewById(R.id.recyclerView_meeting_user);
        recyclerView.setAdapter(adapter);
            }
        });
    }
}
