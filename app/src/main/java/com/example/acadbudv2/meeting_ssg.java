package com.example.acadbudv2;

import android.os.Bundle;
import android.widget.EditText;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.ArrayList;
import java.util.List;

public class meeting_ssg extends AppCompatActivity {
    private RecyclerView recyclerView;
    private meeting_adapter adapter;
    private DatabaseReference mdatabaseReference;
    private EditText subjectEditText;
    private EditText topicEditText;
    private EditText dateEditText;
    private EditText timeEditText;
    private String channelName = "Math";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.meeting_ssg);
        FirebaseApp.initializeApp(this);
        mdatabaseReference = FirebaseDatabase.getInstance().getReference("Meetings");
        recyclerView = findViewById(R.id.recyclerView);
        adapter = new meeting_adapter(new ArrayList<meetings>(), channelName, subjectEditText, topicEditText, dateEditText, timeEditText);
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
                        adapter = new meeting_adapter(meetingsList, channelName, subjectEditText, topicEditText, dateEditText, timeEditText);
                        recyclerView.setAdapter(adapter);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Handle error
                    }
                });
    }
}
