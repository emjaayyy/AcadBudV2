package com.example.acadbudv2;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class meeting_dialog extends AppCompatActivity {

    private EditText subjectEditText;
    private EditText topicEditText;
    private TextView dateTextView;
    private TextView timeTextView;

    private DatabaseReference databaseReference;

    private int year, month, day, hour, minute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.meeting_dialog);

        subjectEditText = findViewById(R.id.editTextSubject);
        topicEditText = findViewById(R.id.editTextTopic);
        dateTextView = findViewById(R.id.date);
        timeTextView = findViewById(R.id.time);

        Button buttonCreateMeeting = findViewById(R.id.buttonCreateMeeting);

        // Initialize Firebase
        databaseReference = FirebaseDatabase.getInstance().getReference("Meetings");

        buttonCreateMeeting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createMeeting();
            }
        });

        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        minute = calendar.get(Calendar.MINUTE);

        findViewById(R.id.calendar_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        findViewById(R.id.clock_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog();
            }
        });
    }

    private void createMeeting() {
        String subject = subjectEditText.getText().toString();
        String topic = topicEditText.getText().toString();
        String date = dateTextView.getText().toString();
        String time = timeTextView.getText().toString();

        if (subject.isEmpty() || topic.isEmpty() || date.isEmpty() || time.isEmpty()) {
            // Handle empty fields or show an error message.
            return;
        }

        // Create a meetings object with the data
        meetings newMeeting = new meetings(subject, topic, date, time, null); // Participants list is initially empty

        // Push the new meeting to Firebase
        String key = databaseReference.child("Channels").child("Math").child("Session 1").push().getKey();
        databaseReference.child("Channels").child("Math").child("Session 1").child(key).setValue(newMeeting);

    }

    private void showDatePickerDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                // This method will be called when the user selects a date.
                // You can update the dateTextView with the selected date.
                String selectedDate = String.format("%02d-%02d-%04d", dayOfMonth, month + 1, year);
                dateTextView.setText(selectedDate);
            }
        }, year, month, day); // Set initial year, month, and day

        datePickerDialog.show();
    }

    private void showTimePickerDialog() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                // This method will be called when the user selects a time.
                // You can update the timeTextView with the selected time.
                String selectedTime = String.format("%02d:%02d", hourOfDay, minute);
                timeTextView.setText(selectedTime);
            }
        }, hour, minute, true); // Set initial hour and minute

        timePickerDialog.show();
    }
}
