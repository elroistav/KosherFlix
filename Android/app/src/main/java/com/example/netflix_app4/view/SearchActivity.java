package com.example.netflix_app4.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.netflix_app4.R;
import com.example.netflix_app4.components.CustomNavbar;
import com.example.netflix_app4.model.MovieModel;
import com.example.netflix_app4.viewmodel.SearchViewModel;
import com.example.netflix_app4.viewmodel.CategoryViewModel;
import com.example.netflix_app4.model.UserInfo;

public class SearchActivity extends AppCompatActivity {
    private RecyclerView searchResultsRecyclerView;
    private SearchResultsAdapter searchResultsAdapter;
    private SearchViewModel searchViewModel;
    private CategoryViewModel categoryViewModel;
    private CustomNavbar customNavbar;
    private Button navbarToggleButton;
    private boolean isNavbarVisible = false;
    private UserInfo userInfo;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);

        userInfo = (UserInfo) getIntent().getSerializableExtra("USER_INFO");
        if (userInfo != null) {
            userId = userInfo.getUserId();
        } else {
            Toast.makeText(this, "User information is missing", Toast.LENGTH_SHORT).show();
            redirectToLogin();
            return;
        }

        // Initialize Navbar
        initializeNavbar(userInfo);

        // Get the query that was searched
        String query = getIntent().getStringExtra("search_query");
        TextView searchQueryText = findViewById(R.id.searchQueryText);
        searchQueryText.setText("Results for: " + query);

        // Initialize views
        searchResultsRecyclerView = findViewById(R.id.searchResultsRecyclerView);

        // Setup RecyclerView
        searchResultsAdapter = new SearchResultsAdapter(this, this::showMovieDetails);
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

    private void initializeNavbar(UserInfo userInfo) {
        customNavbar = findViewById(R.id.custom_navbar);
        customNavbar.setVisibility(View.GONE);
        navbarToggleButton = findViewById(R.id.navbarToggleButton);
        navbarToggleButton.setOnClickListener(v -> toggleNavbar());

        // Setup CategoryViewModel for navbar
        categoryViewModel = new ViewModelProvider(this).get(CategoryViewModel.class);
        customNavbar.initializeCategoryViewModel(categoryViewModel);
        customNavbar.setUserDetails(userInfo);
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

    private void redirectToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
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

    private void showMovieDetails(MovieModel movie) {
        // Navigate to MovieDetailsActivity with the movie and user info
        Intent intent = new Intent(this, MovieDetailsActivity.class);
        intent.putExtra("movieDetails", movie);
        intent.putExtra("USER_INFO", userInfo);
        startActivity(intent);
    }
}