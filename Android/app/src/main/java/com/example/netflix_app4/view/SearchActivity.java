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
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.netflix_app4.R;
import com.example.netflix_app4.model.MovieModel;
import com.example.netflix_app4.viewmodel.SearchViewModel;
import com.example.netflix_app4.model.UserInfo;
import java.util.Objects;

public class SearchActivity extends AppCompatActivity {
    private RecyclerView searchResultsRecyclerView;
    private SearchResultsAdapter searchResultsAdapter;
    private SearchViewModel searchViewModel;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);


        UserInfo userInfo = (UserInfo) getIntent().getSerializableExtra("USER_INFO");
        if (userInfo != null) {
            userId = userInfo.getUserId();
        } else {

            Toast.makeText(this, "User information is missing", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Get the query that was searched
        String query = getIntent().getStringExtra("search_query");
        TextView searchQueryText = findViewById(R.id.searchQueryText);
        searchQueryText.setText("Results for: " + query);

        // Initialize views
        searchResultsRecyclerView = findViewById(R.id.searchResultsRecyclerView);

        // Setup RecyclerView
        searchResultsAdapter = new SearchResultsAdapter(this, this::showMoviePopup);
        searchResultsRecyclerView.setAdapter(searchResultsAdapter);
        searchResultsRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        // Setup ViewModel
        searchViewModel = new ViewModelProvider(this).get(SearchViewModel.class);
        observeViewModel();

        // Get search query from intent and perform search
        String searchQuery = getIntent().getStringExtra("search_query");
        if (searchQuery != null && !searchQuery.isEmpty()) {
            searchViewModel.searchMovies(searchQuery, userId);
        }
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
        Objects.requireNonNull(dialog.getWindow()).setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        // Bind views
        TextView movieTitle = dialog.findViewById(R.id.movieTitle);
        TextView movieDescription = dialog.findViewById(R.id.movieDescription);
        Button watchButton = dialog.findViewById(R.id.watchButton);

        // Set data
        movieTitle.setText(movie.getTitle());
        movieDescription.setText(movie.getDescription());

        // @TODO: Add movie thumbnail and other details
        watchButton.setOnClickListener(v -> {
            Toast.makeText(this, "Watch Movie feature coming soon!", Toast.LENGTH_SHORT).show();
        });

        dialog.show();
    }
}