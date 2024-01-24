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
        holder.editButton.setVisibility(post.getName().equals(currentUser) ? View.VISIBLE : View.GONE);
        holder.deleteButton.setVisibility(post.getName().equals(currentUser) ? View.VISIBLE : View.GONE);

        // Set onClickListeners for edit and delete buttons if needed
        holder.editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle edit button click
            }
        });

        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle delete button click
            }
        });
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
        Button editButton;
        Button deleteButton;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            contentTextView = itemView.findViewById(R.id.contentTextView);
            editButton = itemView.findViewById(R.id.edit_btn_post_adapter);
            deleteButton = itemView.findViewById(R.id.delete_btn_post_adapter);
        }

        public void bind(post_content post) {
            nameTextView.setText(post.getName());
            dateTextView.setText(post.getDate());
            contentTextView.setText(post.getPosts());

            if (post.isCurrentUser(currentUserId)) {
                editButton.setVisibility(View.VISIBLE);
                deleteButton.setVisibility(View.VISIBLE);

                // Set click listeners for edit and delete buttons
                editButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Handle edit button click
                        // Add your logic to open an edit activity or perform the edit action
                        // You can pass the post details to the edit activity if needed
                    }
                });

                deleteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Handle delete button click
                        // Add your logic to confirm the deletion and delete the post from the database
                    }
                });
            } else {
                editButton.setVisibility(View.GONE);
                deleteButton.setVisibility(View.GONE);
            }
        }
    }
}

