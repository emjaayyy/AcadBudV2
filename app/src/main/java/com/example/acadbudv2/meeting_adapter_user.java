package com.example.acadbudv2;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
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
                    showLeaveConfirmationDialog();
                }
            });
        }

        private void showLeaveConfirmationDialog() {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);

            // Set the title and message of the dialog
            builder.setTitle("Leave Meeting");
            builder.setMessage("Are you sure you want to leave the meeting?");

            // Add buttons to the dialog
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // User confirmed, show the rating dialog
                    showRatingDialog(dialog); // Pass the 'dialog' variable to the next function
                }
            });

            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // User canceled, do nothing
                    dialog.dismiss();
                }
            });

            // Create and show the dialog
            AlertDialog dialog = builder.create();
            dialog.show();
        }

        private void showRatingDialog(DialogInterface leaveDialog) {
            // Inflate the rating dialog layout
            View dialogView = LayoutInflater.from(context).inflate(R.layout.rating_dialog, null);

            // Create the AlertDialog builder
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setView(dialogView);

            // Find views in the rating dialog
            Button closeButton = dialogView.findViewById(R.id.close_btn_meeting);
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
                        // Handle the rating submission
                        // For now, let's just close the dialog
                        leaveDialog.dismiss();
                        // You can add additional logic for submitting the rating
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
    }
}
