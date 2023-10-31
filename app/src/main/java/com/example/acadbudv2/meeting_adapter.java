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

import java.util.List;

public class meeting_adapter extends RecyclerView.Adapter<meeting_adapter.MeetingViewHolder> {
    private List<meetings> meetingsList;
    private DatabaseReference databaseReference;

    public meeting_adapter(List<meetings> meetingsList) {
        this.meetingsList = meetingsList;
        databaseReference = FirebaseDatabase.getInstance().getReference("Meetings/Channels/"); // Reference to the "Meetings/Channels" node in Firebase Realtime Database
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

    public void addMeeting(meetings meeting) {
        // Push a new meeting to Firebase
        String key = databaseReference.push().getKey();
        if (key != null) {
            databaseReference.child(key).setValue(meeting);
            meetingsList.add(meeting);
            notifyItemInserted(meetingsList.size() - 1);
        }
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
                    // Handle creating and saving the meeting when the button is clicked
                    createAndSaveMeeting();
                }
            });
        }

        public void bind(meetings meeting) {
            subjectEditText.setText(meeting.getSubject());
            dateEditText.setText(meeting.getDate());
            timeEditText.setText(meeting.getTime());
            topicEditText.setText(meeting.getTopic());
        }

        private void createAndSaveMeeting() {
            // Extract meeting details from the EditText fields
            String subject = subjectEditText.getText().toString();
            String topic = topicEditText.getText().toString();
            String date = dateEditText.getText().toString();
            String time = timeEditText.getText().toString();

            // Check if any of the fields are empty, and do not save if they are
            if (TextUtils.isEmpty(subject) || TextUtils.isEmpty(topic) || TextUtils.isEmpty(date) || TextUtils.isEmpty(time)) {
                Toast.makeText(itemView.getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Create a new meetings object
            meetings newMeeting = new meetings();
            newMeeting.setSubject(subject);
            newMeeting.setTopic(topic);
            newMeeting.setDate(date);
            newMeeting.setTime(time);

            // Save the meeting to Firebase
            addMeeting(newMeeting);

            // You can also add code to dismiss the dialog or perform any other action here
        }
    }
}
