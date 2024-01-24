package com.example.acadbudv2;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class post_mapeh extends AppCompatActivity {
    private DatabaseReference mPostReference;
    private String userName; // Variable to store the user's name
    private List<String> foulWords;
    private String channelIdentifier = "MAPEH"; // Change this to the desired channel identifier

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.post);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        // Initialize Firebase Database reference
        mPostReference = FirebaseDatabase.getInstance().getReference("Channels/" + channelIdentifier);

        Button postButton = findViewById(R.id.post_btn);
        EditText postEditText = findViewById(R.id.post_et);

        // Retrieve the user's name from SharedPreferences
        userName = getUserNameFromSharedPreferences();
        foulWords = Arrays.asList("obob", "bobo", "ulaga", "tanga", "anga", "tangina","taena", "potaena", "potangina", "putangina", "inaka", "pota", "atop", "hindutan", "hindut", "ogag",
                "gagi", "gago","gagu", "gaga", "tarantado", "tado", "tarantada", " puke ng ina mo", "kinanginamo", "kingina","Fota", "Kinangina", "Pu*ta", "Tang*ina", "Put*", "Put@", "Put@ngina", "Fu*ck", "Pak*yu",
                "P@kyu", "Bo*bo", "B*b*", "Obob", "Tanginam0", "T@nginamo", "p0t@", "iyot", "paeut", "paiyot", "hindot", "pahindot", "estuped", "stupid", "antanga", "katangahan", "k@t@ng@h@n", "Punyeta",
                "depota", "putapete", "shunga", "tarantado", "bobita", "bopols", "hindotka", "poka", "pukenginamo", "fuck","stupid","stupida","Nigga", "WTF", "watdapak", "bastard", "Son of a bitch", "jerk",
                "STFU", "dick head", "motherfucker", "dork", "ass","a$$","F*ck", "F*ck you", "F*u", "f*u", "fuck u", "f*ck u", "shit", "sh*t", "Shit", "Sh*t", "$hit", "stupid","$tupid","Bitch", "bastard", "bitch",
                "b*tch", "B*tch", "Damn", "damn", "d*mn", "D*mn", "Shit hole", "shit face"/* Add more foul words */);

        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String postText = postEditText.getText().toString();
                if (!postText.isEmpty()) {
                    // Check for foul words before allowing the post to be saved
                    if (containsFoulWords(postText)) {
                        // Handle foul words
                        Toast.makeText(post_mapeh.this, "Post contains foul words", Toast.LENGTH_SHORT).show();
                    } else {
                        post_content newPost = new post_content();
                        newPost.setName(userName);
                        newPost.setPosts(postText);

                        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
                        String currentDate = dateFormat.format(new Date());
                        newPost.setDate(currentDate);

                        mPostReference.child(userName).addListenerForSingleValueEvent(new ValueEventListener() {
                            public void onDataChange(com.google.firebase.database.DataSnapshot dataSnapshot) {
                                long postCount = dataSnapshot.getChildrenCount() + 1;
                                String postKey = "Post " + postCount;

                                mPostReference.child(userName).child(postKey).setValue(newPost, new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                        Toast.makeText(post_mapeh.this, "Post saved successfully", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                // Handle the error
                            }
                        });
                    }
                } else {
                    Toast.makeText(post_mapeh.this, "Please enter a text", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    // Method to retrieve the user's name from SharedPreferences
    private String getUserNameFromSharedPreferences() {
        // Replace "userName" with the key you used to store the user's name
        return getSharedPreferences("MyPrefsFile", Context.MODE_PRIVATE).getString("userName", "");
    }
    private boolean containsFoulWords(String postContent) {
        for (String foulWord : foulWords) {
            if (postContent.toLowerCase().contains(foulWord.toLowerCase())) {
                return true;
            }
        }
        return false;
    }
}



