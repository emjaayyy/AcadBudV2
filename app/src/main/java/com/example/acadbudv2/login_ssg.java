package com.example.acadbudv2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.auth.FirebaseAuth;

public class login_ssg extends AppCompatActivity {

    private EditText ssgPositionEditText, lrnEditText;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_ssg);

        ssgPositionEditText = findViewById(R.id.Input_ssg_login);
        lrnEditText = findViewById(R.id.Input_Lrn_login_ssg);

        Button loginButton = findViewById(R.id.Login_Btn_ssg);
        auth = FirebaseAuth.getInstance();

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String ssgPosition = ssgPositionEditText.getText().toString();
                String lrn = lrnEditText.getText().toString();

                // Add the code to validate SSG position and LRN here
                validateSSGPositionAndLRN(ssgPosition, lrn);
            }
        });
    }

    // Validate the SSG position and LRN
    private void validateSSGPositionAndLRN(String ssgPosition, String lrn) {
        // Retrieve the user's email from the Realtime Database using the LRN
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("SSG Students");
        databaseReference.child(lrn).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String savedSSGPosition = dataSnapshot.child("position").getValue(String.class);

                    // Check if the saved SSG position matches the input
                    if (ssgPosition.equals(savedSSGPosition)) {

                        Intent homeIntent = new Intent(login_ssg.this, home_ssg.class);
                        startActivity(homeIntent);
                    } else {
                        Toast.makeText(login_ssg.this, "Invalid SSG position or LRN.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(login_ssg.this, "User with this LRN does not exist.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle any database errors
            }
        });
    }
}
