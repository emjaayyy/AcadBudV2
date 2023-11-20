package com.example.acadbudv2;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;

public class meeting_ssg extends AppCompatActivity {
    private DatabaseReference mdatabaseReference;
    private String channelName = "Math";
    private Context context;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.meeting_ssg);
        FirebaseApp.initializeApp(this);
        mdatabaseReference = FirebaseDatabase.getInstance().getReference("Meetings");

        context = this;

        // Create meeting button click listener
        Button createMeetingBtn = findViewById(R.id.create_meeting_btn_user);
        createMeetingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Show a dialog to input meeting details
                showCreateMeetingDialog();
            }
        });

        // Fetch meetings data and set up RecyclerView
        fetchMeetingsData();
    }

    private void fetchMeetingsData() {
        // Fetch data from Firebase and update RecyclerView
        // (Your existing code for fetching and updating meetings data)
    }

    private void showCreateMeetingDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Create Meeting");

        // Inflate the layout for the dialog
        View dialogView = LayoutInflater.from(context).inflate(R.layout.meeting_dialog, null);
        builder.setView(dialogView);

        // Get references to views in the dialog
        Spinner subjectSpinner = dialogView.findViewById(R.id.subjectSpinner);
        EditText topicEditText = dialogView.findViewById(R.id.editTextTopic);
        TextView dateTextView = dialogView.findViewById(R.id.dateTextView);
        TextView timeTextView = dialogView.findViewById(R.id.timeTextView);

        Button datePickerButton = dialogView.findViewById(R.id.datePickerButton);
        Button timePickerButton = dialogView.findViewById(R.id.timePickerButton);

// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                context,
                R.array.subjects_array,
                android.R.layout.simple_spinner_item
        );

// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

// Apply the adapter to the spinner
        subjectSpinner.setAdapter(adapter);

        subjectSpinner.setPrompt("Select Subject");
// Set positive button click listener
        builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Retrieve values from the dialog
                String selectedSubject = subjectSpinner.getSelectedItem().toString();
                String topic = topicEditText.getText().toString();
                String date = dateTextView.getText().toString();
                String time = timeTextView.getText().toString();

                // Create a new meeting object
                meetings newMeeting = new meetings(selectedSubject, topic, date, time, new ArrayList<>());

                // Save the new meeting to Firebase
                mdatabaseReference.child("Channels").child(channelName).child("Session 1").push().setValue(newMeeting);

                // Inform the user that the meeting has been created
                Toast.makeText(context, "Meeting created successfully", Toast.LENGTH_SHORT).show();
            }
        });

// Set negative button click listener
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Cancel the dialog
            }
        });

// Set click listener for date picker button
        datePickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePicker(dateTextView);
            }
        });

// Set click listener for time picker button
        timePickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimePicker(timeTextView);
            }
        });

// Display the dialog
        builder.show();

    }
        private void showDatePicker(final TextView dateTextView) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                // Note: month is 0-indexed, so add 1 to get the correct month
                month += 1;
                String formattedDate = String.format("%02d/%02d/%04d", month, day, year);
                dateTextView.setText(formattedDate);
            }
        }, year, month, day);

        datePickerDialog.show();
    }

    private void showTimePicker(final TextView timeTextView) {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                String formattedTime = String.format("%02d:%02d", hour, minute);
                timeTextView.setText(formattedTime);
            }
        }, hour, minute, false);

        timePickerDialog.show();
    }

    private boolean isValidTime(String time) {
        // Implement logic to validate time (between 7 am and 4 pm)
        // Example:
        if (!TextUtils.isEmpty(time)) {
            String[] parts = time.split(":");
            if (parts.length == 2) {
                int hour = Integer.parseInt(parts[0]);
                int minute = Integer.parseInt(parts[1]);

                return hour >= 7 && hour <= 16 && minute == 0; // 7 am to 4 pm
            }
        }

        return false;
    }

    private boolean isValidWeekday(String date) {
        // Implement logic to validate if the date is a weekday
        // Example:
        if (!TextUtils.isEmpty(date)) {
            String[] parts = date.split("/");
            if (parts.length == 3) {
                int month = Integer.parseInt(parts[0]);
                int day = Integer.parseInt(parts[1]);
                int year = Integer.parseInt(parts[2]);

                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month - 1, day); // month is 0-indexed

                int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
                return dayOfWeek >= Calendar.MONDAY && dayOfWeek <= Calendar.FRIDAY; // Weekdays
            }
        }

        return false;
    }
}