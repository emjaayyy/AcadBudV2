package com.example.acadbudv2;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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

public class signup_ssg extends AppCompatActivity {

    private EditText positionSSG, lrnSSG, nameSSG, emailSSG, passwordSSG;
    private DatabaseReference databaseReference;
    private FirebaseAuth auth; // Firebase Authentication

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_ssg);

        positionSSG = findViewById(R.id.Input_ssg_signup);
        lrnSSG = findViewById(R.id.Input_Lrn_signup_ssg);
        nameSSG = findViewById(R.id.Input_Name_ssg);
        emailSSG = findViewById(R.id.Input_Email_ssg);
        passwordSSG = findViewById(R.id.Input_Password_signup_ssg);

        Button signUpButton = findViewById(R.id.Signupbttn_ssg);

        databaseReference = FirebaseDatabase.getInstance().getReference("SSG Students");
        auth = FirebaseAuth.getInstance();
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String position = positionSSG.getText().toString();
                String lrn = lrnSSG.getText().toString();
                String name = nameSSG.getText().toString();
                String email = emailSSG.getText().toString();
                String password = passwordSSG.getText().toString();

                if (validateInfo(position, lrn, name, email, password)) {
                    // Create a user account using Firebase Authentication
                    auth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(signup_ssg.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // User account creation is successful, you can proceed with saving user data in the Realtime Database
                                        ssg ssg = new ssg(position, lrn, name, email, password);
                                        databaseReference.child(lrn).setValue(ssg);

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
                                                                Toast.makeText(signup_ssg.this, "Email verification sent.", Toast.LENGTH_SHORT).show();
                                                            } else {
                                                                Toast.makeText(signup_ssg.this, "Failed to send email verification: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    });
                                        }

                                        // Redirect to a different activity or perform any post-registration actions
                                    } else {
                                        // User account creation failed, show an error message
                                        Toast.makeText(signup_ssg.this, "User account creation failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                } else {
                    // Display a toast message indicating validation errors
                    Toast.makeText(signup_ssg.this, "Please check your input and ensure that it is correct.", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
    // Validation method
    private boolean validateInfo(String position, String lrn, String name, String email, String password) {
        if (!lrn.matches("[0-9]{12}$")) {
            positionSSG.requestFocus();
            positionSSG.setError("It should be 12 numbers");
            return false;
        } else if (name.trim().length() < 1) {
            lrnSSG.requestFocus();
            lrnSSG.setError("It should be 12 numbers");
            return false;
        } else if (name.trim().length() < 1) {
            nameSSG.requestFocus();
            nameSSG.setError("Enter your full name");
            return false;
        } else if (!name.matches("[a-zA-Z ]+")) {
            nameSSG.requestFocus();
            nameSSG.setError("Enter only alphabetical characters");
            return false;
        } else if (email.length() == 0) {
            emailSSG.requestFocus();
            emailSSG.setError("Field cannot be empty");
            return false;
        } else if (!email.matches("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+")) {
            emailSSG.requestFocus();
            emailSSG.setError("Enter a valid Email");
            return false;
        } else if (password.length() < 8) {
            passwordSSG.requestFocus();
            passwordSSG.setError("Minimum 8 characters required");
            return false;
        } else {
            return true;
        }
    }
}
