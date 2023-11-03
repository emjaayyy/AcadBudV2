package com.example.acadbudv2;

import android.content.Context;
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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class post_adapter extends RecyclerView.Adapter<post_adapter.PostViewHolder> {
    private List<post_content> posts;
    private boolean isUserChecked = false;
    private Set<String> ssgStudentUIDs = new HashSet<>();

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

        // Check if the user should see the postAddBtn based on their LRN (UID)
        if (!isUserChecked) {
            checkUserLRN(holder.postAddBtn);
        }

        // Set the visibility of postAddBtn based on whether the user is an SSG Student
        if (ssgStudentUIDs.contains(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
            holder.postAddBtn.setVisibility(View.VISIBLE);
        } else {
            holder.postAddBtn.setVisibility(View.GONE);
        }
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
        }

        public void bind(post_content post) {
            nameTextView.setText(post.getName());
            dateTextView.setText(post.getDate());
            contentTextView.setText(post.getPosts());
        }
    }

    private void checkUserLRN(final Button postAddBtn) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        if (user != null) {
            DatabaseReference ssgStudentsRef = FirebaseDatabase.getInstance().getReference("SSG Students");

            ssgStudentsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot studentSnapshot : dataSnapshot.getChildren()) {
                        String studentUID = studentSnapshot.getKey();
                        ssgStudentUIDs.add(studentUID);
                    }

                    isUserChecked = true;
                    notifyDataSetChanged(); // Refresh RecyclerView after fetching SSG Students
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Handle the error
                }
            });
        }
    }
}
