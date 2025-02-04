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
    private MaterialButton signInButton;
    private MaterialButton registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        try {
            // Initialize views
            signInButton = findViewById(R.id.signInButton);
            registerButton = findViewById(R.id.registerButton);

            // Set click listeners
            signInButton.setOnClickListener(v -> {
                try {
                    Log.d(TAG, "Launching LoginActivity");
                    Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
                    startActivity(intent);
                } catch (Exception e) {
                    Log.e(TAG, "Error launching LoginActivity", e);
                    showError("Error launching login screen: " + e.getMessage());
                }
            });

            registerButton.setOnClickListener(v -> {
                try {
                    Log.d(TAG, "Launching RegisterActivity");
                    Intent intent = new Intent(WelcomeActivity.this, RegisterActivity.class);
                    startActivity(intent);
                } catch (Exception e) {
                    Log.e(TAG, "Error launching RegisterActivity", e);
                    showError("Error launching register screen: " + e.getMessage());
                }
            });

        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate", e);
            showError("Error initializing screen: " + e.getMessage());
        }
    }

    private void showError(String message) {
        View rootView = findViewById(android.R.id.content);
        if (rootView != null) {
            Snackbar.make(rootView, message, Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        View decorView = getWindow().getDecorView();
        // Hide system UI
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }
}