package com.example.acadbudv2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

        holder.postUserName.setText(post.getName());
        holder.postDate.setText(post.getDate());
        holder.postContent.setText(post.getPosts());
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public class PostViewHolder extends RecyclerView.ViewHolder {
        TextView postUserName;
        TextView postDate;
        TextView postContent;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            postUserName = itemView.findViewById(R.id.postUserName);
            postDate = itemView.findViewById(R.id.postDate);
            postContent = itemView.findViewById(R.id.postContent);
        }
    }
}
