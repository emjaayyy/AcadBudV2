package com.example.acadbudv2;

import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class meeting_adapter_user extends RecyclerView.Adapter<meeting_adapter_user.MeetingViewHolder> {

    private Context context;
    private List<meetings> meetingsList;
    private String currentUser;
    private DatabaseReference mdatabaseReference;

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
        private Button leaveButton;
        private Button participantsButton;
        private List<String> participants;

        public MeetingViewHolder(@NonNull View itemView) {
            super(itemView);
            subjectTextView = itemView.findViewById(R.id.subject_tv_user);
            topicTextView = itemView.findViewById(R.id.topic_tv_user);
            dateTimeTextView = itemView.findViewById(R.id.date_time_tv_1_user);
            leaveButton = itemView.findViewById(R.id.leave_btn_user);
            participantsButton = itemView.findViewById(R.id.participant_user);

            // Set click listeners for buttons
            leaveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showLeaveConfirmationDialog(getAdapterPosition() + 1); // Adding 1 to make it 1-indexed
                }
            });
            participantsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && position < meetingsList.size()) {
                        meetings meeting = meetingsList.get(position);
                        showParticipantsDialog(meeting.getParticipants());
                    }
                }
            });
        }

        public void bind(meetings meeting) {
            subjectTextView.setText(meeting.getSubject());
            topicTextView.setText(meeting.getTopic());
            dateTimeTextView.setText(context.getString(R.string.date_time_format, meeting.getDate(), meeting.getTime()));
            participants = meeting.getParticipants();
        }

        private void showLeaveConfirmationDialog(int sessionId) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);

            // Set the title and message of the dialog
            builder.setTitle("Leave Meeting");
            builder.setMessage("Are you a Student or a Tutor?");

            // Add buttons to the dialog
            builder.setPositiveButton("Student", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // User confirmed as a student, show the tutor identification dialog
                    showTutorIdentificationDialog(dialog, sessionId);
                }
            });

            builder.setNegativeButton("Tutor", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // User confirmed as a tutor, show the thank you dialog
                    showThankYouDialog(dialog);
                }
            });

            // Create and show the dialog
            AlertDialog dialog = builder.create();
            dialog.show();
        }

        private void showParticipantsDialog(List<String> participants) {
            // Check if the participants list is not null
            if (participants != null) {
                // Display the participants list in a dialog
                StringBuilder participantsStringBuilder = new StringBuilder();

                if (!participants.isEmpty()) {
                    for (String participant : participants) {
                        participantsStringBuilder.append(participant).append("\n");
                    }
                } else {
                    participantsStringBuilder.append("No participants available");
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Participants");
                builder.setMessage(participantsStringBuilder.toString());

                // Set positive button click listener (optional)
                builder.setPositiveButton("OK", (dialogInterface, i) -> {
                    dialogInterface.dismiss(); // Close the dialog
                });

                // Show the dialog
                AlertDialog dialog = builder.create();
                dialog.show();
            } else {
                // Handle the case when participants list is null
                Toast.makeText(context, "No participants available", Toast.LENGTH_SHORT).show();
            }
        }

        private void showTutorIdentificationDialog(DialogInterface leaveDialog, int sessionId) {
            // Inflate the tutor identification dialog layout
            View dialogView = LayoutInflater.from(context).inflate(R.layout.identify_tutor, null);

            // Create the AlertDialog builder and apply the custom style
            AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.CustomAlertDialog);
            builder.setView(dialogView);


            // Find views in the tutor identification dialog
            Button closeButton = dialogView.findViewById(R.id.close_btn_identifier);
            AutoCompleteTextView tutorNameEditText = dialogView.findViewById(R.id.tutor_1);
            Button enterButton = dialogView.findViewById(R.id.Enter_tutor);

            // Fetch tutor names before setting up the adapter
            fetchTutorNamesFromSsgStudentsNode(tutorNameEditText, sessionId);

            // Set up close button click listener
            closeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Close the tutor identification dialog
                    leaveDialog.dismiss();
                }
            });

            // Set up enter button click listener
            enterButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Get the entered tutor name
                    String tutorName = tutorNameEditText.getText().toString().trim();

                    if (!tutorName.isEmpty()) {
                        // Check if the entered tutor name is in the list of participants
                        if (participants.contains(tutorName)) {
                            // User entered a valid tutor name, show the rating dialog
                            showRatingDialog(leaveDialog, tutorName);
                        } else {
                            // Display a message if the entered tutor is not a participant
                            Toast.makeText(context, "Invalid tutor name", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // Display a message if no tutor name is entered
                        Toast.makeText(context, "Please enter the name of the Tutor", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            // Create and show the dialog
            AlertDialog tutorIdentificationDialog = builder.create();
            tutorIdentificationDialog.show();
        }

        private void fetchTutorNamesFromSsgStudentsNode(AutoCompleteTextView tutorNameEditText, int sessionId) {
            List<String> tutorNames = new ArrayList<>();
            FirebaseDatabase.getInstance().getReference("SSG Students")
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                String tutorName = snapshot.child("name").getValue(String.class);
                                if (tutorName != null) {
                                    tutorNames.add(tutorName);
                                }
                            }
                            // Continue to fetch tutor names from the "Students" node
                            fetchTutorNamesFromStudentsNode(tutorNames, tutorNameEditText, sessionId);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            // Handle errors if any
                        }
                    });
        }

        private void fetchTutorNamesFromStudentsNode(List<String> tutorNames, AutoCompleteTextView tutorNameEditText, int sessionId) {
            FirebaseDatabase.getInstance().getReference("Students")
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                String tutorName = snapshot.child("name").getValue(String.class);
                                if (tutorName != null) {
                                    tutorNames.add(tutorName);
                                }
                            }

                            // Set up the ArrayAdapter for AutoCompleteTextView
                            ArrayAdapter<String> tutorAdapter = new ArrayAdapter<>(
                                    context,
                                    android.R.layout.simple_dropdown_item_1line,
                                    tutorNames
                            );
                            tutorNameEditText.setAdapter(tutorAdapter);

                            // Store the session ID in the AutoCompleteTextView's tag for later use
                            tutorNameEditText.setTag(sessionId);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            // Handle errors if any
                        }
                    });
        }

        private void showRatingDialog(DialogInterface leaveDialog, String tutorName) {
            // Inflate the rating dialog layout
            View dialogView = LayoutInflater.from(context).inflate(R.layout.rating_dialog, null);

            // Create the AlertDialog builder
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setView(dialogView);

            // Find views in the rating dialog
            Button closeButton = dialogView.findViewById(R.id.close_btn_rating);
            TextView ratingTextView = dialogView.findViewById(R.id.tv_rating);
            RatingBar ratingBar = dialogView.findViewById(R.id.rating_bar);
            Button submitButton = dialogView.findViewById(R.id.button_submit_rating);

            // Set up close button click listener
            closeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Close the rating dialog
                    leaveDialog.dismiss();
                }
            });

            // Set up submit button click listener
            submitButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Check if a rating is given
                    float rating = ratingBar.getRating();
                    if (rating > 0) {
                        // Save the rating to Firebase
                        saveRatingToFirebase(tutorName, rating);

                        // For now, let's just close the dialogs
                        leaveDialog.dismiss();
                        builder.create().dismiss();
                        // You can add additional logic for submitting the rating
                        // Show a message or perform an action based on the rating
                    } else {
                        // Display a message if no rating is given
                        ratingTextView.setText("Please leave a rating before you leave!");
                    }
                }
            });

            // Create and show the dialog
            AlertDialog ratingDialog = builder.create();
            ratingDialog.show();
        }

        private void saveRatingToFirebase(String tutorName, float rating) {
            // Assuming "Ratings" is the new node in your Firebase structure
            DatabaseReference ratingsRef = FirebaseDatabase.getInstance().getReference("Ratings");

            // Update the existing count of stars
            ratingsRef.child(tutorName).runTransaction(new Transaction.Handler() {
                @NonNull
                @Override
                public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                    // Get the current count
                    Float currentRating = mutableData.getValue(Float.class);

                    // If there is no existing count, assume it's 0
                    if (currentRating == null) {
                        currentRating = 0.0f;
                    }

                    // Increment the count by the new rating
                    currentRating += rating;

                    // Update the count in Firebase
                    mutableData.setValue(currentRating);

                    // Indicate that the transaction was successful
                    return Transaction.success(mutableData);
                }

                @Override
                public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {
                    // Handle the completion of the transaction if needed
                    if (databaseError != null) {
                        Log.d("Firebase", "Transaction failed: " + databaseError.getMessage());
                    } else {
                        Log.d("Firebase", "Transaction succeeded.");
                    }
                }
            });
        }


        private void showThankYouDialog(DialogInterface leaveDialog) {
            // Inflate the thank you dialog layout
            View dialogView = LayoutInflater.from(context).inflate(R.layout.tutor_message_dialog, null);

            // Create the AlertDialog builder
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setView(dialogView);

            // Find views in the thank you dialog
            Button closeButton = dialogView.findViewById(R.id.close_btn_tutor_message);

            // Set up close button click listener
            closeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Close the thank you dialog
                    leaveDialog.dismiss();
                }
            });

            // Create and show the dialog
            AlertDialog thankYouDialog = builder.create();
            thankYouDialog.show();
        }
    }
}
