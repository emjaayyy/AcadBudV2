package com.example.acadbudv2;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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

public class esp_channel extends AppCompatActivity {
    private DatabaseReference mPostReference;
    private String userName;
    private SharedPreferences sharedPreferences;
    private RecyclerView recyclerView;
    private post_adapter postAdapter;

    // Define a key for the user's name in shared preferences
    private static final String PREF_USER_NAME = "user_name";

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.esp_channel);

        // Initialize shared preferences
        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);

        // Retrieve the user's name from shared preferences
        userName = sharedPreferences.getString(PREF_USER_NAME, "");

        if (userName.isEmpty()) {
            // If the user's name is not found in shared preferences, you can handle it accordingly
            // For example, you can prompt the user to enter their name.

            // Here's an example of how you can save the user's name to shared preferences
            userName = "Default Name"; // Set a default name if the user hasn't provided one
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(PREF_USER_NAME, userName);
            editor.apply();
        }

        // Initialize Firebase Database reference
        mPostReference = FirebaseDatabase.getInstance().getReference("Channels/ESP/Posts/lrn"); // Replace "lrn" with your specific database node

        // Initialize RecyclerView and its adapter
        recyclerView = findViewById(R.id.recyclerView);
        postAdapter = new post_adapter(new ArrayList<>());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(postAdapter);

        // Create a listener to retrieve and handle posts
        mPostReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                if (dataSnapshot.exists()) {
                    post_content post = dataSnapshot.getValue(post_content.class);
                    if (post != null) {
                        postAdapter.addPost(post);
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

        // Add a button click listener or any other functionality as needed
        Button postButton = findViewById(R.id.esp_channel_post_btn);
        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent write = new Intent(esp_channel.this,post_esp.class);
                startActivity(write);
            }
        });
    }
}