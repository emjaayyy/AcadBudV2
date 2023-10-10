package com.example.acadbudv2;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import java.util.List;

public class signup_user extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_user);

        List<users> userList = new database(this).getUsers();

        // Now you can work with the retrieved user data
        for (users user : userList) {
            String lrn = user.getLrn();
            String name = user.getName();
            String email = user.getEmail();
            String password = user.getPassword();

            // Do something with the user data (e.g., display it in your UI)
        }
    }
}
