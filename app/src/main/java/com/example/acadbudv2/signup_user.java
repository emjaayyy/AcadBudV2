package com.example.acadbudv2;

import android.util.Log; // Import Log class for debugging
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class signup_user extends AppCompatActivity {

    private EditText lrnEditText;
    private EditText nameEditText;
    private EditText emailEditText;
    private EditText passwordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_user);

        lrnEditText = findViewById(R.id.Input_Lrn_signup_user);
        nameEditText = findViewById(R.id.Input_Name_user);
        emailEditText = findViewById(R.id.Input_Email_user);
        passwordEditText = findViewById(R.id.Input_Password_signup_user);

        Button signUpButton = findViewById(R.id.Signupbttn_user);

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String lrn = lrnEditText.getText().toString();
                String name = nameEditText.getText().toString();
                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                // Send the data to your PHP script for registration
                registerUser(lrn, name, email, password);
            }
        });
    }

    private void registerUser(String lrn, String name, String email, String password) {
        AsyncTask.execute(() -> {
            try {
                OkHttpClient client = new OkHttpClient();
                MediaType JSON = MediaType.parse("application/json; charset=utf-8");

                // Define the URL of your PHP script for registration
                String phpScriptUrl = "http://localhost/www/register.php"; // Replace with your PHP script URL

                // Create a JSON object with the registration data
                JSONObject json = new JSONObject();
                json.put("lrn", lrn);
                json.put("name", name);
                json.put("email", email);
                json.put("password", password);

                // Log the data being sent for debugging
                Log.d("Registration", "Sending data: " + json.toString());

                RequestBody requestBody = RequestBody.create(JSON, json.toString());

                Request request = new Request.Builder()
                        .url(phpScriptUrl)
                        .post(requestBody)
                        .build();

                Response response = client.newCall(request).execute();

                if (response.isSuccessful()) {
                    String responseData = response.body().string();

                    // Log the response for debugging
                    Log.d("Registration", "Response: " + responseData);

                    // Handle the response from your PHP script
                    if (responseData.equals("{\"success\": true}")) {
                        runOnUiThread(() -> {
                            Intent intent = new Intent(signup_user.this, login_user.class);
                            startActivity(intent);
                        });
                    } else {
                        runOnUiThread(() -> {
                            Toast.makeText(this, "Registration failed.", Toast.LENGTH_SHORT).show();
                        });
                    }
                } else {
                    runOnUiThread(() -> {
                        Toast.makeText(this, "Registration failed.", Toast.LENGTH_SHORT).show();
                    });
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        });
    }
}
