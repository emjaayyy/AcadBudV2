package com.example.acadbudv2;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class channels extends AppCompatActivity {
    private RecyclerView recyclerView;
    private post_adapter postAdapter; // Use the correct class name for your adapter
    private List<post_content> posts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.channels);

        // Initialize RecyclerView and its adapter
        recyclerView = findViewById(R.id.recyclerView);
        postAdapter = new post_adapter(new ArrayList<>(), "", new ArrayList<>());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(postAdapter);


        // Initialize the list of posts for the specific channel (e.g., "Math" channel)
        posts = new ArrayList<>();

        // Retrieve and display posts for the specific channel
        retrieveAndDisplayPostsForChannel("Math"); // Replace "Math" with the channel name
    }

    private void retrieveAndDisplayPostsForChannel(String channelName) {
        // Assuming you have a database reference for each channel (e.g., "Math", "Science", etc.)
        DatabaseReference channelReference = FirebaseDatabase.getInstance().getReference("Channels/" + channelName + "/Post");

        // Create a listener to retrieve and handle posts for the channel
        channelReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                if (dataSnapshot.exists()) {
                    post_content post = dataSnapshot.getValue(post_content.class);
                    if (post != null) {
                        posts.add(post);
                        postAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

            // Implement other ChildEventListener methods as needed
        });
    }
}
