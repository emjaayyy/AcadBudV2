package com.example.acadbudv2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class post_adapter extends RecyclerView.Adapter<post_adapter.PostViewHolder> {
    private List<post_content> posts;
    private String currentUser;

    public post_adapter(List<post_content> posts) {
        this.posts = posts;
    }

    public void setCurrentUser(String currentUser) {
        this.currentUser = currentUser;
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

        // Check if the post owner is the current user
        boolean isCurrentUserPost = post.getName().equals(currentUser);

        // Set visibility for edit and delete buttons based on ownership
        holder.editButton.setVisibility(isCurrentUserPost ? View.VISIBLE : View.GONE);
        holder.deleteButton.setVisibility(isCurrentUserPost ? View.VISIBLE : View.GONE);
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
        Button editButton;
        Button deleteButton;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            contentTextView = itemView.findViewById(R.id.contentTextView);
            editButton = itemView.findViewById(R.id.edit_btn_post_adapter);
            deleteButton = itemView.findViewById(R.id.delete_btn_post_adapter);

            // Set onClickListeners for edit and delete buttons if needed
        }

        public void bind(post_content post) {
            nameTextView.setText(post.getName());
            dateTextView.setText(post.getDate());
            contentTextView.setText(post.getPosts());
        }
    }
}
