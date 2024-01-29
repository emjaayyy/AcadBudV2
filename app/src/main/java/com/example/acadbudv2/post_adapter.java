package com.example.acadbudv2;

import android.icu.text.SimpleDateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class post_adapter extends RecyclerView.Adapter<post_adapter.PostViewHolder> {
    private List<post_content> posts;
    private String currentUserId;

    private String currentUser;
    private List<String> foulWords;

    public post_adapter(List<post_content> posts, String currentUserId, List<String> foulWords) {
        this.posts = posts;
        this.currentUserId = currentUserId;
        this.foulWords = foulWords;
        this.currentUser = currentUser;
        // Sort the posts initially
        sortPostsByDate();
    }

    public List<post_content> getPosts() {
        return posts;
    }

    public void sortPostsByDate() {
        // Sort posts based on the date
        Collections.sort(posts, new Comparator<post_content>() {
            @Override
            public int compare(post_content post1, post_content post2) {
                SimpleDateFormat sdf = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
                }
                try {
                    Date date1 = null;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                        date1 = sdf.parse(post1.getDate());
                    }
                    Date date2 = null;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                        date2 = sdf.parse(post2.getDate());
                    }

                    // Reverse the order for descending order (newest first)
                    return date2.compareTo(date1);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return 0;
            }
        });
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
        sortPostsByDate(); // Sort the posts after adding a new one
        notifyDataSetChanged(); // Notify the adapter that data has changed
    }

    private boolean containsFoulWords(String postContent) {
        for (String foulWord : foulWords) {
            if (postContent.toLowerCase().contains(foulWord.toLowerCase())) {
                return true; // Found a foul word
            }
        }
        return false; // No foul words found
    }

    public class PostViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        TextView dateTextView;
        TextView contentTextView;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            contentTextView = itemView.findViewById(R.id.contentTextView);
        }

        public void bind(post_content post) {
            nameTextView.setText(post.getName());
            dateTextView.setText(post.getDate());
            contentTextView.setText(post.getPosts());

        }
    }
}

