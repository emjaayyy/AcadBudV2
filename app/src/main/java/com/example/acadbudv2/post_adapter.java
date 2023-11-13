package com.example.acadbudv2;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.List;

public class post_adapter extends RecyclerView.Adapter<post_adapter.PostViewHolder> {
    private List<post_content> posts;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private boolean isSSGStudent;

    public post_adapter(List<post_content> posts) {
        this.posts = posts;
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        checkUserType();
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
        Button deleteBtn;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            contentTextView = itemView.findViewById(R.id.contentTextView);
            postAddBtn = itemView.findViewById(R.id.postAddBtn);
            deleteBtn = itemView.findViewById(R.id.delete_btn_post_adapter);

            postAddBtn.setVisibility(isSSGStudent ? View.VISIBLE : View.GONE);

            postAddBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Start the meeting_adapter activity when the button is clicked.
                    startMeetingAdapterActivity(v.getContext());
                }
            });

            // Add the click listener for the delete button
            deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Get the position of the clicked item
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        // Call the method to show the delete confirmation dialog
                        showDeleteConfirmationDialog(position);
                    }
                }
            });
        }

        public void bind(post_content post) {
            nameTextView.setText(post.getName());
            dateTextView.setText(post.getDate());
            contentTextView.setText(post.getPosts());
        }

        private void startMeetingAdapterActivity(Context context) {
            // Create an Intent to start the meeting_adapter activity.
            Intent intent = new Intent(context, meeting_list.class);

            // You can also pass any data to the meeting_adapter activity if needed.
            // intent.putExtra("key", value);

            context.startActivity(intent);
        }

        private void showDeleteConfirmationDialog(final int position) {
            AlertDialog.Builder builder = new AlertDialog.Builder(itemView.getContext());
            builder.setTitle("Delete Post");
            builder.setMessage("Are you sure you want to delete this post?");

            // Add the buttons
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User clicked Yes button
                    deletePost(position);
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User clicked No button, do nothing
                }
            });

            // Create and show the dialog
            AlertDialog dialog = builder.create();
            dialog.show();
        }

        private void deletePost(int position) {
            post_content post = posts.get(position);

            // Remove the post from the list
            posts.remove(position);

            // Notify the adapter about the item removal
            notifyItemRemoved(position);

            // Delete the post from Firebase
            String postId = post.getPosts(); // Use getPosts() since it is the identifier
            Log.d("POST_DELETE", "Deleting post with postId: " + postId);

            if (postId != null) {
                mDatabase.child("posts").child(postId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // Post deleted successfully
                            Log.d("POST_DELETE", "Post deleted from Firebase");
                        } else {
                            // Handle the error
                            Log.e("POST_DELETE", "Error deleting post from Firebase", task.getException());
                        }
                    }
                });
            }
    }
    }

    private void checkUserType() {
        String userUID = getUserUID();
        if (userUID != null) {
            mDatabase.child("SSG Students").child(userUID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    isSSGStudent = dataSnapshot.exists();

                    // Debugging: Log the value of isSSGStudent
                    Log.d("SSG_DEBUG", "Is SSG Student: " + isSSGStudent);

                    notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle errors
                }
            });
        }
    }

    private String getUserUID() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            return user.getUid();
        } else {
            // Handle the case where the user is not authenticated
            return null;
        }
    }
}
