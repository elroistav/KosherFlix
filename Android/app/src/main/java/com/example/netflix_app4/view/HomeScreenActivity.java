package com.example.netflix_app4.view;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        // Initialize views
        movieTitle = findViewById(R.id.movieTitle);
        movieDescription = findViewById(R.id.movieDescription);
        playButton = findViewById(R.id.playButton);
        infoButton = findViewById(R.id.infoButton);

        // Fetch random movie data
        fetchRandomMovie();

        categoriesRecyclerView = findViewById(R.id.categoriesRecyclerView);
        categoriesRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        categoryAdapter = new CategoryAdapter(this, new ArrayList<>(), movie -> {
            // Show popup dialog when a movie is clicked
            showMoviePopup(movie);
        });
        categoriesRecyclerView.setAdapter(categoryAdapter);

        categoryViewModel = new ViewModelProvider(this).get(CategoryViewModel.class);
        observeViewModel();

        categoryViewModel.fetchCategories("679615afd6aeeebe1038f023");
//        Button fetchButton = findViewById(R.id.fetchButton);
//        fetchButton.setOnClickListener(v -> categoryViewModel.fetchCategories("679615afd6aeeebe1038f023"));
    }

    private void fetchRandomMovie() {
        // Get the Retrofit instance from RetrofitClient
        Retrofit retrofit = RetrofitClient.getRetrofitInstance();

        // Create the service
        MovieApiService movieService = retrofit.create(MovieApiService.class);

        // Step 1: Fetch categories
        movieService.getCategories("679615afd6aeeebe1038f023").enqueue(new Callback<CategoriesResponse>() {
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
                        movieService.getMovieById(randomMovieId, "679615afd6aeeebe1038f023").enqueue(new Callback<MovieModel>() {
                            @Override
                            public void onResponse(Call<MovieModel> call, Response<MovieModel> movieResponse) {
                                if (movieResponse.isSuccessful() && movieResponse.body() != null) {
                                    // Update UI with the random movie details
                                    updateMovieUI(movieResponse.body());
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

        playButton.setOnClickListener(v -> {
            // Navigate to movie playback screen (currently does nothing)
//            Intent intent = new Intent(HomeScreenActivity.this, MoviePlayerActivity.class);
//            intent.putExtra("videoUrl", movie.getVideoUrl());
//            startActivity(intent);
        });

        infoButton.setOnClickListener(v -> {
            // Navigate to movie details screen
            Intent intent = new Intent(HomeScreenActivity.this, MovieDetailsActivity.class);
            intent.putExtra("movieDetails", movie);
            startActivity(intent);
        });
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
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        // Bind views
        ImageView moviePoster = dialog.findViewById(R.id.moviePreview);
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

