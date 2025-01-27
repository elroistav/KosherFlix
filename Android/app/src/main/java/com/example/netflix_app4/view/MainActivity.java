package com.example.netflix_app4.view;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.netflix_app4.R;
import com.example.netflix_app4.model.MovieModel;
import com.example.netflix_app4.viewmodel.CategoryViewModel;
import com.example.netflix_app4.viewmodel.UserViewModel;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private RecyclerView categoriesRecyclerView;
    private CategoryAdapter categoryAdapter;
    private CategoryViewModel categoryViewModel;
    private UserViewModel userViewModel;
    private String userId;
    private String token;
    private String mongoUserId = "679615afd6aeeebe1038f023";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get user data from intent
        userId = getIntent().getStringExtra("userId");
        token = getIntent().getStringExtra("token");

        if (userId == null || token == null) {
            redirectToLogin();
            return;
        }

        initializeViews();
        setupViewModels();
        setupRecyclerView();
        observeViewModels();
    }

    private void initializeViews() {
        // Initialize RecyclerView
        categoriesRecyclerView = findViewById(R.id.categoriesRecyclerView);

        // Initialize Button
        Button fetchButton = findViewById(R.id.fetchButton);
        fetchButton.setOnClickListener(v -> categoryViewModel.fetchCategories(userId));

        // Initialize Toolbar
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Set up logout button in toolbar
        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_logout) {
                logout();
                return true;
            }
            return false;
        });
    }

    private void setupViewModels() {
        categoryViewModel = new ViewModelProvider(this).get(CategoryViewModel.class);
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
    }

    private void setupRecyclerView() {
        categoriesRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        categoryAdapter = new CategoryAdapter(this, new ArrayList<>(), movie -> {
            // Show popup dialog when a movie is clicked
            showMoviePopup(movie);
        });
        categoriesRecyclerView.setAdapter(categoryAdapter);

        categoryViewModel = new ViewModelProvider(this).get(CategoryViewModel.class);
        observeViewModels();

        Button fetchButton = findViewById(R.id.fetchButton);
        fetchButton.setOnClickListener(v -> categoryViewModel.fetchCategories(mongoUserId));
    }


    private void observeViewModels() {
        // Observe categories
        categoryViewModel.getPromotedCategoriesLiveData().observe(this, categories -> {
            if (categories != null) {
                categoryAdapter.updateData(categories);
            }
        });

        categoryViewModel.getLastWatchedLiveData().observe(this, lastWatched -> {
            if (lastWatched != null) {
                Log.d("MainActivity", "Last watched category: " + lastWatched.getCategory());
                Log.d("MainActivity", "Last watched movies: " + lastWatched.getMovies());
            }
        });

        categoryViewModel.getErrorLiveData().observe(this, error -> {
            if (error != null) {
                Toast.makeText(this, "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });

        // Observe user data
        userViewModel.getUser().observe(this, user -> {
            if (user == null || !user.isLoggedIn()) {
                redirectToLogin();
            }
        });
    }

    private void logout() {
        userViewModel.logout();
        redirectToLogin();
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
        Log.d("MainActivity", "Showing movie popup for: " + movie.getTitle());

        // Set data
        String thumbnailUrl = movie.getThumbnail();
        Log.d("MainActivity", "Movie thumbnail URL: " + thumbnailUrl);
        Log.d("MainActivity", "Movie Poster: " + moviePoster);

        movieTitle.setText(movie.getTitle());
        movieDescription.setText(movie.getDescription());

        // Handle button click
        watchButton.setOnClickListener(v -> {
            Toast.makeText(this, "Watch Movie feature coming soon!", Toast.LENGTH_SHORT).show();
        });

        dialog.show();
    }

    private void redirectToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh categories when activity resumes
        if (userId != null) {
            categoryViewModel.fetchCategories(userId);
        }
    }
}