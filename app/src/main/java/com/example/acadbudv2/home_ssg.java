package com.example.acadbudv2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class home_ssg extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_ssg);


        Button math = findViewById(R.id.ssg_math_channel);
        Button english = findViewById(R.id.ssg_english_channels);
        Button science = findViewById(R.id.ssg_science_channels);
        Button filipino = findViewById(R.id.ssg_filipino_channels);
        Button ap = findViewById(R.id.ssg_ap_channels);
        Button esp = findViewById(R.id.ssg_esp_channels);
        Button mapeh = findViewById(R.id.ssg_mapeh_channels);
        Button research = findViewById(R.id.ssg_research_channels);
        Button tle = findViewById(R.id.ssg_tle_channels);
        Button religion = findViewById(R.id.ssg_religion_channels);
        Button it = findViewById(R.id.ssg_innerthoughts_channels);
        Button meeting = findViewById(R.id.meeting_btn_home_ssg);
        Button notif = findViewById(R.id.notif_btn_home_ssg);
        Button profile = findViewById(R.id.profile_btn_home_ssg);


        math.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent s1 = new Intent(home_ssg.this, math_channel.class);
                startActivity(s1);
            }
        });

        english.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent s2 = new Intent(home_ssg.this, english_channel.class);
                startActivity(s2);
            }
        });

        science.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent s3 = new Intent(home_ssg.this, science_channel.class);
                startActivity(s3);
            }
        });

        filipino.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent s4 = new Intent(home_ssg.this, filipino_channel.class);
                startActivity(s4);
            }
        });

        ap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent s5 = new Intent(home_ssg.this, ap_channel.class);
                startActivity(s5);
            }
        });

        esp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent s6 = new Intent(home_ssg.this, esp_channel.class);
                startActivity(s6);
            }
        });

        mapeh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent s7 = new Intent(home_ssg.this, mapeh_channel.class);
                startActivity(s7);
            }
        });

        research.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent s8 = new Intent(home_ssg.this, research_channel.class);
                startActivity(s8);
            }
        });

        tle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent s9 = new Intent(home_ssg.this, tle_channel.class);
                startActivity(s9);
            }
        });

        religion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent s10 = new Intent(home_ssg.this, religion_channel.class);
                startActivity(s10);
            }
        });

        it.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent s11 = new Intent(home_ssg.this, innerthoughts_channel.class);
                startActivity(s11);
            }
        });

        meeting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent meet_user = new Intent(home_ssg.this, meeting_ssg.class);
                startActivity(meet_user);
            }
        });

        notif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent bell = new Intent(home_ssg.this, notif.class);
                startActivity(bell);
            }
        });

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent me = new Intent(home_ssg.this, profile_ssg.class);
                startActivity(me);
            }
        });
    }
}