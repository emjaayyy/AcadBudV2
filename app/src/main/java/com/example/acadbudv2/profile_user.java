package com.example.acadbudv2;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class profile_user extends AppCompatActivity {

    TextView profile_name;
    FirebaseAuth auth;
    SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "MyPrefsFile"; // Name for your SharedPreferences file

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

            }
        });

    }


    private void readData(String uid) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Students").child(uid).child("name");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String name = dataSnapshot.getValue(String.class);

                    // Save the user's name in SharedPreferences
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("userName", name);
                    editor.apply();

                    profile_name.setText(name);
                } else {
                    // Handle the case where the user's data doesn't exist
                    // You can display an error message or take appropriate action.
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle any errors that occur
                // You can display an error message or take appropriate action.
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
