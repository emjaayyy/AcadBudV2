package com.example.acadbudv2;

import android.content.Context;
import android.os.Bundle;
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

public class meeting_ssg extends AppCompatActivity {
    private RecyclerView recyclerView;
    private meeting_adapter adapter;
    private DatabaseReference mdatabaseReference;
    private EditText subjectEditText;
    private EditText topicEditText;
    private TextView date;
    private TextView time;
    private String channelName = "Math";
    private Context context; // Add a context variable

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.meeting_ssg);
        FirebaseApp.initializeApp(this);
        mdatabaseReference = FirebaseDatabase.getInstance().getReference("Meetings");
        recyclerView = findViewById(R.id.recyclerView);
        subjectEditText = findViewById(R.id.editTextSubject);
        topicEditText = findViewById(R.id.editTextTopic);
        date = findViewById(R.id.date);
        time = findViewById(R.id.time);

        context = this; // Store the context

        // Create the adapter with an empty list initially
        adapter = new meeting_adapter(this, new ArrayList<meetings>(), channelName, subjectEditText, topicEditText, date, time);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        fetchMeetingsData();
    }

    private void fetchMeetingsData() {
        mdatabaseReference.child("Channels").child(channelName).child("Session 1")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        List<meetings> meetingsList = new ArrayList<>();
                        for (DataSnapshot meetingSnapshot : dataSnapshot.getChildren()) {
                            meetings meeting = meetingSnapshot.getValue(meetings.class);
                            if (meeting != null) {
                                meetingsList.add(meeting);
                            }
                        }

                        // Update the existing adapter with the fetched data
                        adapter.updateMeetingsList(meetingsList);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Handle error
                        Toast.makeText(context, "Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
