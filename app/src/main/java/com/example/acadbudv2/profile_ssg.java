package com.example.acadbudv2;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class profile_ssg extends AppCompatActivity {

    TextView profile_name;
    FirebaseAuth auth;
    SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "MyPrefsFile"; // Name for your SharedPreferences file

    private RecyclerView postsRecyclerView;
    private post_adapter postAdapter;
    private DatabaseReference databaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_ssg);

        profile_name = findViewById(R.id.profile_name);

        Button logoutButton = findViewById(R.id.logout_btn);
        Button notif = findViewById(R.id.notif_btn_profile);
        Button meet = findViewById(R.id.meeting_btn_profile);
        Button edit = findViewById(R.id.edit_profile_btn);
        Button feed = findViewById(R.id.home_btn_profile);

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent edited = new Intent(profile_ssg.this, edit_profile.class);
                startActivity(edited);
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLogoutConfirmationDialog();
            }
        });

        notif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent bell = new Intent(profile_ssg.this, notif.class);
                startActivity(bell);
            }
        });

        meet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent meeting = new Intent(profile_ssg.this, meeting_ssg.class);
                startActivity(meeting);
            }
        });

        feed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent home = new Intent(profile_ssg.this, home_ssg.class);
                startActivity(home);
            }
        });

        auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();

        if (currentUser != null) {
            String uid = currentUser.getUid();

            // Access the user's name from SharedPreferences
            sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            String savedName = sharedPreferences.getString("userName", "");
            profile_name.setText(savedName);

            // Initialize the RecyclerView and adapter for posts
            postsRecyclerView = findViewById(R.id.profile_rv);
            postsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            postAdapter = new post_adapter(new ArrayList<>(), "", new ArrayList<>());
            postsRecyclerView.setAdapter(postAdapter);


            // Initialize the Firebase Realtime Database reference
            databaseRef = FirebaseDatabase.getInstance().getReference("Channels");

            // Fetch and display posts from all channels
            fetchPostsFromAllChannels(savedName);
        } else {
            // Handle the case where the user is not logged in
            Log.e("ProfileError", "User is not logged in");
        }
    }

    private void showLogoutConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Perform logout and redirect to login activity
                        logoutAndRedirectToLogin();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked "No," so dismiss the dialog
                        dialog.dismiss();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void logoutAndRedirectToLogin() {
        // Perform logout here
        // You can add your logout logic here, like signing out the user

        // Redirect to the login activity
        Intent intent = new Intent(profile_ssg.this, login_ssg.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish(); // Finish the current activity
    }

    private void fetchPostsFromAllChannels(String userName) {
        DatabaseReference channelsRef = FirebaseDatabase.getInstance().getReference("Channels");

        // Iterate through each channel
        channelsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot channelsSnapshot) {
                for (DataSnapshot channelSnapshot : channelsSnapshot.getChildren()) {
                    String channelName = channelSnapshot.getKey();

                    // Fetch posts for the user from the current channel
                    fetchPostsForUserInChannel(userName, channelName);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle the error
                Log.e("FirebaseError", "Error retrieving channels: " + error.getMessage());
            }
        });
    }

    private void fetchPostsForUserInChannel(String userName, String channelName) {
        DatabaseReference userChannelRef = FirebaseDatabase.getInstance()
                .getReference("Channels/" + channelName + "/" + userName);

        userChannelRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                if (dataSnapshot.exists()) {
                    post_content post = dataSnapshot.getValue(post_content.class);
                    if (post != null) {
                        Log.d("FirebaseDebug", "Post retrieved: " + post.getName() + ", " + post.getDate() + ", " + post.getPosts());
                        postAdapter.addPost(post);
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
            public void onCancelled(DatabaseError databaseError) {
                // Handle the error
                Log.e("FirebaseError", "Error retrieving posts: " + databaseError.getMessage());
            }
        });
    }
}
