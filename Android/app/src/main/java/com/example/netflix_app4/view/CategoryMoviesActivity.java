package com.example.netflix_app4.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.example.netflix_app4.model.CategoryModel;
import com.example.netflix_app4.model.MovieModel;
import com.example.netflix_app4.model.UserInfo;
import com.example.netflix_app4.network.MovieApiService;
import com.example.netflix_app4.network.RetrofitClient;
import com.example.netflix_app4.viewmodel.CategoryViewModel;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoryMoviesActivity extends AppCompatActivity {
    private static final String TAG = "CategoryMoviesActivity";

    private CustomNavbar customNavbar;
    private RecyclerView moviesRecyclerView;
    private MovieAdapter movieAdapter;
    private CategoryViewModel categoryViewModel;
    private Button navbarToggleButton;
    private boolean isNavbarVisible = false;
    private TextView categoryTitleTextView;
    private UserInfo userInfo;
    private List<MovieModel> movieDetails = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_movies);

        userInfo = (UserInfo) getIntent().getSerializableExtra("userInfo");
        CategoryModel category = getIntent().getParcelableExtra("category");

        if (userInfo == null || category == null) {
            redirectToLogin();
            return;
        }

        initializeViews();
        setupNavbar(userInfo);
        setupRecyclerView();

        categoryTitleTextView.setText(category.getName());
        fetchMoviesForCategory(category, userInfo.getUserId());
    }

    private void initializeViews() {
        customNavbar = findViewById(R.id.custom_navbar);
        customNavbar.setVisibility(View.GONE);
        navbarToggleButton = findViewById(R.id.navbarToggleButton);
        navbarToggleButton.setOnClickListener(v -> toggleNavbar());
        categoryTitleTextView = findViewById(R.id.categoryTitle);
        moviesRecyclerView = findViewById(R.id.moviesRecyclerView);
    }

    private void setupNavbar(UserInfo userInfo) {
        categoryViewModel = new ViewModelProvider(this).get(CategoryViewModel.class);
        customNavbar.initializeCategoryViewModel(categoryViewModel);
        customNavbar.setUserDetails(userInfo);
    }

    private void setupRecyclerView() {
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        moviesRecyclerView.setLayoutManager(layoutManager);
        movieAdapter = new MovieAdapter(this, movieDetails, userInfo);
        moviesRecyclerView.setAdapter(movieAdapter);
    }

    private void fetchMoviesForCategory(CategoryModel category, String userId) {
        List<String> movieIds = category.getMovies();
        if (movieIds == null || movieIds.isEmpty()) {
            return;
        }

        MovieApiService apiService = RetrofitClient.getRetrofitInstance().create(MovieApiService.class);

        for (String movieId : movieIds) {
            apiService.getMovieById(movieId, userId).enqueue(new Callback<>() {
                @Override
                public void onResponse(Call<MovieModel> call, Response<MovieModel> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        movieDetails.add(response.body());
                        // Create new adapter instance with updated list
                        movieAdapter = new MovieAdapter(CategoryMoviesActivity.this,
                                new ArrayList<>(movieDetails),
                                userInfo);
                        moviesRecyclerView.setAdapter(movieAdapter);
                    }
                }

                @Override
                public void onFailure(Call<MovieModel> call, Throwable t) {
                    Toast.makeText(CategoryMoviesActivity.this,
                            "Error loading movies: " + t.getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            });
        }
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
}