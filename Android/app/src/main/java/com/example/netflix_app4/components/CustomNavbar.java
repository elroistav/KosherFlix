package com.example.netflix_app4.components;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.lifecycle.LifecycleOwner;

import com.bumptech.glide.Glide;
import com.example.netflix_app4.R;
import com.example.netflix_app4.model.CategoryModel;
import com.example.netflix_app4.model.UserInfo;
import com.example.netflix_app4.view.AdminActivity;
import com.example.netflix_app4.view.AllCategoriesActivity;
import com.example.netflix_app4.view.CategoryMoviesActivity;
import com.example.netflix_app4.view.HomeScreenActivity;
import com.example.netflix_app4.viewmodel.CategoryViewModel;
import com.example.netflix_app4.view.WelcomeActivity;

import java.util.List;

public class CustomNavbar extends LinearLayout {
    private static final String TAG = "CustomNavbar";

    // אותם שדות כמו קודם
    private ImageView userAvatarImageView;
    private TextView userNameTextView;
    private SearchBarComponent searchComponent;
    private LinearLayout categoriesContainer;
    private Switch darkModeSwitch;
    private Button adminButton;
    private Button homeButton;

    private UserInfo userInfo;

    private Button categoriesButton;
    private List<CategoryModel> currentCategories;

    private CategoryViewModel categoryViewModel;

    private Button logoutButton;




    public CustomNavbar(Context context, AttributeSet attrs) {
        super(context, attrs);
        Log.d(TAG, "Constructor started");

        LayoutInflater.from(context).inflate(R.layout.custom_navbar_layout, this);
        Log.d(TAG, "Layout inflation completed");

        initializeComponents();
        Log.d(TAG, "Constructor finished");
    }

