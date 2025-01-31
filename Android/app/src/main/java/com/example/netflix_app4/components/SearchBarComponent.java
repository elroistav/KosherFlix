package com.example.netflix_app4.components;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;
import androidx.appcompat.widget.SearchView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.netflix_app4.R;
import com.example.netflix_app4.model.UserInfo;
import com.example.netflix_app4.view.SearchActivity;

public class SearchBarComponent extends FrameLayout {
    private ImageView searchIcon;
    private androidx.appcompat.widget.SearchView searchView;
    private boolean isExpanded = false;

    private UserInfo userInfo;

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }


    public SearchBarComponent(@NonNull Context context) {
        super(context);
        init(context);
    }

    public SearchBarComponent(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        // Inflate the layout
        LayoutInflater.from(context).inflate(R.layout.component_search_bar, this, true);

        // Initialize views
        searchIcon = findViewById(R.id.searchIcon);
        searchView = findViewById(R.id.searchView);

        // Initially hide the SearchView
        searchView.setVisibility(GONE);

        // Set up click listener for the icon
        searchIcon.setOnClickListener(v -> toggleSearch());

        // Set up search functionality
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Intent intent = new Intent(context, SearchActivity.class);
                intent.putExtra("search_query", query);
                intent.putExtra("USER_INFO", userInfo);
                context.startActivity(intent);
                collapseSearch();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        // Handle back press or focus loss
        searchView.setOnCloseListener(() -> {
            collapseSearch();
            return true;
        });
    }

    private void toggleSearch() {
        isExpanded = !isExpanded;
        if (isExpanded) {
            searchIcon.setVisibility(GONE);
            searchView.setVisibility(VISIBLE);
            searchView.setIconified(false); // Show keyboard
        } else {
            searchIcon.setVisibility(VISIBLE);
            searchView.setVisibility(GONE);
            searchView.setQuery("", false);
        }
    }

    public void collapseSearch() {
        if (isExpanded) {
            toggleSearch();
        }
    }
}