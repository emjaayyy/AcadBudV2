package com.example.acadbudv2;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class meeting_list_adapter extends RecyclerView.Adapter<meeting_list_adapter.MeetingViewHolder> {
    private Context context;
    private List<meetings> meetings;

    public meeting_list_adapter(Context context, List<meetings> meetings) {
        this.context = context;
        this.meetings = meetings;
    }

    @NonNull
    @Override
    public MeetingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.meeting_list_item, parent, false);
        return new MeetingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MeetingViewHolder holder, int position) {
        meetings meeting = meetings.get(position);
        holder.bind(meeting);
    }

    @Override
    public int getItemCount() {
        return meetings.size();
    }

    public class MeetingViewHolder extends RecyclerView.ViewHolder {
        TextView subjectTextView;
        TextView dateTextView;
        TextView timeTextView;
        TextView topicTextView;
        Button createButton;
        Button participantButton;

        public MeetingViewHolder(@NonNull View itemView) {
            super(itemView);
            participantButton = itemView.findViewById(R.id.participanttv_1_ssg);
            subjectTextView = itemView.findViewById(R.id.subject_list);
            dateTextView = itemView.findViewById(R.id.date_list);
            topicTextView = itemView.findViewById(R.id.topic_list);
            timeTextView = itemView.findViewById(R.id.time_list);
        }

        public void bind(meetings meeting) {
            subjectTextView.setText(meeting.getSubject());
            dateTextView.setText(meeting.getDate());
            timeTextView.setText(meeting.getTime());
            topicTextView.setText(meeting.getTopic());


        }
    }
}
