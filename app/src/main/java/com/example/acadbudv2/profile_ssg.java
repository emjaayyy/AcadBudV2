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

public class profile_ssg extends AppCompatActivity {

    TextView profile_name;
    FirebaseAuth auth;
    SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "MyPrefsFile"; // Name for your SharedPreferences file

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_ssg);

        profile_name = findViewById(R.id.profile_name);

        Button logoutButton = findViewById(R.id.logout_btn);
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
        } else {
            // Handle the case where the user is not logged in
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
}
