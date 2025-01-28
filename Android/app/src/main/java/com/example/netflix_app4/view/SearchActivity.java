package com.example.netflix_app4.view;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.netflix_app4.R;
import com.example.netflix_app4.model.MovieModel;
import com.example.netflix_app4.viewmodel.SearchViewModel;

public class SearchActivity extends AppCompatActivity {
    private SearchView searchView;
    private SearchResultsAdapter searchResultsAdapter;
    private SearchViewModel searchViewModel;
    private final String userId = "6796929afb50fce3a07283b3"; // Use your actual user ID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // Initialize views
        searchView = findViewById(R.id.searchView);
        RecyclerView searchResultsRecyclerView = findViewById(R.id.searchResultsRecyclerView);

        // Setup RecyclerView
        searchResultsAdapter = new SearchResultsAdapter(this, movie -> {
            // Show movie popup when clicked
            showMoviePopup(movie);
        });
        searchResultsRecyclerView.setAdapter(searchResultsAdapter);
        searchResultsRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        // Setup ViewModel
        searchViewModel = new ViewModelProvider(this).get(SearchViewModel.class);
        observeViewModel();

        // Setup SearchView
        setupSearchView();
    }

    private void setupSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (query != null && !query.isEmpty()) {
                    searchViewModel.searchMovies(query, userId);
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    private void observeViewModel() {
        searchViewModel.getSearchResults().observe(this, movies -> {
            if (movies != null) {
                searchResultsAdapter.updateData(movies);
            }
        });

        searchViewModel.getError().observe(this, error -> {
            if (error != null) {
                Toast.makeText(this, "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showMoviePopup(MovieModel movie) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.movie_details);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        // Bind views
        ImageView moviePoster = dialog.findViewById(R.id.moviePreview);
        TextView movieTitle = dialog.findViewById(R.id.movieTitle);
        TextView movieDescription = dialog.findViewById(R.id.movieDescription);
        Button watchButton = dialog.findViewById(R.id.watchButton);

        // Set data
        movieTitle.setText(movie.getTitle());
        movieDescription.setText(movie.getDescription());

        // Load thumbnail
        Glide.with(this)
                .load(movie.getThumbnail())
                .into(moviePoster);

        watchButton.setOnClickListener(v -> {
            Toast.makeText(this, "Watch Movie feature coming soon!", Toast.LENGTH_SHORT).show();
        });

        dialog.show();
    }
}