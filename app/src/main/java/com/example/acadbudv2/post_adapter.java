package com.example.acadbudv2;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            contentTextView = itemView.findViewById(R.id.contentTextView);
            postAddBtn = itemView.findViewById(R.id.postAddBtn);

            postAddBtn.setVisibility(isSSGStudent ? View.VISIBLE : View.GONE);

            postAddBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Start the meeting_adapter activity when the button is clicked.
                    startMeetingAdapterActivity(v.getContext());
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