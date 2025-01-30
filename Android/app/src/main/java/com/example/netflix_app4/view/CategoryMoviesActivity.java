package com.example.netflix_app4.view;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
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
import java.util.Objects;

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
    private TextView categoryTitleTextView;  // נוסיף כותרת לקטגוריה

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_movies);

        UserInfo userInfo = (UserInfo) getIntent().getSerializableExtra("userInfo");
        CategoryModel category = getIntent().getParcelableExtra("category");

        if (userInfo == null || category == null) {
            redirectToLogin();
            return;
        }

        initializeViews();
        setupNavbar(userInfo);
        setupRecyclerView();

        // נציג את שם הקטגוריה
        categoryTitleTextView.setText(category.getName());

        // נביא את הסרטים של הקטגוריה
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
        movieAdapter = new MovieAdapter(this, new ArrayList<>(), this::showMoviePopup);
        moviesRecyclerView.setAdapter(movieAdapter);
    }

    private void fetchMoviesForCategory(CategoryModel category, String userId) {
        List<String> movieIds = category.getMovies();
        if (movieIds == null || movieIds.isEmpty()) {
            return;
        }

        List<MovieModel> movieDetails = new ArrayList<>();
        MovieApiService apiService = RetrofitClient.getRetrofitInstance().create(MovieApiService.class);

        for (String movieId : movieIds) {
            apiService.getMovieById(movieId, userId).enqueue(new Callback<>() {
                @Override
                public void onResponse(Call<MovieModel> call, Response<MovieModel> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        movieDetails.add(response.body());
                        movieAdapter.updateMovies(movieDetails);
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

    public void showMoviePopup(MovieModel movie) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.movie_details);
        Objects.requireNonNull(dialog.getWindow()).setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView movieTitle = dialog.findViewById(R.id.movieTitle);
        TextView movieDescription = dialog.findViewById(R.id.movieDescription);
        Button watchButton = dialog.findViewById(R.id.watchButton);

        movieTitle.setText(movie.getTitle());
        movieDescription.setText(movie.getDescription());

        watchButton.setOnClickListener(v -> {
            Toast.makeText(this, "Watch Movie feature coming soon!", Toast.LENGTH_SHORT).show();
        });

        dialog.show();
    }
}