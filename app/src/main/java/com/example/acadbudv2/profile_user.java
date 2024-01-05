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

public class profile_user extends AppCompatActivity {

    TextView profile_name;
    TextView profile_year_user;
    TextView profile_section_user;
    FirebaseAuth auth;
    SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "MyPrefsFile"; // Name for your SharedPreferences file

    private RecyclerView recyclerViewProfile;
    private post_adapter postAdapter;
    private DatabaseReference databaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_user);

        profile_name = findViewById(R.id.profile_name_user);
        profile_year_user = findViewById(R.id.profile_year_user);
        profile_section_user = findViewById(R.id.profile_section_user);

        Button logoutButton = findViewById(R.id.logout_btn_user);
        Button notif = findViewById(R.id.notif_btn_profile);
        Button meet = findViewById(R.id.meeting_btn_profile);
        Button edit = findViewById(R.id.edit_profile_btn_user);
        Button feed = findViewById(R.id.home_btn_profile);

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent edited = new Intent(profile_user.this, edit_profile.class);
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
                Intent bell = new Intent(profile_user.this, notif.class);
                startActivity(bell);
            }
        });

        meet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent meeting = new Intent(profile_user.this, meeting_user.class);
                startActivity(meeting);
            }
        });

        feed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent home = new Intent(profile_user.this, home_user.class);
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
            recyclerViewProfile = findViewById(R.id.recyclerViewProfile);
            recyclerViewProfile.setLayoutManager(new LinearLayoutManager(this));
            postAdapter = new post_adapter(new ArrayList<>());
            recyclerViewProfile.setAdapter(postAdapter);

            // Initialize the Firebase Realtime Database reference
            databaseRef = FirebaseDatabase.getInstance().getReference("Channels");

            // Fetch and display posts from all channels
            fetchPostsFromAllChannels(savedName);

            // Fetch and display profile information
            fetchProfileData(uid);
        } else {
            // Handle the case where the user is not logged in
            Log.e("ProfileError", "User is not logged in");
        }

    }


    private void fetchProfileData(String uid) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Students");
        userRef.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String userYear = dataSnapshot.child("year").getValue(String.class);
                    String userSection = dataSnapshot.child("section").getValue(String.class);

                    if (userYear != null && userSection != null) {
                        profile_year_user.setText("Year: " + userYear);
                        profile_section_user.setText("Section: " + userSection);

                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("userYear", userYear);
                        editor.putString("userSection", userSection);
                        editor.apply();
                    } else {
                        Log.e("ProfileUser", "Year or section is null");
                    }
                } else {
                    Log.e("ProfileUser", "DataSnapshot does not exist");
                }
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("ProfileUser", "Error in database operation: " + databaseError.getMessage());
            }
        });
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
        Intent intent = new Intent(profile_user.this, role.class);
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
