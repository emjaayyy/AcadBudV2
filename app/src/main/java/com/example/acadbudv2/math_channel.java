package com.example.acadbudv2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class math_channel extends AppCompatActivity {
    private DatabaseReference mPostReference;
    private String userName;
    private SharedPreferences sharedPreferences;
    private RecyclerView recyclerView;
    private post_adapter postAdapter;

    private static final String PREF_USER_NAME = "user_name";
    private String channelIdentifier = "Math";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.math_channel);

        Button meeting = findViewById(R.id.meeting_btn_math);
        Button notif = findViewById(R.id.notif_btn_math);
        Button profile = findViewById(R.id.profile_btn_math);
        Button home = findViewById(R.id.home_btn_english);

        meeting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent meet_user = new Intent(math_channel.this, meeting_user.class);
                startActivity(meet_user);
            }
        });

        notif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent bell = new Intent(math_channel.this, notif.class);
                startActivity(bell);
            }
        });

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent me = new Intent(math_channel.this, profile_ssg.class);
                startActivity(me);
            }
        });

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent back = new Intent(math_channel.this, home_user.class);
                startActivity(back);
            }
        });



        // Initialize RecyclerView and its adapter
        recyclerView = findViewById(R.id.recyclerView_math);
        postAdapter = new post_adapter(new ArrayList<>());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(postAdapter);

        // Create a listener to retrieve and handle posts
        mPostReference = FirebaseDatabase.getInstance().getReference("Channels");
        mPostReference.child(channelIdentifier).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot studentSnapshot, String previousChildName) {
                // This dataSnapshot refers to each student
                for (DataSnapshot postSnapshot : studentSnapshot.getChildren()) {
                    // This dataSnapshot refers to each post under a student
                    post_content post = postSnapshot.getValue(post_content.class);
                    if (post != null) {
                        postAdapter.addPost(post);
                        // Log information about the retrieved post
                        Log.d("PostDebug", "Post ID: " + postSnapshot.getKey() + ", User: " + post.getName() + ", Text: " + post.getPosts());
                    }
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                // Handle post changes if needed
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                // Handle post removal if needed
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                // Handle post movement if needed
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle database error if needed
                Log.e("FirebaseError", "Error retrieving posts: " + error.getMessage());
            }
        });

        Button postButton = findViewById(R.id.math_channel_post_btn);
        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent write = new Intent(math_channel.this, post_math.class);
                startActivity(write);
            }

        });
    }
}

