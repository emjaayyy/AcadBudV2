package com.example.acadbudv2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class login_user extends AppCompatActivity {

    private EditText lrnEditText, passwordEditText;
    private DatabaseReference databaseReference;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_user);

        lrnEditText = findViewById(R.id.Input_Lrn_login_user);
        passwordEditText = findViewById(R.id.Input_Password_login_user);

        Button loginButton = findViewById(R.id.Login_Btn_user);
        databaseReference = FirebaseDatabase.getInstance().getReference("Students");
        auth = FirebaseAuth.getInstance();
        // ToggleButton for showing/hiding password
        ToggleButton showPasswordToggle = findViewById(R.id.showPasswordeye_user);
        showPasswordToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Toggle the password visibility
                if (isChecked) {
                    passwordEditText.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                } else {
                    passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
            }
        });
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String lrn = lrnEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                // Retrieve the user's email and name from the Realtime Database using the LRN
                databaseReference.child(lrn).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            String email = dataSnapshot.child("email").getValue(String.class);
                            String name = dataSnapshot.child("name").getValue(String.class);

                            // Sign in with the retrieved email and the provided password
                            auth.signInWithEmailAndPassword(email, password)
                                    .addOnCompleteListener(login_user.this, new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if (task.isSuccessful()) {
                                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                                if (user != null && user.isEmailVerified()) {
                                                    // User is logged in and their email is verified

                                                    // Save the user's name in SharedPreferences
                                                    saveNameInSharedPreferences(name);

                                                    // Redirect to the home_user activity
                                                    Intent intent = new Intent(login_user.this, home_user.class);
                                                    startActivity(intent);
                                                    finish(); // Finish the current activity
                                                } else {
                                                    Toast.makeText(login_user.this, "Email not verified. Please check your email and verify your account.", Toast.LENGTH_SHORT).show();
                                                }
                                            } else {
                                                // Failed to log in
                                                Toast.makeText(login_user.this, "Login failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        } else {
                            Toast.makeText(login_user.this, "User with this LRN does not exist.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Handle any database errors
                    }
                });
            }
        });
    }

    // Add this method to save the user's name in SharedPreferences
    private void saveNameInSharedPreferences(String name) {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefsFile", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("userName", name);
        editor.apply();
    }
}
