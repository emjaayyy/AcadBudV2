package com.example.acadbudv2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class signup_user extends AppCompatActivity {

    private EditText lrnEditText, nameEditText, emailEditText, passwordEditText;
    private DatabaseReference databaseReference;
    private FirebaseAuth auth; // Firebase Authentication
    private CheckBox showPasswordCheckBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_user);

        lrnEditText = findViewById(R.id.Input_Lrn_signup_user);
        nameEditText = findViewById(R.id.Input_Name_user);
        emailEditText = findViewById(R.id.Input_Email_user);
        passwordEditText = findViewById(R.id.Input_Password_signup_user);

        Button signUpButton = findViewById(R.id.Signupbttn_user);
        Button loginButton = findViewById(R.id.Login_Btn_signup_user);

        databaseReference = FirebaseDatabase.getInstance().getReference("Students");
        auth = FirebaseAuth.getInstance(); // Initialize Firebase Authentication

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent login = new Intent(signup_user.this, login_user.class);
                startActivity(login);
            }
        });


        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String lrn = lrnEditText.getText().toString();
                String name = nameEditText.getText().toString();
                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                if (validateInfo(lrn, name, email, password)) {
                    // Create a user account using Firebase Authentication
                    auth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(signup_user.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        String hashedPassword = hashPassword(password);
                                        // User account creation is successful, you can proceed with saving user data in the Realtime Database
                                        student user = new student(lrn, name, email, hashedPassword);
                                        databaseReference.child(lrn).setValue(user);

                                        // Optional: You can sign in the user after successful registration
                                        auth.signInWithEmailAndPassword(email, password);

                                        // Send email verification
                                        FirebaseUser users = FirebaseAuth.getInstance().getCurrentUser();
                                        if (users != null) {
                                            users.sendEmailVerification()
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                Toast.makeText(signup_user.this, "Email verification sent.", Toast.LENGTH_SHORT).show();
                                                            } else {
                                                                Toast.makeText(signup_user.this, "Failed to send email verification: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    });
                                        }

                                        // Redirect to a different activity or perform any post_math-registration actions
                                    } else {
                                        // User account creation failed, show an error message
                                        Toast.makeText(signup_user.this, "User account creation failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                } else {
                    // Display a toast message indicating validation errors
                    Toast.makeText(signup_user.this, "Please check your input and ensure that it is correct.", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
    // Validation method
    private boolean validateInfo(String lrn, String name, String email, String password) {
        if (!lrn.matches("[0-9]{12}$")) {
            lrnEditText.requestFocus();
            lrnEditText.setError("It should be 12 numbers");
            return false;
        } else if (name.trim().length() < 1) {
            nameEditText.requestFocus();
            nameEditText.setError("Enter your full name");
            return false;
        } else if (!name.matches("[a-zA-Z ]+")) {
            nameEditText.requestFocus();
            nameEditText.setError("Enter only alphabetical characters");
            return false;
        } else if (email.length() == 0) {
            emailEditText.requestFocus();
            emailEditText.setError("Field cannot be empty");
            return false;
        } else if (!email.matches("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+")) {
            emailEditText.requestFocus();
            emailEditText.setError("Enter a valid Email");
            return false;
        } else if (password.length() < 8) {
            passwordEditText.requestFocus();
            passwordEditText.setError("Minimum 8 characters required");
            return false;
        } else {
            return true;
        }
    }
    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(password.getBytes());

            // Convert bytes to hexadecimal format
            StringBuilder sb = new StringBuilder();
            for (byte b : hashedBytes) {
                sb.append(String.format("%02x", b));
            }

            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "";
        }
    }
}

