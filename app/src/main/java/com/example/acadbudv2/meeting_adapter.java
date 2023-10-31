package com.example.acadbudv2;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class meeting_adapter extends RecyclerView.Adapter<meeting_adapter.MeetingViewHolder> {
    private List<meetings> meetingsList;
    private DatabaseReference databaseReference;
    private String channelName;
    private EditText subjectEditText;
    private EditText topicEditText;
    private EditText dateEditText;
    private EditText timeEditText;

    public meeting_adapter(List<meetings> meetingsList, String channelName, EditText subjectEditText, EditText topicEditText, EditText dateEditText, EditText timeEditText) {
        // Point to the "Meetings" node in Firebase
        databaseReference = FirebaseDatabase.getInstance().getReference("Meetings");
        this.meetingsList = meetingsList;
        this.channelName = channelName;
        this.subjectEditText = subjectEditText;
        this.topicEditText = topicEditText;
        this.dateEditText = dateEditText;
        this.timeEditText = timeEditText;
    }

    @NonNull
    @Override
    public MeetingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.meeting_dialog, parent, false);
        return new MeetingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MeetingViewHolder holder, int position) {
        meetings meeting = meetingsList.get(position);
        holder.bind(meeting);
    }

    @Override
    public int getItemCount() {
        return meetingsList.size();
    }

    public void createAndSaveMeeting() {
        String subject = subjectEditText.getText().toString();
        String topic = topicEditText.getText().toString();
        String date = dateEditText.getText().toString();
        String time = timeEditText.getText().toString();

        if (TextUtils.isEmpty(subject) || TextUtils.isEmpty(topic) || TextUtils.isEmpty(date) || TextUtils.isEmpty(time)) {
            Toast.makeText(subjectEditText.getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a new meetings object
        List<String> participantsList = new ArrayList<>();
        // Add participants to participantsList based on your UI

        meetings newMeeting = new meetings(subject, topic, date, time, participantsList);

        // Save the meeting to Firebase under the specified channel
        saveMeetingToFirebase(channelName, newMeeting);
    }

    private void saveMeetingToFirebase(String channelName, meetings meeting) {
        DatabaseReference channelRef = databaseReference.child("Channels").child(channelName).child("Session 1");
        DatabaseReference newMeetingRef = channelRef.push();
        newMeetingRef.child("subject").setValue(meeting.getSubject());
        newMeetingRef.child("topic").setValue(meeting.getTopic());
        newMeetingRef.child("date").setValue(meeting.getDate());
        newMeetingRef.child("time").setValue(meeting.getTime());

        // You can also add participants to the meeting by updating the Firebase database

        // Display a Toast message to indicate the success
        Toast.makeText(subjectEditText.getContext(), "Meeting created successfully", Toast.LENGTH_SHORT).show();
    }



    public class MeetingViewHolder extends RecyclerView.ViewHolder {
        EditText subjectEditText;
        EditText topicEditText;
        EditText dateEditText;
        EditText timeEditText;
        Button buttonCreateMeeting;

        public MeetingViewHolder(@NonNull View itemView) {
            super(itemView);
            subjectEditText = itemView.findViewById(R.id.editTextSubject);
            topicEditText = itemView.findViewById(R.id.editTextTopic);
            dateEditText = itemView.findViewById(R.id.editTextDate);
            timeEditText = itemView.findViewById(R.id.editTextTime);
            buttonCreateMeeting = itemView.findViewById(R.id.buttonCreateMeeting);

            buttonCreateMeeting.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    createAndSaveMeeting(); // Replace with the selected channel name
                }
            });
        }

        public void bind(meetings meeting) {
            // Bind meeting data to the UI, if needed
        }
    }
}
