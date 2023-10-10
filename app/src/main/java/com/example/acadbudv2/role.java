package com.example.acadbudv2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class role extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.role);

        Button user = findViewById(R.id.student_role);
        user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent role_user = new Intent(role.this, signup_user.class);
                startActivity(role_user);
            }
        });

        Button ssg = findViewById(R.id.SSG_OFFICER);
        ssg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent role_ssg = new Intent(role.this, signup_ssg.class);
                startActivity(role_ssg);
            }
        });

    }
}