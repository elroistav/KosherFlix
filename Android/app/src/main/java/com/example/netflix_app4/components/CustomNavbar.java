package com.example.netflix_app4.components;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Switch;

import androidx.lifecycle.LifecycleOwner;

import com.bumptech.glide.Glide;
import com.example.netflix_app4.R;
import com.example.netflix_app4.model.CategoryModel;
import com.example.netflix_app4.model.UserInfo;
import com.example.netflix_app4.viewmodel.CategoryViewModel;

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

    private Button categoriesButton;
    private List<CategoryModel> currentCategories;

    private CategoryViewModel categoryViewModel;



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
        categoriesButton = findViewById(R.id.categories_button);
        setupCategoriesButton();  //
        darkModeSwitch = findViewById(R.id.dark_mode_switch);
        adminButton = findViewById(R.id.admin_button);
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
        this.currentUser = userInfo;
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

        // categories after we have userInfo
        if (categoryViewModel != null) {
            Log.d(TAG, "Fetching categories for user: " + userInfo.getUserId());
            categoryViewModel.fetchAllCategories(userInfo.getUserId());
        } else {
            Log.e(TAG, "CategoryViewModel not initialized yet");
        }

        Log.d(TAG, "setUserDetails completed");


        Log.d(TAG, "setUserDetails completed");
    }
    private void setupCategoriesButton() {
        categoriesButton.setOnClickListener(v -> showCategoriesMenu());
    }
    private void showCategoriesMenu() {
        PopupMenu popup = new PopupMenu(getContext(), categoriesButton);
        popup.getMenu().add("All");

        if (currentCategories != null) {
            for (CategoryModel category : currentCategories) {
                popup.getMenu().add(category.getName());
            }
        }

        popup.setOnMenuItemClickListener(item -> {
            String selectedCategory = item.getTitle().toString();
            if (selectedCategory.equals("All")) {
                navigateToAllCategories();
            } else {
                // מוצאים את הקטגוריה המתאימה לפי השם
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
        this.currentCategories = categories; // שומרים את הקטגוריות
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
