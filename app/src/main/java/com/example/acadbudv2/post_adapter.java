package com.example.acadbudv2;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class post_adapter extends RecyclerView.Adapter<post_adapter.PostViewHolder> {
    private List<post_content> posts;
    private boolean isSSGStudent; // Set this to true for SSG Students

    public post_adapter(List<post_content> posts, boolean isSSGStudent) {
        this.posts = posts;
        this.isSSGStudent = isSSGStudent;
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

    public class PostViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        TextView dateTextView;
        TextView contentTextView;
        Button postAddBtn;
        Button editBtn;
        Button deleteBtn;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            contentTextView = itemView.findViewById(R.id.contentTextView);
            postAddBtn = itemView.findViewById(R.id.postAddBtn);
            editBtn = itemView.findViewById(R.id.edit_btn_post_adapter);
            deleteBtn = itemView.findViewById(R.id.delete_btn_post_adapter);

            postAddBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Start the meeting_adapter activity when the button is clicked.
                    startMeetingListActivity(v.getContext());
                }
            });

            // Control the visibility of buttons based on the isSSGStudent value
            if (isSSGStudent) {
                postAddBtn.setVisibility(View.VISIBLE);
                editBtn.setVisibility(View.GONE);
                deleteBtn.setVisibility(View.GONE);
            } else {
                postAddBtn.setVisibility(View.GONE);
                editBtn.setVisibility(View.VISIBLE);
                deleteBtn.setVisibility(View.VISIBLE);
            }
        }

        public void bind(post_content post) {
            nameTextView.setText(post.getName());
            dateTextView.setText(post.getDate());
            contentTextView.setText(post.getPosts());
        }

        private void startMeetingListActivity(Context context) {
            // Create an Intent to start the meeting_adapter activity.
            Intent intent = new Intent(context, meeting_list.class);

            // You can also pass any data to the meeting_adapter activity if needed.
            // intent.putExtra("key", value);

            context.startActivity(intent);
        }
    }
}
