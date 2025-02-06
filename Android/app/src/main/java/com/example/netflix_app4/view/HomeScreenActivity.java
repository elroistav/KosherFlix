package com.example.netflix_app4.view;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;
import android.util.Log;
import com.example.netflix_app4.components.CustomNavbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.netflix_app4.R;
import com.example.netflix_app4.model.CategoriesResponse;
import com.example.netflix_app4.model.CategoryModel;
import com.example.netflix_app4.model.CategoryPromoted;
import com.example.netflix_app4.model.MovieModel;
import com.example.netflix_app4.model.UserInfo;
import com.example.netflix_app4.network.Config;
import com.example.netflix_app4.network.MovieApiService;
import com.example.netflix_app4.network.RetrofitClient;
import com.example.netflix_app4.repository.MovieRepository;
import com.example.netflix_app4.viewmodel.CategoryViewModel;
import com.example.netflix_app4.viewmodel.HomeScreenViewModel;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import java.util.Properties;


public class HomeScreenActivity extends AppCompatActivity {
    private static final String TAG = "HomeScreenActivity";

    private CustomNavbar customNavbar;
    private RecyclerView categoriesRecyclerView;
    private CategoryAdapter categoryAdapter;
    private HomeScreenViewModel viewModel;
    private CategoryViewModel categoryViewModel;

    private TextView movieTitle;
    private TextView movieDescription;
    private Button playButton;
    private Button infoButton;
    private FrameLayout moviePlayerWrapper;
    private LastWatchedAdapter lastWatchedAdapter;
    private MovieRepository movieRepository;
    private Button navbarToggleButton;
    private boolean isNavbarVisible = false;

    private UserInfo userInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        // Initialize repository
        movieRepository = MovieRepository.getInstance();

        // Initialize navbar
        customNavbar = findViewById(R.id.custom_navbar);
        customNavbar.setVisibility(View.GONE);  //
        navbarToggleButton = findViewById(R.id.navbarToggleButton);
        navbarToggleButton.setOnClickListener(v -> toggleNavbar());

        // יצירת ViewModel
        categoryViewModel = new ViewModelProvider(this,
                new ViewModelProvider.AndroidViewModelFactory(getApplication()))
                .get(CategoryViewModel.class);

        // אתחול ה-CustomNavbar
        customNavbar = findViewById(R.id.custom_navbar);
        customNavbar.initializeCategoryViewModel(categoryViewModel);


        String token = getIntent().getStringExtra("USER_TOKEN");
        if (token == null) {
            redirectToLogin();
            return;
        }

        // Initialize views - keeping your original initialization
        movieTitle = findViewById(R.id.movieTitle);
        movieDescription = findViewById(R.id.movieDescription);
        playButton = findViewById(R.id.playButton);
        infoButton = findViewById(R.id.infoButton);
        moviePlayerWrapper = findViewById(R.id.moviePlayerWrapper);

