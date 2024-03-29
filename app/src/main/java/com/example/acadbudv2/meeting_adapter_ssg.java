package com.example.acadbudv2;

import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class meeting_adapter_ssg extends RecyclerView.Adapter<meeting_adapter_ssg.MeetingViewHolder> {
    private Context context;
    private List<meetings> meetingsList;
    private DatabaseReference mdatabaseReference;
    private String channelName;

    // Add a member variable to store the maximum participants limit
    private static final int MAX_PARTICIPANTS = 10;
    private int currentSessionNumber;

    // List of valid students to check if a student is valid during participant addition
    private List<String> validStudents = new ArrayList<>();

    public meeting_adapter_ssg(Context context, List<meetings> meetingsList, DatabaseReference mdatabaseReference, String channelName, int currentSessionNumber) {
        this.context = context;
        this.meetingsList = meetingsList;
        this.mdatabaseReference = mdatabaseReference;
        this.channelName = channelName;
        this.currentSessionNumber = currentSessionNumber;

        // Fetch valid student names from both nodes
        fetchValidStudentNames();
    }

    @NonNull
    @Override
    public MeetingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.meeting_adapter_ssg, parent, false);
        return new MeetingViewHolder(view, validStudents); // Pass valid students here
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

        // Keep track of selected student names
        private List<String> selectedStudentNames = new ArrayList<>();
        private List<String> validStudents;

        // Add a constructor to receive valid students
        public MeetingViewHolder(@NonNull View itemView, List<String> validStudents) {
            super(itemView);
            this.validStudents = validStudents;
            subjectTextView = itemView.findViewById(R.id.subject_tv_ssg);
            topicTextView = itemView.findViewById(R.id.topic_tv_ssg);
            dateTimeTextView = itemView.findViewById(R.id.date_time_tv_1_ssg);
            participantsButton = itemView.findViewById(R.id.participant_ssg);
            addButton = itemView.findViewById(R.id.add_btn_ssg);

            addButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showSearchBar(getAdapterPosition() + 1);
                }
            });

            participantsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showParticipants(currentSessionNumber);
                }
            });
        }

        public void bind(meetings meeting) {
            subjectTextView.setText(meeting.getSubject());
            topicTextView.setText(meeting.getTopic());
            String dateTime = meeting.getDate() + " " + meeting.getTime();
            dateTimeTextView.setText(dateTime);


        }
        private void showSearchBar(int sessionNumber) {
            if (selectedStudentNames.size() < 10) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Search Students");

                // Inflate the layout for the dialog
                View dialogView = LayoutInflater.from(context).inflate(R.layout.search_dialog, null);
                builder.setView(dialogView);

                // Get a reference to AutoCompleteTextView
                AutoCompleteTextView autoCompleteTextView = dialogView.findViewById(R.id.autoCompleteTextView);

                // Set the adapter for AutoCompleteTextView
                autoCompleteTextView.setAdapter(createAutoCompleteAdapter());

                // Set positive button click listener
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String selectedStudentName = autoCompleteTextView.getText().toString();

                        // Check if the selected student is valid
                        if (isValidStudent(selectedStudentName)) {
                            // Check if the selected student is not already added
                            if (!TextUtils.isEmpty(selectedStudentName) && !selectedStudentNames.contains(selectedStudentName)) {
                                selectedStudentNames.add(selectedStudentName);

                                // Save the selected user to Firebase
                                saveSelectedUserToFirebase(selectedStudentName, sessionNumber);

                                // Notify the user
                                Toast.makeText(context, "User selected: " + selectedStudentName, Toast.LENGTH_LONG).show();
                            }
                        } else {
                            // Notify the user that the selected student is not valid
                            Toast.makeText(context, "Student not found.", Toast.LENGTH_LONG).show();
                        }

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
            } else {
                // Show a message that the limit has been reached
                Toast.makeText(context, "Maximum number of participants reached (10)", Toast.LENGTH_SHORT).show();
            }
        }

        private ArrayAdapter<String> createAutoCompleteAdapter() {
            return new ArrayAdapter<>(
                    context,
                    android.R.layout.simple_dropdown_item_1line,
                    validStudents
            );
        }

        private boolean isValidStudent(String studentName) {
            // Check if the student name is in the list of valid students
            return validStudents.contains(studentName);
        }

        // Inside the MeetingViewHolder class
        private void saveSelectedUserToFirebase(String selectedStudentName, int sessionNumber) {
            DatabaseReference currentSessionReference = mdatabaseReference.child("Session " + sessionNumber);
            DatabaseReference participantsReference = currentSessionReference.child("Participants");

            participantsReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (!dataSnapshot.hasChild(selectedStudentName)) {
                        long currentParticipantsCount = dataSnapshot.getChildrenCount();

                        if (currentParticipantsCount < MAX_PARTICIPANTS) {
                            // User is not already added and the limit is not reached, proceed to add
                            participantsReference.child(selectedStudentName).setValue(true);

                            // Notify the user
                            Toast.makeText(context, "You added: " + selectedStudentName, Toast.LENGTH_LONG).show();

                            // Update the UI
                            notifyDataSetChanged();

                            // Check if the maximum number of participants (10) has been reached after addition
                            if (currentParticipantsCount + 1 == MAX_PARTICIPANTS) {
                                // Disable the add button
                                addButton.setEnabled(false);
                                Toast.makeText(context, "Maximum number of participants reached (10)", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            // Maximum participants reached, show a message
                            Toast.makeText(context, "Maximum number of participants reached (10)", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // User is already added, show a message
                        Toast.makeText(context, "User already added to this session", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e("Firebase", "Error checking user in the session", databaseError.toException());
                }
            });
        }


        private void showParticipants(int sessionNumber) {
            DatabaseReference currentSessionReference = mdatabaseReference.child("Session " + sessionNumber).child("Participants");

            currentSessionReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        StringBuilder participantsStringBuilder = new StringBuilder();
                        for (DataSnapshot participantSnapshot : dataSnapshot.getChildren()) {
                            String participant = participantSnapshot.getKey();
                            participantsStringBuilder.append(participant).append("\n");
                        }

                        // Show the participants for the selected session
                        showParticipantsDialog(participantsStringBuilder.toString());
                    } else {
                        // Show a message that no participants are added yet
                        Toast.makeText(context, "No participants added yet", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        private void showParticipantsDialog(String participants) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Participants");
            builder.setMessage(participants);

            // Set positive button click listener (optional)
            builder.setPositiveButton("OK", (dialogInterface, i) -> {
                dialogInterface.dismiss(); // Close the dialog
            });

            // Show the dialog
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    private void fetchValidStudentNames() {
        // Fetch valid student names from both nodes and populate the list
        fetchValidStudentNamesFromSsgStudentsNode();
        fetchValidStudentNamesFromStudentsNode();
    }

    private void fetchValidStudentNamesFromSsgStudentsNode() {
        FirebaseDatabase.getInstance().getReference("SSG Students")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            String studentName = snapshot.child("name").getValue(String.class);
                            if (studentName != null) {
                                validStudents.add(studentName);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e("Firebase", "Error fetching SSG Students data", databaseError.toException());
                    }
                });
    }

    private void fetchValidStudentNamesFromStudentsNode() {
        FirebaseDatabase.getInstance().getReference("Students")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            String studentName = snapshot.child("name").getValue(String.class);
                            if (studentName != null) {
                                validStudents.add(studentName);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e("Firebase", "Error fetching Students data", databaseError.toException());
                    }
                });
    }
}