    private void initializeComponents() {
        Log.d(TAG, "Starting initializeComponents");
        Log.d(TAG, "Current night mode: " +
                (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES ? "NIGHT" : "DAY"));

        userAvatarImageView = findViewById(R.id.user_avatar);
        userNameTextView = findViewById(R.id.user_name);
        searchComponent = findViewById(R.id.search_component);
        categoriesButton = findViewById(R.id.categories_button);
        darkModeSwitch = findViewById(R.id.dark_mode_switch);
        setupCategoriesButton();

        Log.d(TAG, "Initial switch state: " + darkModeSwitch.isChecked());

        darkModeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Log.d(TAG, "Switch changed to: " + isChecked);
            if (isChecked) {
                Log.d(TAG, "Attempting to switch to night mode");
                androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode(
                        androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
                );
            } else {
                Log.d(TAG, "Attempting to switch to day mode");
                androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode(
                        androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
                );
            }
        });

        adminButton = findViewById(R.id.admin_button);
        homeButton = findViewById(R.id.home_button);
        setupHomeButton();
        adminButton.setOnClickListener(v -> navigateToAdminPanel());
        logoutButton = findViewById(R.id.logout_button);
        logoutButton.setOnClickListener(v -> handleLogout());
        Log.d(TAG, "Finished initializeComponents");
    }


    public void initializeCategoryViewModel(CategoryViewModel viewModel) {
        Log.d(TAG, "Initializing CategoryViewModel");
        this.categoryViewModel = viewModel;

        if (getContext() instanceof LifecycleOwner) {
            viewModel.getAllCategoriesLiveData().observe((LifecycleOwner) getContext(), categories -> {
                Log.d(TAG, "Received categories from ViewModel: " + (categories != null ? categories.size() : "null"));
                this.currentCategories = categories;
            });

        }
    }

    public void setUserDetails(UserInfo userInfo) {
        Log.d(TAG, "setUserDetails started");
        this.userInfo = userInfo;
        userNameTextView.setText(userInfo.getName());

        if (userInfo.getAvatar() != null) {
            Log.d(TAG, "Attempting to load avatar from: " + userInfo.getAvatar());
            Glide.with(getContext())
                    .load(userInfo.getAvatar())

                    .circleCrop()
                    .into(userAvatarImageView);
        } else {
            Log.e(TAG, "Avatar URL is null");
        }

        adminButton.setVisibility(userInfo.isAdmin() ? VISIBLE : GONE);
        Log.d(TAG, "Is admin: " + userInfo.isAdmin());


        // categories after we have userInfo
        if (categoryViewModel != null) {
            Log.d(TAG, "Fetching categories for user: " + userInfo.getUserId());
            categoryViewModel.fetchAllCategories(userInfo.getUserId());
        } else {
            Log.e(TAG, "CategoryViewModel not initialized yet");
        }

        searchComponent.setUserInfo(userInfo);

        Log.d(TAG, "setUserDetails completed");

    }
    private void setupCategoriesButton() {
        categoriesButton.setOnClickListener(v -> showCategoriesMenu());
    }
    private void showCategoriesMenu() {
        PopupMenu popup = new PopupMenu(getContext(), categoriesButton);

        popup.getMenu().add("All Categories");

        if (currentCategories != null) {
            for (CategoryModel category : currentCategories) {
                popup.getMenu().add(category.getName());
            }
        }

        popup.setOnMenuItemClickListener(item -> {
            String selectedCategory = item.getTitle().toString();
            if (selectedCategory.equals("All Categories")) {
                navigateToAllCategories();
            } else {
                CategoryModel selectedModel = currentCategories.stream()
                        .filter(cat -> cat.getName().equals(selectedCategory))
                        .findFirst()
                        .orElse(null);

                if (selectedModel != null) {
                    navigateToCategoryMovies(selectedModel);
                }
            }
            return true;
        });

        popup.show();
    }

    public void addCategories(List<CategoryModel> categories) {
        this.currentCategories = categories;
    }

    private Button createCategoryButton(String name, CategoryModel categoryModel) {
        Log.d(TAG, "Creating button for category: " + name);
        Button button = new Button(getContext());
        button.setText(name);
        button.setOnClickListener(v -> {
            if (categoryModel == null) {
                navigateToAllCategories();
            } else {
                navigateToCategoryMovies(categoryModel);
            }
        });
        return button;
    }

    private void toggleDarkMode(boolean isDarkMode) {
        Log.d(TAG, "toggleDarkMode called: " + isDarkMode);
    }

    private void navigateToAdminPanel() {
            Log.d(TAG, "navigateToAdminPanel called");

            if (userInfo != null && userInfo.isAdmin()) {
                Intent intent = new Intent(getContext(), AdminActivity.class);
                intent.putExtra("userInfo", userInfo);
                getContext().startActivity(intent);
            } else {
                Log.e(TAG, "User is not an admin, cannot navigate to admin panel.");
            }
    }

    private void navigateToAllCategories() {
        Intent intent = new Intent(getContext(), AllCategoriesActivity.class);
        intent.putExtra("userInfo", userInfo);
        getContext().startActivity(intent);
    }

    private void navigateToCategoryMovies(CategoryModel category) {
        Intent intent = new Intent(getContext(), CategoryMoviesActivity.class);
        intent.putExtra("category", category);
        intent.putExtra("userInfo", userInfo);
        getContext().startActivity(intent);
    }

    private void setupCategoriesListeners() {
        Log.d(TAG, "setupCategoriesListeners called");
    }

    private void setupHomeButton() {
        homeButton.setOnClickListener(v -> navigateToHome());
    }

    private void navigateToHome() {


        Intent intent = new Intent(getContext(), HomeScreenActivity.class);
        intent.putExtra("USER_TOKEN", userInfo.getToken());
        getContext().startActivity(intent);
    }

    private void handleLogout() {
        new android.app.AlertDialog.Builder(getContext())
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    // Clear all activities and start WelcomeActivity
                    Intent intent = new Intent(getContext(), WelcomeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    getContext().startActivity(intent);
                })
                .setNegativeButton("No", null)
                .show();
    }
}
