package com.example.acadbudv2;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class post_science extends AppCompatActivity {
    private DatabaseReference mPostReference;
    private String userName; // Variable to store the user's name

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.post);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        // Initialize Firebase Database reference
        mPostReference = FirebaseDatabase.getInstance().getReference("Channels/Science/Posts/lrn"); // Replace "lrn" with your specific database node

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

                    // Set the current date and time
                    SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
                    String currentDate = dateFormat.format(new Date());
                    newPost.setDate(currentDate);

                    // Calculate the next post_math key
                    mPostReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            long postCount = dataSnapshot.getChildrenCount();
                            String postKey = "Post " + (postCount + 1);

                            // Save the new post_math to the Firebase database under the user's UID
                            mPostReference.child(postKey).setValue(newPost);

                            // Optionally, you can add a success message
                            Toast.makeText(post_science.this, "Post saved successfully", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            // Handle the error
                        }
                    });
                } else {
                    // Handle empty post_math text
                    Toast.makeText(post_science.this, "Please enter a text", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Method to retrieve the user's name from SharedPreferences
    private String getUserNameFromSharedPreferences() {
        // Replace "userName" with the key you used to store the user's name
        return getSharedPreferences("MyPrefsFile", Context.MODE_PRIVATE).getString("userName", "");
    }
}
