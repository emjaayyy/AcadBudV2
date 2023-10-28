package com.example.acadbudv2;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class post extends AppCompatActivity {
    private DatabaseReference mPostReference;
    private int postCount = 0;

    private String userName; // Variable to store the user's name

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.post);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        // Initialize Firebase Database reference
        mPostReference = FirebaseDatabase.getInstance().getReference("Channels/Math Channels/Post/lrn"); // Replace "lrn" with your specific database node

        // Create a listener to retrieve and handle the "lrn" value
        mPostReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                if (dataSnapshot.exists()) {
                    // Check if the data exists
                    HashMap<String, Object> dataMap = (HashMap<String, Object>) dataSnapshot.getValue();
                    if (dataMap != null) {
                        // Make sure the dataMap is not null

                        // Access the 'lrn' value from the dataMap
                        String lrnValue = (String) dataMap.get("lrn");

                        // Handle the lrnValue as needed (e.g., store it, process it)
                    }
                }

                Button postButton = findViewById(R.id.post_btn);
                EditText postEditText = findViewById(R.id.post_et);

                // Retrieve the user's UID
                String uid = mAuth.getCurrentUser().getUid();

                // Retrieve the user's name from SharedPreferences
                userName = getUserNameFromSharedPreferences();

                postButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String postText = postEditText.getText().toString();
                        if (!postText.isEmpty()) {
                            // Create a new post_content object
                            post_content newPost = new post_content();

                            // Set the user's name from SharedPreferences
                            newPost.setName(userName);

                            newPost.setPosts(postText);

                            // Calculate the next post key
                            String postKey = "Post " + (postCount + 1);

                            // Save the new post to the Firebase database under the user's UID
                            mPostReference.push().setValue(newPost);

                            // Optionally, you can add a success message
                            Toast.makeText(post.this, "Post saved successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            // Handle empty post text
                            Toast.makeText(post.this, "Please enter a post", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
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

            // Method to retrieve the user's name from SharedPreferences
            private String getUserNameFromSharedPreferences() {
                // Replace "userName" with the key you used to store the user's name
                return getSharedPreferences("MyPrefsFile", Context.MODE_PRIVATE).getString("userName", "");
            }
        });
    }
}