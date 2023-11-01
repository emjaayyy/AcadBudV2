package com.example.acadbudv2;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

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
    private TextView dateEditText;
    private TextView timeEditText;
    private Context context;

    public meeting_adapter(Context context, List<meetings> meetingsList, String channelName, EditText subjectEditText, EditText topicEditText, TextView dateEditText, TextView timeEditText) {
        this.context = context;
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

    public class MeetingViewHolder extends RecyclerView.ViewHolder {
        EditText subjectEditText;
        EditText topicEditText;
        TextView dateEditText;
        TextView timeEditText;
        Button buttonCreateMeeting;

        public MeetingViewHolder(@NonNull View itemView) {
            super(itemView);
            subjectEditText = itemView.findViewById(R.id.editTextSubject);
            topicEditText = itemView.findViewById(R.id.editTextTopic);
            dateEditText = itemView.findViewById(R.id.date);
            timeEditText = itemView.findViewById(R.id.time);
            buttonCreateMeeting = itemView.findViewById(R.id.buttonCreateMeeting);

            itemView.findViewById(R.id.calendar_btn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDatePickerDialog();
                }
            });

            itemView.findViewById(R.id.clock_btn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showTimePickerDialog();
                }
            });

            buttonCreateMeeting.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    createMeeting();
                }
            });
        }

        public void bind(meetings meeting) {
            // Bind meeting data to the UI, if needed
        }

        private void showDatePickerDialog() {
            DatePickerDialog datePickerDialog = new DatePickerDialog(context,
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                            String selectedDate = String.format("%02d-%02d-%04d", dayOfMonth, month + 1, year);
                            dateEditText.setText(selectedDate);
                        }
                    },
                    2023, 0, 1);

            datePickerDialog.show();
        }

        private void showTimePickerDialog() {
            TimePickerDialog timePickerDialog = new TimePickerDialog(context,
                    new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                            String selectedTime = String.format("%02d:%02d", hourOfDay, minute);
                            timeEditText.setText(selectedTime);
                        }
                    },
                    12, 0, true);

            timePickerDialog.show();
        }

        private void createMeeting() {
            String subject = subjectEditText.getText().toString();
            String topic = topicEditText.getText().toString();
            String date = dateEditText.getText().toString();
            String time = timeEditText.getText().toString();

            if (TextUtils.isEmpty(subject) || TextUtils.isEmpty(topic) || TextUtils.isEmpty(date) || TextUtils.isEmpty(time)) {
                // Handle empty fields or show an error message.
                return;
            }

            // Create a new meetings object
            List<String> participants = new ArrayList<>();
            // Add participants' names to the participants list

            meetings newMeeting = new meetings(subject, topic, date, time, participants);

            // Push the new meeting to Firebase
            String key = databaseReference.child("Channels")
                    .child(channelName)
                    .child("Session 1")
                    .push()
                    .getKey();
            databaseReference.child("Channels")
                    .child(channelName)
                    .child("Session 1")
                    .child(key)
                    .setValue(newMeeting);

            // Clear the input fields
            subjectEditText.setText("");
            topicEditText.setText("");
            dateEditText.setText("");
            timeEditText.setText("");
        }
    }

    public void updateMeetingsList(List<meetings> updatedMeetingsList) {
        meetingsList.clear();
        meetingsList.addAll(updatedMeetingsList);
        notifyDataSetChanged();
    }
}
