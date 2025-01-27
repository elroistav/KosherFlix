package com.example.netflix_app4.view;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.netflix_app4.R;
import com.example.netflix_app4.model.CategoriesResponse;
import com.example.netflix_app4.model.CategoryModel;
import com.example.netflix_app4.model.CategoryPromoted;
import com.example.netflix_app4.model.MovieModel;
import com.example.netflix_app4.network.MovieApiService;
import com.example.netflix_app4.network.RetrofitClient;
import com.example.netflix_app4.viewmodel.CategoryViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class HomeScreenActivity extends AppCompatActivity {

    private RecyclerView categoriesRecyclerView;
    private CategoryAdapter categoryAdapter;

    private CategoryViewModel categoryViewModel;

    private TextView movieTitle;
    private TextView movieDescription;
    private Button playButton;
    private Button infoButton;
    private FrameLayout moviePlayerWrapper;
    private String userId = "679615afd6aeeebe1038f023";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        // Initialize views
        movieTitle = findViewById(R.id.movieTitle);
        movieDescription = findViewById(R.id.movieDescription);
        playButton = findViewById(R.id.playButton);
        infoButton = findViewById(R.id.infoButton);
        moviePlayerWrapper = findViewById(R.id.moviePlayerWrapper);

        // Fetch random movie data
        fetchRandomMovie();

        categoriesRecyclerView = findViewById(R.id.categoriesRecyclerView);
        categoriesRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Show popup dialog when a movie is clicked
        categoryAdapter = new CategoryAdapter(this, new ArrayList<>(), this::showMoviePopup);
        categoriesRecyclerView.setAdapter(categoryAdapter);

        categoryViewModel = new ViewModelProvider(this).get(CategoryViewModel.class);
        observeViewModel();

        categoryViewModel.fetchCategories(userId);
//        Button fetchButton = findViewById(R.id.fetchButton);
//        fetchButton.setOnClickListener(v -> categoryViewModel.fetchCategories(userId));
    }

    private void fetchRandomMovie() {
                        // Get the Retrofit instance from RetrofitClient
                        Retrofit retrofit = RetrofitClient.getRetrofitInstance();

                        // Create the service
                        MovieApiService movieService = retrofit.create(MovieApiService.class);

                        // Step 1: Fetch categories
                        movieService.getCategories(userId).enqueue(new Callback<CategoriesResponse>() {
                            @Override
                            public void onResponse(Call<CategoriesResponse> call, Response<CategoriesResponse> response) {
                                if (response.isSuccessful() && response.body() != null) {
                                    CategoriesResponse categoriesResponse = response.body();
                                    List<CategoryPromoted> promotedCategories = categoriesResponse.getPromotedCategories();

                                    // Step 2: Randomly select a movie ID
                    List<String> allMovieIds = new ArrayList<>();
                    for (CategoryPromoted category : promotedCategories) {
                        allMovieIds.addAll(category.getMovies()); // Assuming `getMovies()` returns a list of movie IDs
                    }

                    if (!allMovieIds.isEmpty()) {
                        String randomMovieId = allMovieIds.get(new Random().nextInt(allMovieIds.size()));

                        // Step 3: Fetch movie details for the randomly selected movie ID
                        movieService.getMovieById(randomMovieId, userId).enqueue(new Callback<MovieModel>() {
                            @Override
                            public void onResponse(Call<MovieModel> call, Response<MovieModel> movieResponse) {
                                if (movieResponse.isSuccessful() && movieResponse.body() != null) {
                                    // Overwrite the videoUrl with the URL of the sample.mp4 file
                                    MovieModel movie = movieResponse.body();
                                    movie.setVideoUrl("https://350d-132-70-66-11.ngrok-free.app/uploads/sample.mp4");
                                    Log.d("Video Url: ", movie.getVideoUrl());
                                    // Update UI with the random movie details
                                    updateMovieUI(movie);
                                } else {
                                    Toast.makeText(HomeScreenActivity.this, "Failed to fetch random movie details", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<MovieModel> call, Throwable t) {
                                Toast.makeText(HomeScreenActivity.this, "Error fetching movie details: " + t.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
                    } else {
                        Toast.makeText(HomeScreenActivity.this, "No movies found in promoted categories", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(HomeScreenActivity.this, "Failed to fetch categories", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CategoriesResponse> call, Throwable t) {
                Toast.makeText(HomeScreenActivity.this, "Error fetching categories: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void updateMovieUI(MovieModel movie) {
        movieTitle.setText(movie.getTitle());
        movieDescription.setText(movie.getDescription());

        // Get the container where the VideoView will go
        FrameLayout moviePlayerWrapper = findViewById(R.id.moviePlayerWrapper);

        // Remove any existing views from the FrameLayout
        moviePlayerWrapper.removeAllViews();

        // Create a VideoView and set it as the child of moviePlayerWrapper
        VideoView videoView = new VideoView(this);
        moviePlayerWrapper.addView(videoView);

        // Set the video URI and the MediaController
        Uri videoUri = Uri.parse(movie.getVideoUrl());
        videoView.setVideoURI(videoUri);

        // Start the video when it's ready
        videoView.setOnPreparedListener(mp -> {
            videoView.start();
            adjustVideoViewSize(videoView, mp);
        });

        // Handle video playback errors
        videoView.setOnErrorListener((mp, what, extra) -> {
            Toast.makeText(this, "Error playing video", Toast.LENGTH_SHORT).show();
            return true;
        });
    }

    private void adjustVideoViewSize(VideoView videoView, MediaPlayer mp) {
        // Get the video dimensions (width and height)
        int videoWidth = mp.getVideoWidth();
        int videoHeight = mp.getVideoHeight();

        // Get the parent container (FrameLayout) dimensions
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) videoView.getLayoutParams();

        // Calculate aspect ratio based on video dimensions
        float aspectRatio = (float) videoWidth / videoHeight;

        // Get the width of the parent container (moviePlayerWrapper)
        int videoViewWidth = moviePlayerWrapper.getWidth();
        int videoViewHeight = (int) (videoViewWidth / aspectRatio);

        // Set the width and height for the VideoView based on the aspect ratio
        layoutParams.width = videoViewWidth;
        layoutParams.height = videoViewHeight;

        videoView.setLayoutParams(layoutParams);
    }



    private void observeViewModel() {
        categoryViewModel.getPromotedCategoriesLiveData().observe(this, categories -> {
            if (categories != null) {
                categoryAdapter.updateData(categories);
            }
        });

        categoryViewModel.getLastWatchedLiveData().observe(this, lastWatched -> {
            if (lastWatched != null) {
                Log.d("HomeScreenActivity", "Last watched category: " + lastWatched.getCategory());
                Log.d("HomeScreenActivity", "Last watched movies: " + lastWatched.getMovies());
            }
        });

        categoryViewModel.getErrorLiveData().observe(this, error -> {
            if (error != null) {
                Toast.makeText(this, "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void showMoviePopup(MovieModel movie) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.movie_details);
        Objects.requireNonNull(dialog.getWindow()).setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        // Bind views
        TextView movieTitle = dialog.findViewById(R.id.movieTitle);
        TextView movieDescription = dialog.findViewById(R.id.movieDescription);
        Button watchButton = dialog.findViewById(R.id.watchButton);
        Log.d("HomeScreenActivity", "Showing movie popup for: " + movie.getTitle());

        // Set data
        movieTitle.setText(movie.getTitle());
        movieDescription.setText(movie.getDescription());

        // Handle button click
        watchButton.setOnClickListener(v -> {
            Toast.makeText(this, "Watch Movie feature coming soon!", Toast.LENGTH_SHORT).show();
        });

        dialog.show();
    }
}

