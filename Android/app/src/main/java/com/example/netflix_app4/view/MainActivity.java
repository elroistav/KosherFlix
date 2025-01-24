package com.example.netflix_app4.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.netflix_app4.R;
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
        categoryAdapter = new CategoryAdapter(this, new ArrayList<>());
        categoriesRecyclerView.setAdapter(categoryAdapter);
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
                Log.d(TAG, "Last watched category: " + lastWatched.getCategory());
                Log.d(TAG, "Last watched movies: " + lastWatched.getMovies());
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