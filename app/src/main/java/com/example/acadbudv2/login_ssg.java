package com.example.acadbudv2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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
                        // Valid SSG position and LRN
                        // You can store the user's name and other information in SharedPreferences
                        String userName = dataSnapshot.child("name").getValue(String.class);
                        storeUserInfoInSharedPreferences(userName);

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
                Toast.makeText(login_ssg.this, "Database Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Store the user's name in SharedPreferences
    private void storeUserInfoInSharedPreferences(String userName) {
        // You can use SharedPreferences to store user information like name
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefsFile", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("userName", userName);
        editor.apply();
    }
}
