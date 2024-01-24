package com.example.acadbudv2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class edit_profile_ssg extends AppCompatActivity {

    private EditText sectionEditText;
    private EditText yearEditText;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_profile_ssg);

        sectionEditText = findViewById(R.id.profile_section_edit_profile);
        yearEditText = findViewById(R.id.profile_year_edit_profile);

        Button updateProfileButton = findViewById(R.id.update_profile);
        updateProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfile();
            }
        });

        // Fetch and display the current user's "year" and "section" data
        fetchProfileDataStudent();
    }

    private void fetchProfileDataStudent() {
        // Retrieve the user's name from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefsFile", MODE_PRIVATE);
        String userName = sharedPreferences.getString("userName", "");

        // Retrieve "year" and "section" data for the specific user from Firebase
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("SSG Students");
        userRef.orderByChild("name").equalTo(userName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    // Get the "year" and "section" values
                    String userYear = userSnapshot.child("year").getValue(String.class);
                    String userSection = userSnapshot.child("section").getValue(String.class);

                    // Set the fetched values to the corresponding EditText fields
                    yearEditText.setText(userYear);
                    sectionEditText.setText(userSection);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(edit_profile_ssg.this, "Error fetching profile data: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateProfile() {
        String newSection = sectionEditText.getText().toString();
        String newYear = yearEditText.getText().toString();

        // Retrieve the user's name from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefsFile", MODE_PRIVATE);
        String userName = sharedPreferences.getString("userName", "");

        // Update the user's profile data in Firebase under their name
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("SSG Students");
        userRef.orderByChild("name").equalTo(userName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    // Update "year" and "section" for the specific user
                    userSnapshot.getRef().child("year").setValue(newYear);
                    userSnapshot.getRef().child("section").setValue(newSection);

                    // Save the updated values to SharedPreferences
                    editor = getSharedPreferences("MyPrefsFile", MODE_PRIVATE).edit();
                    editor.putString("userYear", newYear);
                    editor.putString("userSection", newSection);
                    editor.apply();

                    // Provide feedback to the user
                    Toast.makeText(edit_profile_ssg.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(edit_profile_ssg.this, "Error updating profile data: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        Intent resultIntent = new Intent();
        resultIntent.putExtra("updatedYear", newYear);
        resultIntent.putExtra("updatedSection", newSection);
        setResult(RESULT_OK, resultIntent);

        // Finish the activity
        finish();
    }
}

