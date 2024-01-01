package com.example.acadbudv2;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;

import java.util.List;

public class meeting_adapter_user extends RecyclerView.Adapter<meeting_adapter_user.MeetingViewHolder> {
    private Context context;
    private List<meetings> meetingsList;
    private DatabaseReference mdatabaseReference;
    private String currentUser;

    public meeting_adapter_user(Context context, List<meetings> meetingsList, DatabaseReference mdatabaseReference) {
        this.context = context;
        this.meetingsList = meetingsList;
        this.mdatabaseReference = mdatabaseReference;

        // Retrieve the current user's name from SharedPreferences
        SharedPreferences preferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        currentUser = preferences.getString("current_user", "");
    }

    @NonNull
    @Override
    public MeetingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.meeting_adapter_user, parent, false);
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

    public class MeetingViewHolder extends RecyclerView.ViewHolder {
        TextView subjectTextView;
        TextView topicTextView;
        TextView dateTimeTextView;
        TextView participantNumTextView;
        Button leaveButton;

        public MeetingViewHolder(@NonNull View itemView) {
            super(itemView);
            subjectTextView = itemView.findViewById(R.id.subject_tv_user);
            topicTextView = itemView.findViewById(R.id.topic_tv_user);
            dateTimeTextView = itemView.findViewById(R.id.date_time_tv_1_user);
            participantNumTextView = itemView.findViewById(R.id.participant_num_1_user);
            leaveButton = itemView.findViewById(R.id.leave_btn_user);

            leaveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Implement leave meeting functionality here
                    // Make sure to handle participant removal and update UI accordingly
                    // You may use mdatabaseReference to update the Firebase database
                    // and notifyDataSetChanged() to update the RecyclerView
                    // Example: leaveMeeting(meetingsList.get(getAdapterPosition()));
                    Toast.makeText(context, "Leave button clicked", Toast.LENGTH_SHORT).show();
                }
            });
        }

        public void bind(meetings meeting) {
            subjectTextView.setText(meeting.getSubject());
            topicTextView.setText(meeting.getTopic());
            String dateTime = meeting.getDate() + " " + meeting.getTime();
            dateTimeTextView.setText(dateTime);

            // Display the participant count
            int participantCount = meeting.getParticipants().size();
            participantNumTextView.setText(String.valueOf(participantCount));
        }
    }

    // Add the method to handle leave meeting functionality if needed
    private void leaveMeeting(meetings meeting) {
        // Implement the logic to leave the meeting
        // Update the database and UI accordingly
    }
}
