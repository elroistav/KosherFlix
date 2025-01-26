package com.example.netflix_app4.view;
import android.app.Dialog;
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

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private RecyclerView categoriesRecyclerView;
    private CategoryAdapter categoryAdapter;

    private CategoryViewModel categoryViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        categoriesRecyclerView = findViewById(R.id.categoriesRecyclerView);
        categoriesRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        categoryAdapter = new CategoryAdapter(this, new ArrayList<>(), movie -> {
            // Show popup dialog when a movie is clicked
            showMoviePopup(movie);
        });
        categoriesRecyclerView.setAdapter(categoryAdapter);

        categoryViewModel = new ViewModelProvider(this).get(CategoryViewModel.class);
        observeViewModel();

        Button fetchButton = findViewById(R.id.fetchButton);
        fetchButton.setOnClickListener(v -> categoryViewModel.fetchCategories("679615afd6aeeebe1038f023"));
    }


    private void observeViewModel() {
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

//        if (thumbnailUrl != null && !thumbnailUrl.isEmpty()) {
//            // Only load the image if the URL is not null or empty
//            Glide.with(this)
//                    .load(R.drawable.placeholder_image) // Your placeholder image here
//                    .into(moviePoster);
//        } else {
//            // Optionally, load a placeholder image if the thumbnail is null or empty
//            Glide.with(this)
//                    .load(R.drawable.placeholder_image) // Your placeholder image here
//                    .into(moviePoster);
//        }
        movieTitle.setText(movie.getTitle());
        movieDescription.setText(movie.getDescription());

        // Handle button click
        watchButton.setOnClickListener(v -> {
            Toast.makeText(this, "Watch Movie feature coming soon!", Toast.LENGTH_SHORT).show();
        });

        dialog.show();
    }
}

