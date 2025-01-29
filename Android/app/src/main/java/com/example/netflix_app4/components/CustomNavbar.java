package com.example.netflix_app4.components;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Switch;

import com.bumptech.glide.Glide;
import com.example.netflix_app4.R;
import com.example.netflix_app4.model.CategoryModel;
import com.example.netflix_app4.model.UserInfo;

import java.util.List;

public class CustomNavbar extends LinearLayout {
    private static final String TAG = "CustomNavbar";

    // אותם שדות כמו קודם
    private ImageView userAvatarImageView;
    private TextView userNameTextView;
    private Button searchButton;
    private LinearLayout categoriesContainer;
    private Switch darkModeSwitch;
    private Button adminButton;
    private UserInfo currentUser;

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
        userAvatarImageView = findViewById(R.id.user_avatar);
        userNameTextView = findViewById(R.id.user_name);
        searchButton = findViewById(R.id.search_button);
        categoriesContainer = findViewById(R.id.categories_container);
        darkModeSwitch = findViewById(R.id.dark_mode_switch);
        adminButton = findViewById(R.id.admin_button);
        Log.d(TAG, "Finished initializeComponents");
    }

    public void setUserDetails(UserInfo userInfo) {
        Log.d(TAG, "setUserDetails started");
        this.currentUser = userInfo;
        userNameTextView.setText(userInfo.getName());

        if (userInfo.getAvatar() != null) {
            Log.d(TAG, "Attempting to load avatar from: " + userInfo.getAvatar());
            Glide.with(getContext())
                    .load(userInfo.getAvatar())

                    .circleCrop()                         // חיתוך התמונה לעיגול
                    .into(userAvatarImageView);
        } else {
            Log.e(TAG, "Avatar URL is null");
        }


        adminButton.setVisibility(userInfo.isAdmin() ? VISIBLE : GONE);
        Log.d(TAG, "setUserDetails completed");
    }

    public void addCategories(List<CategoryModel> categories) {
        Log.d(TAG, "addCategories started");
        categoriesContainer.removeAllViews();
        Button allCategoriesButton = createCategoryButton("All", null);
        categoriesContainer.addView(allCategoriesButton);

        for (CategoryModel categoryModel : categories) {
            Button categoryButton = createCategoryButton(categoryModel.getName(), categoryModel);
            categoriesContainer.addView(categoryButton);
        }
        Log.d(TAG, "addCategories completed");
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

    // שאר המתודות נשארות בדיוק אותו דבר
    private void toggleDarkMode(boolean isDarkMode) {
        Log.d(TAG, "toggleDarkMode called: " + isDarkMode);
    }

    private void navigateToAdminPanel() {
        Log.d(TAG, "navigateToAdminPanel called");
    }

    private void navigateToAllCategories() {
        Log.d(TAG, "navigateToAllCategories called");
    }

    private void navigateToCategoryMovies(CategoryModel categoryModel) {
        Log.d(TAG, "navigateToCategoryMovies called for: " + categoryModel.getName());
    }

    private void setupCategoriesListeners() {
        Log.d(TAG, "setupCategoriesListeners called");
    }
}
