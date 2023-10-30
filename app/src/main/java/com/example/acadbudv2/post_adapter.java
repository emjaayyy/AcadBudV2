package com.example.acadbudv2;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class post_adapter extends RecyclerView.Adapter<post_adapter.PostViewHolder> {
    private List<post_content> posts;

    public post_adapter(List<post_content> posts) {
        this.posts = posts;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_adapter, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        post_content post = posts.get(position);
        holder.bind(post);
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public void addPost(post_content post) {
        posts.add(post);
        notifyItemInserted(posts.size() - 1);

    }

    public class PostViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        TextView dateTextView;
        TextView contentTextView;
        Button postAddBtn;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            contentTextView = itemView.findViewById(R.id.contentTextView);
            postAddBtn = itemView.findViewById(R.id.postAddBtn);

            postAddBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onAddMeetingClick(v);
                }
            });
        }

        public void bind(post_content post) {
            nameTextView.setText(post.getName());
            dateTextView.setText(post.getDate());
            contentTextView.setText(post.getPosts());
        }
    }
    public void onAddMeetingClick(View view) {
        // Handle the button click to show the meeting dialog.
        showMeetingDialog(view.getContext());
    }
    private void showMeetingDialog(Context context) {
        // Create a custom dialog for meetings using your existing setup.
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.meeting_dialog);

        // Initialize the dialog's views
        EditText editTextDate = dialog.findViewById(R.id.editTextDate);
        EditText editTextTime = dialog.findViewById(R.id.editTextTime);
        EditText editTextSubject = dialog.findViewById(R.id.editTextSubject);
        EditText editTextTopic = dialog.findViewById(R.id.editTextTopic);
        Button buttonCreateMeeting = dialog.findViewById(R.id.buttonCreateMeeting);

        // Set a listener for the "Create Meeting" button
        buttonCreateMeeting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String date = editTextDate.getText().toString();
                String time = editTextTime.getText().toString();
                String subject = editTextSubject.getText().toString();
                String topic = editTextTopic.getText().toString();

                if (meetingCreatedSuccessfully(date, time, subject, topic)) {
                    // Handle successful meeting creation, e.g., updating UI.
                    // Optionally, you can also add the meeting to your meeting adapter here.
                }

                dialog.dismiss();
            }
        });

        // Show the dialog
        dialog.show();
    }

    private boolean meetingCreatedSuccessfully(String date, String time, String subject, String topic) {
        // Implement your logic to create the meeting.
        // Return true if the meeting is created successfully, false otherwise.
        // You can use your existing `meeting_adapter` logic here.
        // For demonstration, we return true.
        return true;
    }
}
