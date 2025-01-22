package com.example.netflix_app4;

import java.util.List;

public class CategoryModel {
    private String _id;
    private String name;
    private String description;
    private boolean promoted;
    private List<String> movies;

    // Getters
    public String getId() {
        return _id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public boolean isPromoted() {
        return promoted;
    }

    public List<String> getMovies() {
        return movies;
    }
}
