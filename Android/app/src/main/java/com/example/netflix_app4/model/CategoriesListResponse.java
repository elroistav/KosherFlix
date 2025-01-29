package com.example.netflix_app4.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CategoriesListResponse {
    @SerializedName("categories")
    private List<CategoryModel> categories;

    public List<CategoryModel> getCategories() {
        return categories;
    }

    public void setCategories(List<CategoryModel> categories) {
        this.categories = categories;
    }
}