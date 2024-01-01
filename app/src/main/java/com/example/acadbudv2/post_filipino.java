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
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class post_filipino extends AppCompatActivity {
    private DatabaseReference mPostReference;
    private String userName; // Variable to store the user's name

    private String channelIdentifier = "Filipino"; // Change this to the desired channel identifier

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.post);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        // Initialize Firebase Database reference
        mPostReference = FirebaseDatabase.getInstance().getReference("Channels/" + channelIdentifier);

        Button postButton = findViewById(R.id.post_btn);
        EditText postEditText = findViewById(R.id.post_et);

        // Retrieve the user's name from SharedPreferences
        userName = getUserNameFromSharedPreferences();

        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String postText = postEditText.getText().toString();
                if (!postText.isEmpty()) {
                    // Create a new post_content object
                    post_content newPost = new post_content();

                    // Set the user's name as the key
                    newPost.setName(userName);
                    newPost.setPosts(postText);

                    // Set the current date and time
                    SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
                    String currentDate = dateFormat.format(new Date());
                    newPost.setDate(currentDate);

                    // Save the new post to the Firebase database under the user's name
                    mPostReference.child(userName).addListenerForSingleValueEvent(new ValueEventListener() {

                        public void onDataChange(com.google.firebase.database.DataSnapshot dataSnapshot) {
                            long postCount = dataSnapshot.getChildrenCount() + 1; // Increment the post counter
                            String postKey = "Post " + postCount;

                            // Save the new post to the Firebase database under the user's name
                            mPostReference.child(userName).child(postKey).setValue(newPost, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                    // Optionally, you can add a success message
                                    Toast.makeText(post_filipino.this, "Post saved successfully", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            // Handle the error
                        }
                    });
                } else {
                    // Handle empty post text
                    Toast.makeText(post_filipino.this, "Please enter a text", Toast.LENGTH_SHORT).show();
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
