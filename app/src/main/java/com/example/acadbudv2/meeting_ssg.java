package com.example.acadbudv2;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class meeting_ssg extends AppCompatActivity {
    private DatabaseReference mdatabaseReference;
    private String channelName = "Math"; // Update with your channel name or retrieve dynamically
    private Context context;
    private ArrayAdapter<String> adapter;
    private int currentSessionNumber; // Update or manage session numbers accordingly

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.meeting_ssg);
        FirebaseApp.initializeApp(this);
        mdatabaseReference = FirebaseDatabase.getInstance().getReference("Meetings");

        context = this;

        fetchLatestSessionNumber();

        Button home = findViewById(R.id.home_btn_meeting_ssg);
        Button notif = findViewById(R.id.notif_btn_meeting_ssg);
        Button me = findViewById(R.id.profile_btn_meeting_ssg);

        me.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent m1 = new Intent(meeting_ssg.this, profile_ssg.class);
                startActivity(m1);
            }
        });

        notif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent n1 = new Intent(meeting_ssg.this, notif.class);
                startActivity(n1);
            }
        });
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent user = new Intent(meeting_ssg.this, home_ssg.class);
                startActivity(user);
            }
        });


        // Create meeting button click listener
        Button createMeetingBtn = findViewById(R.id.create_meeting_btn_user);
        createMeetingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Show a dialog to input meeting details
                showCreateMeetingDialog();
            }
        });

        // Set up AutoCompleteTextView for search suggestions
        List<String> studentNames = new ArrayList<>();
        fetchStudentNamesFromSsgStudentsNode(studentNames);

        // Set up RecyclerView with a layout manager
        RecyclerView recyclerView = findViewById(R.id.recyclerView_ssg_meeting);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        // Fetch meetings data and set up RecyclerView
        fetchMeetingsData();

    }

    private void fetchLatestSessionNumber() {
        mdatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Find the latest session number
                for (DataSnapshot sessionSnapshot : dataSnapshot.getChildren()) {
                    int sessionNumber = extractSessionNumber(sessionSnapshot.getKey());
                    if (sessionNumber > currentSessionNumber) {
                        currentSessionNumber = sessionNumber;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Firebase", "Failed to retrieve latest session number", databaseError.toException());
                Toast.makeText(context, "Failed to retrieve latest session number", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private int extractSessionNumber(String sessionKey) {
        // Assuming sessionKey is in the format "Session 1", "Session 2", etc.
        try {
            return Integer.parseInt(sessionKey.split(" ")[1]);
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            // Handle parsing errors or index out of bounds
            e.printStackTrace();
        }
        return 0;
    }

    private void fetchMeetingsData() {
        mdatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<meetings> meetingsList = new ArrayList<>();

                for (DataSnapshot sessionSnapshot : dataSnapshot.getChildren()) {
                    // Assuming that each child is a session (Session 1, Session 2, etc.)
                    DataSnapshot detailsSnapshot = sessionSnapshot.child("Details");
                    meetings meeting = detailsSnapshot.getValue(meetings.class);

                    if (meeting != null) {
                        // Assuming that Participants is directly under the session
                        DataSnapshot participantsSnapshot = sessionSnapshot.child("Participants");
                        // Fetch participants data (if needed)

                        // Add the meeting to the list
                        meetingsList.add(meeting);
                        scheduleRemindersForParticipants(sessionSnapshot.getKey(), meeting);
                    }
                    
                }

                // Update the RecyclerView adapter with the new data
                updateRecyclerView(meetingsList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Firebase", "Failed to retrieve meetings", databaseError.toException());
                Toast.makeText(context, "Failed to retrieve meetings", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void scheduleRemindersForParticipants(String sessionNumber, meetings meeting) {
        DatabaseReference currentSessionReference = mdatabaseReference.child(sessionNumber).child("Participants");

        // Fetch participants data
        currentSessionReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot participantSnapshot : dataSnapshot.getChildren()) {
                    String participantName = participantSnapshot.getKey();

                    // Calculate the alarmTime based on your logic
                    long alarmTime = calculateAlarmTimeBasedOnYourLogic(meeting.getDate(), meeting.getTime());

                    if (alarmTime > System.currentTimeMillis()) {
                        // Schedule a local notification for each participant
                        scheduleLocalNotification(alarmTime, meeting.getTopic(), participantName);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors if any
            }
        });
    }

    private void scheduleLocalNotification(long alarmTime, String topic, String participantName) {
        Intent intent = new Intent(this, reminder_receiver.class);
        intent.putExtra("topic", topic);
        intent.putExtra("participant", participantName);

        // Generate a unique requestCode based on meeting details
        int requestCode = generateUniqueRequestCode(topic, participantName);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, alarmTime, pendingIntent);
    }

    // Helper method to generate a unique requestCode based on meeting details
    private int generateUniqueRequestCode(String topic, String participantName) {
        // Concatenate the topic and participantName to create a unique identifier
        String uniqueIdentifier = topic + "_" + participantName;

        // Use hashCode() to convert the string to an integer
        return Math.abs(uniqueIdentifier.hashCode());
    }

    private long calculateAlarmTimeBasedOnYourLogic(String meetingDate, String meetingTime) {
        // Combine the meeting date and time into a single string
        String dateTimeString = meetingDate + " " + meetingTime;

        // Define the format of your date and time
        SimpleDateFormat dateFormat = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm", Locale.getDefault());
        }

        try {
            // Parse the combined date and time string
            Date dateTime = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                dateTime = dateFormat.parse(dateTimeString);
            }

            // Set the parsed date and time to a Calendar instance
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(dateTime);

            // Return the time in milliseconds
            return calendar.getTimeInMillis();
        } catch (ParseException e) {
            // Handle parsing errors
            e.printStackTrace();
            return 0; // Return an appropriate value indicating an error
        }
    }
    private void updateRecyclerView(List<meetings> meetingsList) {
        meeting_adapter_ssg adapter = new meeting_adapter_ssg(context, meetingsList, mdatabaseReference, channelName, currentSessionNumber);
        RecyclerView recyclerView = findViewById(R.id.recyclerView_ssg_meeting);
        recyclerView.setAdapter(adapter);
    }

    private void fetchStudentNamesFromSsgStudentsNode(List<String> studentNames) {
        FirebaseDatabase.getInstance().getReference("SSG Students")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            String studentName = snapshot.child("name").getValue(String.class);
                            if (studentName != null) {
                                studentNames.add(studentName);
                            }
                        }
                        // Continue to fetch student names from the "Students" node
                        fetchStudentNamesFromStudentsNode(studentNames);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Handle errors if any
                    }
                });
    }

    private void fetchStudentNamesFromStudentsNode(List<String> studentNames) {
        FirebaseDatabase.getInstance().getReference("Students")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            String studentName = snapshot.child("name").getValue(String.class);
                            if (studentName != null) {
                                studentNames.add(studentName);
                            }
                        }
                        // Set up the ArrayAdapter for AutoCompleteTextView
                        adapter = new ArrayAdapter<>(
                                meeting_ssg.this,
                                android.R.layout.simple_dropdown_item_1line,
                                studentNames
                        );
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Handle errors if any
                    }
                });
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

        // AutoCompleteTextView for student search is removed

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

                if (isValidTime(time) && isValidWeekday(date)) {
                    // Increment session number
                    currentSessionNumber++;

                    // Create a new meeting object
                    meetings newMeeting = new meetings(selectedSubject, topic, date, time, new ArrayList<>());

                    // Save the new meeting to Firebase under the current session
                    saveMeetingToFirebase(newMeeting, currentSessionNumber);

                    // Inform the user that the meeting has been created
                    Toast.makeText(context, "Meeting created successfully", Toast.LENGTH_SHORT).show();
                } else {
                    // Display an error message if the conditions are not met
                    if (!isValidTime(time)) {
                        Toast.makeText(context, "Invalid time selection. Please choose a time between 7 am and 4 pm.", Toast.LENGTH_LONG).show();
                    } else if (!isValidWeekday(date)) {
                        Toast.makeText(context, "Invalid date selection. Please choose a date between Monday and Friday.", Toast.LENGTH_LONG).show();
                    }
                }
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

    private void saveMeetingToFirebase(meetings newMeeting, int sessionNumber) {
        DatabaseReference currentSessionReference = mdatabaseReference.child("Session " + sessionNumber);
        DatabaseReference detailsReference = currentSessionReference.child("Details");
        DatabaseReference participantsReference = currentSessionReference.child("Participants");

        // Save meeting details under "Details"
        detailsReference.setValue(newMeeting);

        // Save participants under "Participants"
        for (String participantName : newMeeting.getParticipants()) {
            participantsReference.child(participantName).setValue(true);
        }
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
        if (!TextUtils.isEmpty(time)) {
            String[] parts = time.split(":");
            if (parts.length == 2) {
                int hour = Integer.parseInt(parts[0]);
                int minute = Integer.parseInt(parts[1]);

                // Check if the time is between 7 am and 4 pm
                return (hour == 7 && minute == 0) || (hour > 7 && hour < 16) || (hour == 16 && minute == 0);
            }
        }
        return false;
    }

    private boolean isValidWeekday(String date) {
        if (!TextUtils.isEmpty(date)) {
            String[] parts = date.split("/");
            if (parts.length == 3) {
                int month = Integer.parseInt(parts[0]);
                int day = Integer.parseInt(parts[1]);
                int year = Integer.parseInt(parts[2]);

                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month - 1, day); // month is 0-indexed

                int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

                // Check if the day is between Monday and Friday
                return dayOfWeek >= Calendar.MONDAY && dayOfWeek <= Calendar.FRIDAY;
            }
        }
        return false;
    }

    public void showSearchBar(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Search Students");

        // Inflate the layout for the dialog
        View dialogView = LayoutInflater.from(this).inflate(R.layout.search_dialog, null);
        builder.setView(dialogView);

        // Get a reference to AutoCompleteTextView
        AutoCompleteTextView autoCompleteTextView = dialogView.findViewById(R.id.autoCompleteTextView);

        // Populate AutoCompleteTextView with student names
        List<String> studentNames = new ArrayList<>();
        fetchStudentNamesFromSsgStudentsNode(studentNames);
        fetchStudentNamesFromStudentsNode(studentNames);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                studentNames
        );
        autoCompleteTextView.setAdapter(adapter);

        // Set positive button click listener
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss(); // Close the dialog
            }
        });

        // Set negative button click listener (optional)
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss(); // Close the dialog
            }
        });

        // Show the dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}