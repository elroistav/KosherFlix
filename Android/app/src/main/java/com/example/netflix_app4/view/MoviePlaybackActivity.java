package com.example.netflix_app4.view;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.netflix_app4.R;
import com.example.netflix_app4.network.Config;

public class MoviePlaybackActivity extends AppCompatActivity {

    private VideoView movieVideoView;
    private ImageButton backButton;
    private MediaController mediaController;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_playback);

        // Set to Landscape Mode
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        movieVideoView = findViewById(R.id.movieVideoView);
        backButton = findViewById(R.id.backButton);

        // Hide the system UI for full-screen mode
        getWindow().setFlags(android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN, android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();

        // Get the video URL from the Intent
        String videoUrl = getIntent().getStringExtra("movieVideoUrl");

        if (videoUrl != null) {
            Uri videoUri = Uri.parse(Config.getBaseUrl() + "/" + videoUrl);
            Log.d("MoviePlaybackActivity", "Playing video from URL: " + videoUri);
            movieVideoView.setVideoURI(videoUri);

            // Set up MediaController
            mediaController = new MediaController(this);
            mediaController.setAnchorView(movieVideoView);
            movieVideoView.setMediaController(mediaController);

            // Show the back button along with the media controller
            mediaController.setVisibility(View.VISIBLE);
            backButton.setVisibility(View.VISIBLE);

            // Handle the back button click
            backButton.setOnClickListener(v -> onBackPressed());

            // Start the video once it's prepared
            movieVideoView.setOnPreparedListener(mp -> {
                movieVideoView.start();
            });

            // Handle errors during playback
            movieVideoView.setOnErrorListener((mp, what, extra) -> {
                Toast.makeText(this, "Error playing video", Toast.LENGTH_SHORT).show();
                return true;
            });

            // Auto-hide controls after 3 seconds
            hideControlsAfterDelay();
        } else {
            Toast.makeText(this, "No video URL provided", Toast.LENGTH_SHORT).show();
        }
    }

    // Method to auto-hide controls (including back button)
    private void hideControlsAfterDelay() {
        // Create a Handler to remove the controls after a few seconds
        Handler handler = new Handler();
        Runnable hideControlsRunnable = new Runnable() {
            @Override
            public void run() {
                // Hide the media controller and back button after a delay
                if (mediaController != null) {
                    mediaController.hide();
                    backButton.setVisibility(View.GONE); // Hide the back button
                }
            }
        };

        // Delay of 3 seconds (3000 milliseconds)
        handler.postDelayed(hideControlsRunnable, 3000);

        // Handle touch events to show the controls again
        movieVideoView.setOnTouchListener((v, event) -> {
            // Show the media controller and back button
            mediaController.show();
            backButton.setVisibility(View.VISIBLE);

            // Cancel the auto-hide delay
            handler.removeCallbacks(hideControlsRunnable);

            // Start a new delay to hide the controls
            handler.postDelayed(hideControlsRunnable, 3000);
            return true;
        });
    }
}
