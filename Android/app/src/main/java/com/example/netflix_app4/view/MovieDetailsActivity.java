package com.example.netflix_app4.view;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.IOException;
import java.util.Properties;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.netflix_app4.R;
import com.example.netflix_app4.model.CategoryModel;
import com.example.netflix_app4.model.MovieModel;
import com.example.netflix_app4.model.UserInfo;
import com.example.netflix_app4.network.Config;
import com.example.netflix_app4.network.MovieApiService;
import com.example.netflix_app4.network.RetrofitClient;

import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieDetailsActivity extends AppCompatActivity {

    private ImageView moviePreview;
    private TextView movieTitle, movieDescription;
    private GridLayout movieInfoGrid;
    private ImageButton backButton;
    private Button watchButton;
    private RecyclerView recommendationRecyclerView;
    private RecommendationAdapter recommendationAdapter;
    private TextView recommendationTitle;
    private VideoView videoView;

    //private String userId = "67991fc041ad471db335232f";
    private String userId;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_details);

        UserInfo userInfo = (UserInfo) getIntent().getSerializableExtra("USER_INFO");
        if (userInfo != null) {
            userId = userInfo.getUserId();
        } else {
            Toast.makeText(this, "User information is missing", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        hideSystemUI();
        videoView = findViewById(R.id.videoView);

        // Initialize UI elements
        movieTitle = findViewById(R.id.movieTitle);
        movieDescription = findViewById(R.id.movieDescription);
        movieInfoGrid = findViewById(R.id.movieInfoGrid);
        watchButton = findViewById(R.id.watchButton);
        backButton = findViewById(R.id.backButton);
        recommendationRecyclerView = findViewById(R.id.recommendationRecyclerView);

        // Retrieve the movie details from Intent
        MovieModel movie = getIntent().getParcelableExtra("movieDetails");
        //movie.setVideoUrl(Config.getBaseUrl() + "/uploads/sample.mp4");
        if (movie != null) {
            Log.d("MovieDetailsActivity", "Movie details: " + movie);
            if (movie.getVideoUrl() != null) {
                Log.d("MovieDetailsActivity", "Playing video: " + movie.getVideoUrl());
                playVideo(movie.getVideoUrl());
            } else {
                Toast.makeText(this, "No video available", Toast.LENGTH_SHORT).show();
            }
            setupMovieDetails(movie);
            fetchRecommendations(movie.getId());
        }
        Log.d("MovieDetailsActivity", "Movie details: " + movie);

        // Back button action
        backButton.setOnClickListener(v -> finish());
        watchButton.setOnClickListener(v -> {
            if (movie != null) {
                handleWatchClick(movie);
            }
        });
        recommendationTitle = findViewById(R.id.recommendationTitle);
        recommendationTitle.setVisibility(View.GONE); // Initially hidden
    }

    private void handleWatchClick(MovieModel movie) {
        MovieApiService apiService = RetrofitClient.getRetrofitInstance().create(MovieApiService.class);
        apiService.recommendMovie(movie.getId(), userId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d("MovieDetailsActivity", "Successfully recommended movie");
                } else {
                    Log.e("MovieDetailsActivity", "Failed to recommend movie: " + response.code());
                }

                Intent intent = new Intent(MovieDetailsActivity.this, MoviePlaybackActivity.class);
                intent.putExtra("movieVideoUrl", movie.getVideoUrl());
                startActivity(intent);
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                Log.e("MovieDetailsActivity", "Network error recommending movie", t);

                Intent intent = new Intent(MovieDetailsActivity.this, MoviePlaybackActivity.class);
                intent.putExtra("movieVideoUrl", movie.getVideoUrl());
                startActivity(intent);
            }
        });
    }

    private void playVideo(String videoUrl) {
        // Set up the MediaController for playback controls
//        MediaController mediaController = new MediaController(this);
//        mediaController.setAnchorView(videoView);
        videoView.setZOrderOnTop(true);  // Ensure the VideoView is rendered above other views

        // Set the video URI and the MediaController
        Uri videoUri = Uri.parse(Config.getBaseUrl() + "/" + videoUrl);
        videoView.setVideoURI(videoUri);
//        videoView.setMediaController(mediaController);
        videoView.setFocusable(true);
        videoView.requestFocus();

        // Start the video when ready
        videoView.setOnPreparedListener(mp -> videoView.start());

        // Handle video playback errors
        videoView.setOnErrorListener((mp, what, extra) -> {
            Toast.makeText(this, "Error playing video", Toast.LENGTH_SHORT).show();
            return true;
        });
    }

    // Function to hide the navigation bar and status bar
    private void hideSystemUI() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        );
    }

    private void setupMovieDetails(MovieModel movie) {
        // Set the movie title and description
        movieTitle.setText(movie.getTitle());
        movieDescription.setText(movie.getDescription());

        // Clear previous info from grid
        movieInfoGrid.removeAllViews();

        // Basic Information
        if (movie.getRating() > 0) {
            addInfoToGrid("Rating", String.format(Locale.getDefault(), "%.1f/10", movie.getRating()));
        }

        addInfoToGrid("Length", movie.getLength() + " minutes");

        if (movie.getDirector() != null && !movie.getDirector().isEmpty()) {
            addInfoToGrid("Director", movie.getDirector());
        }

        if (movie.getLanguage() != null && !movie.getLanguage().isEmpty()) {
            addInfoToGrid("Language", movie.getLanguage());
        }

        // Categories
        if (movie.getCategories() != null && !movie.getCategories().isEmpty()) {
            StringBuilder categories = new StringBuilder();
            for (int i = 0; i < movie.getCategories().size(); i++) {
                if (i > 0) categories.append(", ");
                categories.append(movie.getCategories().get(i).getName());
            }
            addInfoToGrid("Categories", categories.toString());
        }

        // Release Date
        if (movie.getReleaseDate() != null && !movie.getReleaseDate().isEmpty()) {
            addInfoToGrid("Release Date", movie.getReleaseDate());
        }

        // Video playback
        if (movie.getVideoUrl() != null && !movie.getVideoUrl().isEmpty()) {
            playVideo(movie.getVideoUrl());
        } else {
            videoView.setVisibility(View.GONE);
        }
    }
    private void fetchRecommendations(String movieId) {
        recommendationAdapter = new RecommendationAdapter(this);
        recommendationRecyclerView.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.HORIZONTAL, false));
        recommendationRecyclerView.setAdapter(recommendationAdapter);

        // Make a GET request
        MovieApiService apiService = RetrofitClient.getRetrofitInstance().create(MovieApiService.class);
        Call<List<String>> call = apiService.getRecommendations(movieId, userId);

        call.enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<String> recommendedIds = response.body();
                    Log.d("Recommendations", "Fetched: " + recommendedIds);
                    recommendationTitle.setVisibility(View.VISIBLE); // Show the title
                    fetchRecommendedMovies(recommendedIds);
                } else {
                    recommendationTitle.setVisibility(View.GONE); // Hide the title if no recommendations
                }
            }

            @Override
            public void onFailure(Call<List<String>> call, Throwable t) {
                recommendationTitle.setVisibility(View.GONE); // Hide the title on failure
                Log.e("Recommendations", "Failed to fetch recommendations: " + t.getMessage());
            }
        });
    }


    private void fetchRecommendedMovies(List<String> movieIds) {
        MovieApiService apiService = RetrofitClient.getRetrofitInstance().create(MovieApiService.class);
        for (String movieId : movieIds) {
            apiService.getMovieById(movieId, userId).enqueue(new Callback<MovieModel>() {
                @Override
                public void onResponse(@NonNull Call<MovieModel> call, @NonNull Response<MovieModel> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        recommendationAdapter.addMovie(response.body());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<MovieModel> call, Throwable t) {
                    Log.e("Recommendations", "Failed to fetch movie details: " + t.getMessage());
                }
            });
        }
    }

    private void addInfoToGrid(String label, String value) {
        if (value == null || value.trim().isEmpty()) {
            Log.d("addInfoToGrid", "Skipped adding: " + label + " because value is empty or null.");
            return;
        }

        LinearLayout infoCard = new LinearLayout(this);
        infoCard.setOrientation(LinearLayout.VERTICAL);
        infoCard.setBackgroundResource(R.drawable.card_background);
        infoCard.setPadding(24, 20, 24, 20); // Increased padding

        // Label TextView
        TextView labelView = new TextView(this);
        labelView.setText(label.toUpperCase());
        labelView.setTextColor(Color.GRAY);
        labelView.setTextSize(12);
        labelView.setTypeface(null, Typeface.BOLD);
        labelView.setGravity(Gravity.CENTER);
        labelView.setAlpha(0.8f); // Slightly dimmed label

        // Value TextView with improved styling
        TextView valueView = new TextView(this);
        valueView.setText(value);
        valueView.setTextColor(Color.WHITE);
        valueView.setTextSize(16);
        valueView.setGravity(Gravity.CENTER);
        valueView.setLineSpacing(0, 1.2f); // Add line spacing for better readability
        valueView.setPadding(0, 8, 0, 0); // Add space between label and value

        infoCard.addView(labelView);
        infoCard.addView(valueView);

        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.setMargins(8, 8, 8, 8);
        params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
        params.rowSpec = GridLayout.spec(GridLayout.UNDEFINED);
        params.setGravity(Gravity.FILL_HORIZONTAL);
        infoCard.setLayoutParams(params);

        movieInfoGrid.addView(infoCard);
    }
}


