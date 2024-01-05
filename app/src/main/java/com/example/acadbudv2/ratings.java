package com.example.acadbudv2;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ratings extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_top_ratings);

        // Fetch top 5 students with highest ratings from Firebase
        fetchTopRatings();
    }

    private void fetchTopRatings() {
        DatabaseReference ratingsRef = FirebaseDatabase.getInstance().getReference("Ratings");

        ratingsRef.orderByValue().limitToLast(5).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Process the top 5 students with highest ratings
                List<rating_item> ratingItems = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String studentName = snapshot.getKey();
                    float rating = snapshot.getValue(Float.class);

                    rating_item ratingItem = new rating_item(studentName, rating);
                    ratingItems.add(ratingItem);
                }

                // Sort the list in descending order based on ratings
                Collections.sort(ratingItems, new Comparator<rating_item>() {
                    @Override
                    public int compare(rating_item item1, rating_item item2) {
                        return Float.compare(item2.getRating(), item1.getRating());
                    }
                });

                // Show the top 5 students in a pop-up dialog
                showTopRatingsDialog(ratingItems);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors if any
            }
        });
    }

    private void showTopRatingsDialog(List<rating_item> ratingItems) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_top_ratings, null);

        LinearLayout topRatingsLayout = dialogView.findViewById(R.id.top_ratings_layout);

        for (int i = 0; i < ratingItems.size(); i++) {
            View ratingView = inflater.inflate(R.layout.item_rating, null);

            TextView nameTextView = ratingView.findViewById(R.id.text_name1);
            TextView ratingTextView = ratingView.findViewById(R.id.text_rating1);

            rating_item ratingItem = ratingItems.get(i);

            nameTextView.setText(ratingItem.getStudentName());
            ratingTextView.setText(String.valueOf(ratingItem.getRating()));

            topRatingsLayout.addView(ratingView);
        }

        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
