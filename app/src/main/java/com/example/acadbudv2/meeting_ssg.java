package com.example.acadbudv2;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class meeting_ssg extends AppCompatActivity {
    private RecyclerView recyclerView;
    private meeting_adapter adapter;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.meeting_ssg);

        // Initialize Firebase
        FirebaseApp.initializeApp(this);
        databaseReference = FirebaseDatabase.getInstance().getReference("Meetings/Channels/");

        // Initialize the RecyclerView
        recyclerView = findViewById(R.id.recyclerView);

        // Create an instance of the meeting_adapter
        adapter = new meeting_adapter(new ArrayList<meetings>());

        // Set the RecyclerView's layout manager and adapter
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // Fetch and populate your meetings data
        // You can add meetings to the adapter using the addMeeting method:

        // Create a new meeting
        meetings newMeeting = new meetings();
        newMeeting.setSubject("Meeting Subject");
        newMeeting.setTopic("Meeting Topic");
        newMeeting.setDate("Meeting Date");
        newMeeting.setTime("Meeting Time");

        // Add the meeting to the adapter and save it to Firebase
        adapter.addMeeting(newMeeting);

        // Fetch meetings data (if needed)
        // List<meetings> meetingsData = yourDataFetchingMethod();
        // adapter.addMeetings(meetingsData);
    }

    // Add a method to fetch your meetings data from Firebase or any other source
    private List<meetings> yourDataFetchingMethod() {
        // Fetch your meetings data here and return a list of meetings.
        // You may need to implement the logic to fetch meetings from your data source (e.g., Firebase).
        List<meetings> meetingsList = new ArrayList<>();
        // Add your meetings to the list
        return meetingsList;
    }
}
