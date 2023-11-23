package com.example.acadbudv2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class meeting_adapter_ssg extends RecyclerView.Adapter<meeting_adapter_ssg.MeetingViewHolder> {
    private List<meetings> meetingsList;
    private Context context;
    private OnAddBtnClickListener addBtnClickListener;

    public interface OnAddBtnClickListener {
        void onAddBtnClick(int position);
    }

    public meeting_adapter_ssg(Context context, List<meetings> meetingsList) {
        this.context = context;
        this.meetingsList = meetingsList;
    }

    @NonNull
    @Override
    public MeetingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.meeting_adapter_ssg, parent, false);
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
        Button participantsButton;
        Button addButton;

        public MeetingViewHolder(@NonNull View itemView) {
            super(itemView);
            subjectTextView = itemView.findViewById(R.id.subject_tv_ssg);
            topicTextView = itemView.findViewById(R.id.topic_tv_ssg);
            dateTimeTextView = itemView.findViewById(R.id.date_time_tv_1_ssg);
            participantsButton = itemView.findViewById(R.id.participant_ssg);
            addButton = itemView.findViewById(R.id.add_btn_ssg);

            addButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (addBtnClickListener != null) {
                        addBtnClickListener.onAddBtnClick(getAdapterPosition());
                    }
                }
            });
        }

        public void bind(meetings meeting) {
            subjectTextView.setText(meeting.getSubject());
            topicTextView.setText(meeting.getTopic());
            String dateTime = meeting.getDate() + " " + meeting.getTime();
            dateTimeTextView.setText(dateTime);
        }
    }

    public void setOnAddBtnClickListener(OnAddBtnClickListener listener) {
        this.addBtnClickListener = listener;
    }

    public void updateMeetingsList(List<meetings> updatedMeetingsList) {
        meetingsList.clear();
        meetingsList.addAll(updatedMeetingsList);
        notifyDataSetChanged();
    }
}
