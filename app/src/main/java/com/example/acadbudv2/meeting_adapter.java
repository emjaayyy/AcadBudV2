package com.example.acadbudv2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class meeting_adapter extends RecyclerView.Adapter<meeting_adapter.MeetingViewHolder> {
    private List<meetings> meetingsList;
    private int currentParticipants;

    public meeting_adapter(List<meetings> meetingsList) {
        this.meetingsList = meetingsList;
        currentParticipants = 0; // Initialize currentParticipants
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

    // Add methods related to meetings here
    // For example, adding meetings, updating meetings, etc.

    public class MeetingViewHolder extends RecyclerView.ViewHolder {
        TextView subjectTextView;
        TextView topicTextView;
        TextView dateTextView;
        TextView timeTextView;
        Button buttonCreateMeeting;

        public MeetingViewHolder(@NonNull View itemView) {
            super(itemView);
            subjectTextView = itemView.findViewById(R.id.editTextSubject);
            topicTextView = itemView.findViewById(R.id.editTextTopic);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            timeTextView = itemView.findViewById(R.id.editTextTime);
            buttonCreateMeeting = itemView.findViewById(R.id.buttonCreateMeeting);
            buttonCreateMeeting.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Handle joining a meeting
                }
            });
        }

        public void bind(meetings meeting) {
            subjectTextView.setText(meeting.getSubject());
            dateTextView.setText(meeting.getDate());
            timeTextView.setText(meeting.getTime());
            // You can set other meeting-related information here
        }
    }


}
