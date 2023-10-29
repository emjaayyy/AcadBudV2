package com.example.acadbudv2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;

public class math_channel extends AppCompatActivity {
    private DatabaseReference mPostReference;
    private String userName;
    private TextView dateTextView;
    private SharedPreferences sharedPreferences;

    // Define a key for the user's name in shared preferences
    private static final String PREF_USER_NAME = "user_name";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.math_channel);

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
        mPostReference = FirebaseDatabase.getInstance().getReference("Channels/Math Channels/Post/lrn"); // Replace "lrn" with your specific database node

        dateTextView = findViewById(R.id.date_time_math_channel);
        TextView mathChannelTextView2 = findViewById(R.id.math_channel_tv_2);
        TextView mathChannelTextView1 = findViewById(R.id.math_channel_tv_1);

        // Create a listener to retrieve and handle the "lrn" value
        mPostReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                if (dataSnapshot.exists()) {
                    // Check if the data exists
                    HashMap<String, Object> dataMap = (HashMap<String, Object>) dataSnapshot.getValue();
                    if (dataMap != null) {
                        // Make sure the dataMap is not null

                        // Access the 'posts' field from the dataMap
                        String postText = (String) dataMap.get("posts");
                        String postUser = (String) dataMap.get("name"); // Get the user's name
                        String postDate = (String) dataMap.get("date");

                        // Set postText to math_channel_tv_2
                        mathChannelTextView2.setText(postText);

                        // Set postUser to math_channel_tv_1
                        mathChannelTextView1.setText(postUser);

                        // Display the postDate
                        dateTextView.setText(postDate);

                        // Handle the lrnValue as needed (e.g., store it, process it)
                    }
                }
            }

            // Implement other ChildEventListener methods as needed

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                // Handle changes to the "lrn" value if necessary
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                // Handle removal of the "lrn" value if necessary
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
                // Handle movement of the "lrn" value if necessary
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle database error if necessary
            }
        });

        // Add a button click listener or any other functionality as needed
        Button post = findViewById(R.id.math_channel_post_btn);
        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent write = new Intent(math_channel.this, post_math.class);
                startActivity(write);
            }
        });

        // Display the current date in Manila's timezone
        displayCurrentDateInManilaTimezone();
    }

    private void displayCurrentDateInManilaTimezone() {
        // Create a SimpleDateFormat with the desired format and timezone
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Manila"));

        // Get the current date and format it
        Date date = new Date();
        String formattedDate = sdf.format(date);

        // Display the formatted date in the dateTextView
        dateTextView.setText(formattedDate);
    }
}
