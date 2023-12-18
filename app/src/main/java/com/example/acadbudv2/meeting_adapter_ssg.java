package com.example.acadbudv2;

import android.content.Context;
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
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class meeting_adapter_ssg extends RecyclerView.Adapter<meeting_adapter_ssg.MeetingViewHolder> {
    private List<meetings> meetingsList;
    private Context context;

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

        // Keep track of selected student names
        private List<String> selectedStudentNames = new ArrayList<>();

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
                    showSearchBar();
                }
            });

            participantsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showParticipants();
                }
            });
        }

        public void bind(meetings meeting) {
            subjectTextView.setText(meeting.getSubject());
            topicTextView.setText(meeting.getTopic());
            String dateTime = meeting.getDate() + " " + meeting.getTime();
            dateTimeTextView.setText(dateTime);
        }

        private void showSearchBar() {
            if (selectedStudentNames.size() < 10) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Search Students");

                // Inflate the layout for the dialog
                View dialogView = LayoutInflater.from(context).inflate(R.layout.search_dialog, null);
                builder.setView(dialogView);

                // Get a reference to AutoCompleteTextView
                AutoCompleteTextView autoCompleteTextView = dialogView.findViewById(R.id.autoCompleteTextView);

                // Fetch student names from both nodes and populate AutoCompleteTextView
                List<String> studentNames = new ArrayList<>();
                fetchStudentNamesFromBothNodes(studentNames, autoCompleteTextView);

                // Set positive button click listener
                builder.setPositiveButton("OK", (dialogInterface, i) -> {
                    // Handle the selected student name
                    String selectedStudentName = autoCompleteTextView.getText().toString();
                    if (!selectedStudentNames.contains(selectedStudentName) && selectedStudentNames.size() < 10) {
                        selectedStudentNames.add(selectedStudentName);
                    }

                    dialogInterface.dismiss(); // Close the dialog
                });

                // Set negative button click listener (optional)
                builder.setNegativeButton("Cancel", (dialogInterface, i) -> {
                    dialogInterface.dismiss(); // Close the dialog
                });

                // Show the dialog
                AlertDialog dialog = builder.create();
                dialog.show();
            } else {
                // Show a message that the limit has been reached
                Toast.makeText(context, "Maximum number of participants reached (10)", Toast.LENGTH_SHORT).show();
            }
        }

        private void showParticipants() {
            if (!selectedStudentNames.isEmpty()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Participants");

                // Convert the list of selected student names to a single string
                StringBuilder participantsStringBuilder = new StringBuilder();
                for (String participant : selectedStudentNames) {
                    participantsStringBuilder.append(participant).append("\n");
                }

                builder.setMessage(participantsStringBuilder.toString());

                // Set positive button click listener (optional)
                builder.setPositiveButton("OK", (dialogInterface, i) -> {
                    dialogInterface.dismiss(); // Close the dialog
                });

                // Show the dialog
                AlertDialog dialog = builder.create();
                dialog.show();
            } else {
                // Show a message that no participants are added yet
                Toast.makeText(context, "No participants added yet", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void fetchStudentNamesFromBothNodes(List<String> studentNames, AutoCompleteTextView autoCompleteTextView) {
        FirebaseDatabase.getInstance().getReference().child("Students")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot studentsSnapshot) {
                        for (DataSnapshot studentSnapshot : studentsSnapshot.getChildren()) {
                            String studentName = studentSnapshot.child("name").getValue(String.class);
                            if (studentName != null) {
                                studentNames.add(studentName);
                            }
                        }

                        // Fetch names from "SSG Students" node
                        fetchStudentNamesFromSsgStudentsNode(studentNames, autoCompleteTextView);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e("Firebase", "Error fetching Students data", databaseError.toException());
                    }
                });
    }

    private void fetchStudentNamesFromSsgStudentsNode(List<String> studentNames, AutoCompleteTextView autoCompleteTextView) {
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

                        // Update AutoCompleteTextView adapter
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                                context,
                                android.R.layout.simple_dropdown_item_1line,
                                studentNames
                        );
                        autoCompleteTextView.setAdapter(adapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e("Firebase", "Error fetching SSG Students data", databaseError.toException());
                    }
                });
    }
}
