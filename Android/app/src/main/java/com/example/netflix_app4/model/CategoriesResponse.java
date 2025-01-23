package com.example.netflix_app4.model;

import java.util.List;

public class CategoriesResponse {
    private List<CategoryPromoted> promotedCategories;
    private LastWatched lastWatched;

    public List<CategoryPromoted> getPromotedCategories() {
        return promotedCategories;
    }

    public LastWatched getLastWatched() {
        return lastWatched;
    }
}
