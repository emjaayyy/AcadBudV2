package com.example.acadbudv2;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FirebaseDatabaseHelper {
    private static final DatabaseReference mDatabaseReference = FirebaseDatabase.getInstance().getReference();

    public interface MeetingDetailsCallback {
        void onMeetingDetailsFetched(List<meetings> meetingsList);
        void onMeetingDetailsFetchError(String errorMessage);
    }

    public static void getMeetingDetailsForCurrentUser(String currentUserName, MeetingDetailsCallback callback) {
        mDatabaseReference.child("Meetings").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<meetings> meetingsList = new ArrayList<>();

                for (DataSnapshot sessionSnapshot : dataSnapshot.getChildren()) {
                    DataSnapshot participantsSnapshot = sessionSnapshot.child("Participants");

                    if (participantsSnapshot.exists() && participantsSnapshot.hasChild(currentUserName)) {
                        DataSnapshot detailsSnapshot = sessionSnapshot.child("Details");
                        meetings meeting = detailsSnapshot.getValue(meetings.class);

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

                callback.onMeetingDetailsFetched(meetingsList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onMeetingDetailsFetchError(databaseError.getMessage());
            }
        });
    }
}
