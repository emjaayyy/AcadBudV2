package com.example.acadbudv2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class edit_profile extends AppCompatActivity {

    private EditText sectionEditText;
    private EditText yearEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_profile);

        sectionEditText = findViewById(R.id.profile_section_edit_profile);
        yearEditText = findViewById(R.id.profile_year_edit_profile);

        Button updateProfileButton = findViewById(R.id.update_profile);
        updateProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfile();
            }
        });
    }

    private void updateProfile() {
        // Get the new data from the EditText fields
        String newSection = sectionEditText.getText().toString();
        String newYear = yearEditText.getText().toString();

        // Get the current user's UID (or LRN, if it's the user's LRN)
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid(); // Assuming UID is the LRN

        // Update the user's profile data in Firebase under their "lrn" node in "Students"
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Students").child(uid);
        userRef.child("section").setValue(newSection);
        userRef.child("year").setValue(newYear);

        // Once the data is updated, you can navigate back to the profile screen or perform another action.
        // For example, you can use an Intent to navigate back to the profile screen.
        Intent intent = new Intent(edit_profile.this, profile_ssg.class);
        startActivity(intent);
        finish(); // Finish the current activity
    }
}
