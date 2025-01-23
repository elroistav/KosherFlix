package com.example.netflix_app4.model;

import java.util.List;

public class LastWatched {
    private String category;
    private List<String> movies; // List of movie IDs

    public String getCategory() {
        return category;
    }

    public List<String> getMovies() {
        return movies;
    }
}
