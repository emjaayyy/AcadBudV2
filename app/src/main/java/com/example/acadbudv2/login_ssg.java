package com.example.acadbudv2;

import static com.example.acadbudv2.passwords.hashPassword;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class login_ssg extends AppCompatActivity {

    private EditText ssgPositionEditText, lrnEditText, passwordEditText;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_ssg);

        ssgPositionEditText = findViewById(R.id.Input_ssg_login);
        lrnEditText = findViewById(R.id.Input_Lrn_login_ssg);
        passwordEditText = findViewById(R.id.Input_Password_login_ssg);

        // Set the password input type to be hidden by default
        passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        Button loginButton = findViewById(R.id.Login_Btn_ssg);
        Button forgotPasswordButton = findViewById(R.id.ForgotPassword_ssg);
        auth = FirebaseAuth.getInstance();

        // ToggleButton for showing/hiding password
        ToggleButton showPasswordToggle = findViewById(R.id.showPasswordeye_ssg);
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
                String ssgPosition = ssgPositionEditText.getText().toString();
                String lrn = lrnEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                validateSSGPositionAndLRN(ssgPosition, lrn, password);
            }
        });
        forgotPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = lrnEditText.getText().toString();
                if (!TextUtils.isEmpty(email)) {
                    auth.sendPasswordResetEmail(email)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(login_ssg.this, "Password reset email sent.", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(login_ssg.this, "Failed to send password reset email: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                } else {
                    Toast.makeText(login_ssg.this, "Please enter your LRN to reset the password.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void validateSSGPositionAndLRN(String ssgPosition, String lrn, String password) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("SSG Students");
        databaseReference.child(lrn).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String savedSSGPosition = dataSnapshot.child("position").getValue(String.class);
                    String savedHashedPassword = dataSnapshot.child("password").getValue(String.class);

                    if (ssgPosition.equals(savedSSGPosition) && checkPassword(password, savedHashedPassword)) {
                        String userName = dataSnapshot.child("name").getValue(String.class);
                        storeUserInfoInSharedPreferences(userName);

                        Intent homeIntent = new Intent(login_ssg.this, home_ssg.class);
                        startActivity(homeIntent);
                    } else {
                        Toast.makeText(login_ssg.this, "Invalid SSG position or password.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(login_ssg.this, "User with this LRN does not exist.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(login_ssg.this, "Database Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean checkPassword(String enteredPassword, String savedHashedPassword) {
        String enteredHashedPassword = hashPassword(enteredPassword);
        return enteredHashedPassword.equals(savedHashedPassword);
    }


    private void storeUserInfoInSharedPreferences(String userName) {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefsFile", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("userName", userName);
        editor.apply();
    }
}
