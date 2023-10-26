package com.example.acadbudv2;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class math_channel extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.math_channel);


        Button post = findViewById(R.id.math_channel_post_btn);

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent write = new Intent(math_channel.this, post.class);
                startActivity(write);
            }
        });
    }
}