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

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class profile_user extends AppCompatActivity {

    TextView profile_name;
    FirebaseAuth auth;
    SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "MyPrefsFile"; // Name for your SharedPreferences file
    private RecyclerView recyclerViewProfile;
    private post_adapter postAdapter;
    private List<post_content> userPosts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_user);

        profile_name = findViewById(R.id.profile_name_user);

        // Get the user's name from SharedPreferences
        sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String savedName = sharedPreferences.getString("userName", "");
        profile_name.setText(savedName);

        auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();

        if (currentUser != null) {
            String uid = currentUser.getUid();

            // Initialize RecyclerView and Adapter
            recyclerViewProfile = findViewById(R.id.recyclerViewProfile);
            userPosts = new ArrayList<>();
            postAdapter = new post_adapter(userPosts);
            recyclerViewProfile.setLayoutManager(new LinearLayoutManager(this));
            recyclerViewProfile.setAdapter(postAdapter);

            // Call the readData method with the UID
            readData(uid);
        }

        // The rest of your code...
        Button logoutButton = findViewById(R.id.logout_btn_user);
        Button notif = findViewById(R.id.notif_btn_profile);
        Button meet = findViewById(R.id.meeting_btn_profile);
        Button feed = findViewById(R.id.home_btn_profile);

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
                Intent feed = new Intent(profile_user.this, home_user.class);
            }
        });
    }

    private void readData(String userName) {
        DatabaseReference channelsReference = FirebaseDatabase.getInstance().getReference("Channels");

        channelsReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot channelsSnapshot) {
                userPosts.clear(); // Clear the list to avoid duplicates

                for (DataSnapshot channelSnapshot : channelsSnapshot.getChildren()) {
                    // Assuming each post has a "name" field
                    for (DataSnapshot postSnapshot : channelSnapshot.child("Posts").child("lrn").getChildren()) {
                        String postName = postSnapshot.child("name").getValue(String.class);

                        if (postName != null && postName.equalsIgnoreCase(userName)) {
                            post_content post = postSnapshot.getValue(post_content.class);
                            Log.d("ProfileUser", "Post: " + post.getName() + " - " + post.getPosts() + " - " + post.getDate());
                            userPosts.add(post);
                        }
                    }
                }

                postAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("ProfileUser", "Error in database operation: " + databaseError.getMessage());
                // Handle any errors that occur
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
        auth.signOut();

        // Redirect to the login activity
        Intent intent1 = new Intent(profile_user.this, login_user.class);
        intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent1);
        finish(); // Finish the current activity
    }
}
