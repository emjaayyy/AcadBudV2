package com.example.acadbudv2;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class meeting_adapter_user extends RecyclerView.Adapter<meeting_adapter_user.MeetingViewHolder> {

    private Context context;
    private List<meetings> meetingsList;
    private String currentUser;

    public meeting_adapter_user(Context context, List<meetings> meetingsList, String currentUser) {
        this.context = context;
        this.meetingsList = meetingsList;
        this.currentUser = currentUser;
    }

    @NonNull
    @Override
    public MeetingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.meeting_adapter_user, parent, false);
        return new MeetingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MeetingViewHolder holder, int position) {
        if (meetingsList != null && position < meetingsList.size()) {
            meetings meeting = meetingsList.get(position);
            holder.bind(meeting);
        } else {
            Toast.makeText(context, "Error: Unable to retrieve meeting details.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public int getItemCount() {
        return meetingsList != null ? meetingsList.size() : 0;
    }

    public class MeetingViewHolder extends RecyclerView.ViewHolder {
        private TextView subjectTextView;
        private TextView topicTextView;
        private TextView dateTimeTextView;
        private TextView participantNumTextView;
        private Button leaveButton;

        public MeetingViewHolder(@NonNull View itemView) {
            super(itemView);
            subjectTextView = itemView.findViewById(R.id.subject_tv_user);
            topicTextView = itemView.findViewById(R.id.topic_tv_user);
            dateTimeTextView = itemView.findViewById(R.id.date_time_tv_1_user);
            participantNumTextView = itemView.findViewById(R.id.participant_num_1_user);
            leaveButton = itemView.findViewById(R.id.leave_btn_user);
        }

        public void bind(meetings meeting) {
            subjectTextView.setText(meeting.getSubject());
            topicTextView.setText(meeting.getTopic());
            dateTimeTextView.setText(context.getString(R.string.date_time_format, meeting.getDate(), meeting.getTime()));

            List<String> participants = meeting.getParticipants();
            int numParticipants = participants != null ? participants.size() : 0;
            participantNumTextView.setText(String.valueOf(numParticipants));

            // Example: Set a click listener for the leave button
            leaveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (context != null) {
                        Intent left = new Intent(context, ratings.class);
                        context.startActivity(left);
                        Toast.makeText(context, "You have left the meeting.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "Error: Context is null.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}
