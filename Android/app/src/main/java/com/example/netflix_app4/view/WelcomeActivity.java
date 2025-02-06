package com.example.netflix_app4.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import com.example.netflix_app4.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;

public class WelcomeActivity extends AppCompatActivity {
    private static final String TAG = "WelcomeActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        // Initialize buttons
        MaterialButton signInButton = findViewById(R.id.signInButton);
        MaterialButton registerButton = findViewById(R.id.registerButton);

        // Set click listeners
        signInButton.setOnClickListener(v -> {
            try {
                Log.d(TAG, "Starting LoginActivity");
                Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
                startActivity(intent);
            } catch (Exception e) {
                Log.e(TAG, "Error starting LoginActivity", e);
                showError("Error: " + e.getMessage());
            }
        });

        registerButton.setOnClickListener(v -> {
            try {
                Log.d(TAG, "Starting RegisterActivity");
                Intent intent = new Intent(WelcomeActivity.this, RegisterActivity.class);
                startActivity(intent);
            } catch (Exception e) {
                Log.e(TAG, "Error starting RegisterActivity", e);
                showError("Error: " + e.getMessage());
            }
        });
    }

    private void showError(String message) {
        View rootView = findViewById(android.R.id.content);
        Snackbar.make(rootView, message, Snackbar.LENGTH_LONG).show();
    }
}