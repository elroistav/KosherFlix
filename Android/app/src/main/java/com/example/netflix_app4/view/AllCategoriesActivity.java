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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.netflix_app4.R;
import com.example.netflix_app4.components.CustomNavbar;
import com.example.netflix_app4.model.MovieModel;
import com.example.netflix_app4.model.UserInfo;
import com.example.netflix_app4.viewmodel.CategoryViewModel;

import java.util.ArrayList;
import java.util.Objects;

public class AllCategoriesActivity extends AppCompatActivity {
    private static final String TAG = "AllCategoriesActivity";

    private CustomNavbar customNavbar;
    private RecyclerView categoriesRecyclerView;
    private CategoryAdapter categoryAdapter;
    private CategoryViewModel categoryViewModel;
    private Button navbarToggleButton;
    private boolean isNavbarVisible = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_categories);

        // קבלת ה-UserInfo מה-Intent
        UserInfo userInfo = (UserInfo) getIntent().getSerializableExtra("userInfo");
        if (userInfo == null) {
            redirectToLogin();
            return;
        }

        // אתחול הנאבבר
        customNavbar = findViewById(R.id.custom_navbar);
        customNavbar.setVisibility(View.GONE);
        navbarToggleButton = findViewById(R.id.navbarToggleButton);
        navbarToggleButton.setOnClickListener(v -> toggleNavbar());

        // יצירת ViewModel
        categoryViewModel = new ViewModelProvider(this).get(CategoryViewModel.class);

        // אתחול ה-CustomNavbar עם ה-ViewModel
        customNavbar.initializeCategoryViewModel(categoryViewModel);
        customNavbar.setUserDetails(userInfo);

        // הגדרת ה-RecyclerView
        categoriesRecyclerView = findViewById(R.id.categoriesRecyclerView);
        categoriesRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // הגדרת האדפטר
        categoryAdapter = new CategoryAdapter(this, new ArrayList<>(), userInfo);
        categoriesRecyclerView.setAdapter(categoryAdapter);

        // אתחול הצפייה במידע
        observeViewModels();

        // קריאה לפונקציה שמביאה את כל הקטגוריות
        categoryViewModel.fetchAllCategories(userInfo.getUserId());  // שימוש ב-userId מתוך אובייקט ה-UserInfo
    }

    private void observeViewModels() {
        // צפייה בכל הקטגוריות (לא רק המקודמות)
        categoryViewModel.getAllCategoriesLiveData().observe(this, categories -> {
            if (categories != null) {
                categoryAdapter.updateData(categories);
            }
        });

        categoryViewModel.getErrorLiveData().observe(this, error -> {
            if (error != null) {
                Toast.makeText(this, "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

//    private void loadData(String token) {
//        categoryViewModel.validateTokenAndFetchCategories(token);
//    }

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
