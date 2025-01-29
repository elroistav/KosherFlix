package com.example.netflix_app4.view;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.netflix_app4.R;
import com.example.netflix_app4.components.CustomNavbar;
import com.example.netflix_app4.viewmodel.CategoryViewModel;

import java.util.ArrayList;

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

        // אתחול הנאבבר והכפתור שלו - בדיוק כמו במסך הבית
        customNavbar = findViewById(R.id.custom_navbar);
        customNavbar.setVisibility(View.GONE);
        navbarToggleButton = findViewById(R.id.navbarToggleButton);
        navbarToggleButton.setOnClickListener(v -> toggleNavbar());

        // יצירת ViewModel
        categoryViewModel = new ViewModelProvider(this).get(CategoryViewModel.class);

        // אתחול ה-CustomNavbar עם ה-ViewModel
        customNavbar.initializeCategoryViewModel(categoryViewModel);

        // קבלת הטוקן מה-Intent
        String token = getIntent().getStringExtra("USER_TOKEN");
        if (token == null) {
            redirectToLogin();
            return;
        }

        // הגדרת ה-RecyclerView
        categoriesRecyclerView = findViewById(R.id.categoriesRecyclerView);
        categoriesRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // הגדרת האדפטר
        categoryAdapter = new CategoryAdapter(this, new ArrayList<>(), this::showMoviePopup);
        categoriesRecyclerView.setAdapter(categoryAdapter);

        // אתחול הצפייה במידע
        observeViewModels();

        // וולידציה של הטוקן וטעינת המידע
//        loadData(token);
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

    // שאר המתודות זהות למסך הבית
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
        // אותו מימוש כמו במסך הבית
    }
}
