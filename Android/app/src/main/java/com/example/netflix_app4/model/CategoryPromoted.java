package com.example.netflix_app4.model;

import java.util.ArrayList;
import java.util.List;

public class CategoryPromoted {
    private List<MovieModel> moviesDetails; // To store fetched movie details
    private String category;
    private List<String> movies; // List of movie IDs
    private boolean isPromoted;


    public CategoryPromoted(String category, boolean isPromoted) {
        this.category = category;
        this.isPromoted = isPromoted;
        this.movies = new ArrayList<>(); // Default empty list
        this.moviesDetails = new ArrayList<>();
    }

    public String getCategory() {
        return category;
    }

    public List<String> getMovies() {
        return movies;
    }

    public List<MovieModel> getMoviesDetails() {
        return moviesDetails;
    }

    public void setMoviesDetails(List<MovieModel> moviesDetails) {
        this.moviesDetails = moviesDetails;
    }
}
