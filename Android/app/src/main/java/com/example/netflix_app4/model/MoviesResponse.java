package com.example.netflix_app4.model;

import java.util.List;

public class MoviesResponse {
    private List<CategoryModel> promotedCategories;
    private CategoryModel lastWatched;

    // Getters
    public List<CategoryModel> getPromotedCategories() {
        return promotedCategories;
    }

    public CategoryModel getLastWatched() {
        return lastWatched;
    }
}
