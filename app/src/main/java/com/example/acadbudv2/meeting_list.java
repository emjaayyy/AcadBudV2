package com.example.acadbudv2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

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

public class meeting_list extends AppCompatActivity {
    private RecyclerView recyclerView;
    private meeting_list_adapter meetingListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.meeting_list_dialog); // Set your layout here

        recyclerView = findViewById(R.id.meetingListRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Button create = findViewById(R.id.create_meeting);

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newmeet = new Intent(meeting_list.this, meeting_dialog.class);
                startActivity(newmeet);
            }
        });

        // Retrieve meetings from Firebase and display them
        retrieveMeetingsFromFirebase();
    }

    private void retrieveMeetingsFromFirebase() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Meetings/Channels/Math/Session 1");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<meetings> meetings = new ArrayList<>();
                for (DataSnapshot meetingSnapshot : dataSnapshot.getChildren()) {
                    meetings meeting = meetingSnapshot.getValue(meetings.class);
                    meetings.add(meeting);
                }

                // Once you have the list of meetings, pass it to your adapter.
                meetingListAdapter = new meeting_list_adapter(meeting_list.this, meetings);
                recyclerView.setAdapter(meetingListAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle any errors here.
            }
        });
    }
}
