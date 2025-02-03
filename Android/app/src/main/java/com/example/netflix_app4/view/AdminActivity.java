package com.example.netflix_app4.view;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

//import com.example.netflix_app4.Manifest;
import com.example.netflix_app4.R;
import com.example.netflix_app4.components.CustomNavbar;
import com.example.netflix_app4.model.CategoryModel;
import com.example.netflix_app4.model.MovieModel;
import com.example.netflix_app4.model.UserInfo;

import com.example.netflix_app4.viewmodel.CategoryViewModel;
import com.example.netflix_app4.viewmodel.MovieViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import android.Manifest;



public class AdminActivity extends AppCompatActivity implements
        AdminCategoryAdapter.OnCategoryDeleteListener,
        AdminCategoryAdapter.OnCategoryEditListener,
        AdminMovieAdapter.OnMovieDeleteListener,
        AdminMovieAdapter.OnMovieEditListener  {

    private static final String TAG = "AdminActivity";

    private CustomNavbar customNavbar;
    private RecyclerView categoriesRecyclerView;
    private AdminCategoryAdapter categoryAdapter;
    private CategoryViewModel categoryViewModel;
    private UserInfo userInfo;
    private FloatingActionButton addCategoryFab;
    private FloatingActionButton addMovieFab;
    private boolean isNavbarVisible = false;

    private CategoryAddDialog currentAddDialog;

    private MovieViewModel movieViewModel;

    private MovieEditDialog dialog;

    private ArrayList<String> categories = new ArrayList<>();



    private static final int PERMISSION_REQUEST = 100;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        // Get UserInfo from intent
        userInfo = (UserInfo) getIntent().getSerializableExtra("userInfo");
        if (userInfo == null || !userInfo.isAdmin()) {
            Toast.makeText(this, "Unauthorized access", Toast.LENGTH_SHORT).show();
            redirectToHome();
            return;
        }

        initializeViews();
        setupViewModel();
        observeViewModel();

        // Fetch all categories
        categoryViewModel.fetchAllCategories(userInfo.getUserId());
    }

    private void initializeViews() {
        // Initialize Navbar
        customNavbar = findViewById(R.id.custom_navbar);
        customNavbar.setUserDetails(userInfo);

        // Initialize navbar toggle
        Button navbarToggleButton = findViewById(R.id.navbarToggleButton);
        navbarToggleButton.setOnClickListener(v -> toggleNavbar());
        customNavbar.setVisibility(View.GONE);

        // Initialize RecyclerView
        categoriesRecyclerView = findViewById(R.id.categoriesRecyclerView);
        categoriesRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize Adapter
        categoryAdapter = new AdminCategoryAdapter(
                this,
                new ArrayList<>(),
                userInfo,
                this,  // CategoryDeleteListener
                this,  // CategoryEditListener
                //this::showMovieDetails,  // MovieClickListener
                this,  // MovieEditListener
                this   // MovieDeleteListener
        );
        categoriesRecyclerView.setAdapter(categoryAdapter);

        // Initialize FABs
        addCategoryFab = findViewById(R.id.addCategoryFab);
        addMovieFab = findViewById(R.id.addMovieFab);

        addCategoryFab.setOnClickListener(v -> showAddCategoryDialog());
        addMovieFab.setOnClickListener(v -> showAddMovieDialog());
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

    private void setupViewModel() {
        movieViewModel = new ViewModelProvider(this).get(MovieViewModel.class);
        categoryViewModel = new ViewModelProvider(this).get(CategoryViewModel.class);
    }

    private void observeViewModel() {
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

        categoryViewModel.getOperationSuccess().observe(this, success -> {
            if (success != null && success) {
                if (currentEditDialog != null && currentEditDialog.isShowing()) {
                    Toast.makeText(this, "Category updated successfully", Toast.LENGTH_SHORT).show();
                    currentEditDialog.handleSaveResult(true, null);
                }
                if (currentAddDialog != null && currentAddDialog.isShowing()) {
                    Toast.makeText(this, "Category added successfully", Toast.LENGTH_SHORT).show();
                    currentAddDialog.handleSaveResult(true, null);
                }
            }
        });

        movieViewModel.getOperationSuccess().observe(this, success -> {
            if (success != null && success) {
                // Close the dialog after successful update
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        });

        movieViewModel.getOperationSuccess().observe(this, success -> {
            if (success != null && success) {
                if (currentAddMovieDialog != null && currentAddMovieDialog.isShowing()) {
                    currentAddMovieDialog.handleSaveResult(true, null);
                    Toast.makeText(this, "Movie added successfully", Toast.LENGTH_SHORT).show();
                    // Refresh categories to show new movie
                    categoryViewModel.fetchAllCategories(userInfo.getUserId());
                }
            }
        });
    }


    private void showAddCategoryDialog() {
        currentAddDialog = new CategoryAddDialog(this,
                newCategory -> categoryViewModel.addCategory(
                        newCategory,
                        userInfo.getUserId()
                )
        );
        currentAddDialog.show();
    }



    @Override
    public void onCategoryDelete(String categoryId) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Category")
                .setMessage("Are you sure you want to delete this category?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    categoryViewModel.deleteCategory(categoryId, userInfo.getUserId());
                })
                .setNegativeButton("No", null)
                .show();
    }

    @Override
    public void onCategoryEdit(CategoryModel category) {
        showEditCategoryDialog(category);
    }

    private CategoryEditDialog currentEditDialog;

    private void showEditCategoryDialog(CategoryModel category) {
        currentEditDialog = new CategoryEditDialog(this, category,
                updatedCategory -> categoryViewModel.updateCategory(
                        updatedCategory.getId(),
                        updatedCategory,
                        userInfo.getUserId()
                )
        );

        currentEditDialog.show();
    }

    @Override
    public void onMovieDelete(String movieId) {
        if (dialog != null && dialog.isShowing()) {
            return;  // Don't show the dialog again if it's already showing
        }

        new AlertDialog.Builder(this)
                .setTitle("Delete Movie")
                .setMessage("Are you sure you want to delete this movie?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    movieViewModel.deleteMovie(movieId, userInfo.getUserId());
                })
                .setNegativeButton("No", null)
                .show();
    }

    @Override
    public void onMovieEdit(MovieModel movie) {
        dialog = new MovieEditDialog(this, movie,
                (updatedMovie, thumbnailUri, videoUri, selectedCategories) -> {
                    // Update through ViewModel - now passing all the required parameters
                    movieViewModel.updateMovie(
                            updatedMovie.getId(),
                            updatedMovie,
                            selectedCategories,  // pass category names
                            thumbnailUri,        // pass thumbnail if changed
                            videoUri,            // pass video if changed
                            userInfo.getUserId(),
                            this                 // pass context
                    );
                });
        dialog.show();
    }

    private void showMovieDetails(MovieModel movie) {
        Intent intent = new Intent(this, MovieDetailsActivity.class);
        intent.putExtra("movieDetails", movie);
        intent.putExtra("USER_INFO", userInfo);
        startActivity(intent);
    }

    private void redirectToHome() {
        Intent intent = new Intent(this, HomeScreenActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private MovieAddDialog currentAddMovieDialog;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass file selection results to the dialog
        if (currentAddMovieDialog != null) {
            currentAddMovieDialog.handleFileSelection(requestCode, resultCode, data);
        }

        if (dialog != null) {
            dialog.handleFileSelection(requestCode, resultCode, data);
        }
    }

    private void showAddMovieDialog() {
        // Check for permissions first
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(new String[]{
                    Manifest.permission.READ_MEDIA_IMAGES,
                    Manifest.permission.READ_MEDIA_VIDEO
            }, PERMISSION_REQUEST);
        } else {
            requestPermissions(new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE
            }, PERMISSION_REQUEST);
        }

        currentAddMovieDialog = new MovieAddDialog(this,
                (newMovie, thumbnailUri, videoUri, selectedCategories) -> {
                    // Update the categories field
                    this.categories = selectedCategories;

                    // Call ViewModel to add movie with categories
                    movieViewModel.addMovie(
                            newMovie,
                            selectedCategories,
                            thumbnailUri,
                            videoUri,
                            userInfo.getUserId(),
                            this
                    );
                }
        );
        currentAddMovieDialog.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST) {
            boolean allGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    break;
                }
            }

            if (!allGranted) {
                Toast.makeText(this, "Permissions are needed to select files", Toast.LENGTH_LONG).show();
            }
        }
    }


}