        categoriesRecyclerView = findViewById(R.id.categoriesRecyclerView);
        categoriesRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        RecyclerView lastWatchedRecyclerView = findViewById(R.id.lastWatchedRecyclerView);
        lastWatchedRecyclerView.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.HORIZONTAL, false));


        // Initialize ViewModels
        setupViewModels();

        // Observe ViewModels after setting them up
        observeViewModels();

        // Validate token and fetch data
        viewModel.validateToken(token);
    }

    private void setupViewModels() {
        viewModel = new ViewModelProvider(this).get(HomeScreenViewModel.class);
        categoryViewModel = new ViewModelProvider(this,
                new ViewModelProvider.AndroidViewModelFactory(getApplication()))
                .get(CategoryViewModel.class);
    }

    private void observeViewModels() {
        // Token validation
        viewModel.getUserInfo().observe(this, userInfo -> {
            if (userInfo != null) {
                this.userInfo = userInfo;  // Store userInfo
                Log.d(TAG, "User info received: " + userInfo.getUserId());

                // Update UI and fetch data
                updateUIWithUserInfo(userInfo);
                categoryViewModel.fetchCategories(userInfo.getUserId());
                categoryViewModel.fetchRandomMovie(this, userInfo.getUserId());

                // Set up adapters
                categoryAdapter = new CategoryAdapter(this, new ArrayList<>(), userInfo);
                lastWatchedAdapter = new LastWatchedAdapter(this, new ArrayList<>(), userInfo);

                // Set adapters to RecyclerViews
                categoriesRecyclerView.setAdapter(categoryAdapter);
                RecyclerView lastWatchedRecyclerView = findViewById(R.id.lastWatchedRecyclerView);
                lastWatchedRecyclerView.setAdapter(lastWatchedAdapter);

                // Add last watched observer
                categoryViewModel.getLastWatchedLiveData().observe(this, lastWatched -> {
                    if (lastWatched != null && lastWatched.getMovies() != null) {
                        Log.d(TAG, "Last watched movies received: " + lastWatched.getMovies().size());
                        List<MovieModel> movieDetailsList = new ArrayList<>();
                        for (String movieId : lastWatched.getMovies()) {
                            movieRepository.getMovieById(movieId, userInfo.getUserId(), new MovieRepository.MovieCallback() {
                                @Override
                                public void onSuccess(MovieModel movie) {
                                    movieDetailsList.add(movie);
                                    if (movieDetailsList.size() == lastWatched.getMovies().size()) {
                                        runOnUiThread(() -> lastWatchedAdapter.updateData(movieDetailsList));
                                    }
                                }

                                @Override
                                public void onError(String error) {
                                    Log.e(TAG, "Error fetching movie details: " + error);
                                }
                            });
                        }
                    }
                });

                if (customNavbar != null) {
                    customNavbar.setUserDetails(new UserInfo(
                            userInfo.getName(),
                            userInfo.getAvatar(),
                            userInfo.getUserId(),
                            userInfo.getToken(),
                            userInfo.isAdmin()
                    ));

                    //customNavbar.setupEventListeners();
                }
            }
        });

        // Original observers
        categoryViewModel.getPromotedCategoriesLiveData().observe(this, categories -> {
            if (categories != null) {
                categoryAdapter.updateData(categories);
            }
        });

        categoryViewModel.getRandomMovieLiveData().observe(this, movie -> {
            if (movie != null) {
                updateMovieUI(movie);

                infoButton.setOnClickListener(v -> {
                    Intent intent = new Intent(HomeScreenActivity.this, MovieDetailsActivity.class);
                    intent.putExtra("movieDetails", movie);
                    intent.putExtra("USER_INFO", userInfo);
                    startActivity(intent);
                });

                playButton.setOnClickListener(v -> handleWatchClick(movie));

            }
        });

        categoryViewModel.getErrorLiveData().observe(this, error -> {
            if (error != null) {
                Toast.makeText(this, "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });

        // Token validation error
        viewModel.getValidationError().observe(this, error -> {
            if (error != null) {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
                if (error.contains("Token validation failed")) {
                    redirectToLogin();
                }
            }
        });
    }

    private void handleWatchClick(MovieModel movie) {
        MovieApiService apiService = RetrofitClient.getRetrofitInstance().create(MovieApiService.class);
        apiService.recommendMovie(movie.getId(), userInfo.getUserId()).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                // Navigate to playback screen regardless of recommendation success
                Intent intent = new Intent(HomeScreenActivity.this, MoviePlaybackActivity.class);
                intent.putExtra("movieVideoUrl", movie.getVideoUrl());
                startActivity(intent);

                if (!response.isSuccessful()) {
                    Log.e(TAG, "Failed to recommend movie");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                // Navigate to playback screen even if recommendation fails
                Intent intent = new Intent(HomeScreenActivity.this, MoviePlaybackActivity.class);
                intent.putExtra("movieVideoUrl", movie.getVideoUrl());
                startActivity(intent);
                Log.e(TAG, "Network error recommending movie", t);
            }
        });
    }

    private void updateUIWithUserInfo(UserInfo userInfo) {
        TextView welcomeText = findViewById(R.id.welcomeText);
        welcomeText.setText(getString(R.string.welcome_message, userInfo.getName()));
        // Add any other UI updates based on user info
    }

    // Your existing methods remain unchanged
    private void updateMovieUI(MovieModel movie) {
        movieTitle.setText(movie.getTitle());
        movieDescription.setText(movie.getDescription());

        // Prepare VideoView
        Log.d("HomeScreenActivity", "Playing video: " + movie.getVideoUrl());
        moviePlayerWrapper.removeAllViews();
        VideoView videoView = new VideoView(this);
        moviePlayerWrapper.addView(videoView);

        Uri videoUri = Uri.parse(Config.getBaseUrl() + "/" + movie.getVideoUrl());
        Log.d("HomeScreenActivity", "Video URI: " + videoUri);
        videoView.setVideoURI(videoUri);
        if (videoUri != null) {
            Log.d("HomeScreenActivity", "Video URI is valid: " + videoUri.toString());
        } else {
            Log.e("HomeScreenActivity", "Invalid video URL.");
        }

        videoView.setOnInfoListener((mp, what, extra) -> {
            Log.d("HomeScreenActivity", "Info listener triggered. What: " + what + ", Extra: " + extra);
            switch (what) {
                case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                    Log.d("HomeScreenActivity", "Buffering started");
                    break;
                case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                    Log.d("HomeScreenActivity", "Buffering ended");
                    break;
                case MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START:
                    Log.d("HomeScreenActivity", "Video rendering started");
                    break;
                default:
                    Log.d("HomeScreenActivity", "Other info: " + what);
                    break;
            }
            return false;
        });

        videoView.setOnPreparedListener(mp -> {
            Log.d("HomeScreenActivity", "Video starting. Enjoy!");
            videoView.start();
            adjustVideoViewSize(videoView, mp);
        });

        videoView.setOnErrorListener((mp, what, extra) -> {
            Log.e("HomeScreenActivity", "Error playing video. What: " + what + ", Extra: " + extra);
            Toast.makeText(this, "Error playing video", Toast.LENGTH_SHORT).show();
            return true;
        });
    }

    private void adjustVideoViewSize(VideoView videoView, MediaPlayer mp) {
        int videoWidth = mp.getVideoWidth();
        int videoHeight = mp.getVideoHeight();
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) videoView.getLayoutParams();

        float aspectRatio = (float) videoWidth / videoHeight;
        int videoViewWidth = moviePlayerWrapper.getWidth();
        int videoViewHeight = (int) (videoViewWidth / aspectRatio);

        layoutParams.width = videoViewWidth;
        layoutParams.height = videoViewHeight;

        videoView.setLayoutParams(layoutParams);
    }

    private void showMoviePopup(MovieModel movie) {
        Intent intent = new Intent(this, MovieDetailsActivity.class);
        intent.putExtra("movieDetails", movie);
        intent.putExtra("USER_INFO", userInfo);
        startActivity(intent);
    }

    private void redirectToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void toggleNavbar() {
        isNavbarVisible = !isNavbarVisible;

        if (isNavbarVisible) {
            customNavbar.setVisibility(View.VISIBLE);
            customNavbar.animate()
                    .alpha(1f)
                    .setDuration(200)
                    .start();
        } else {
            customNavbar.animate()
                    .alpha(0f)
                    .setDuration(200)
                    .withEndAction(() ->
                            customNavbar.setVisibility(View.GONE))
                    .start();
        }
    }
}