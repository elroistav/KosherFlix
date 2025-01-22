package com.example.netflix_app4;

import java.util.List;

public class CategoryPromoted {
    private List<MovieModel> moviesDetails; // To store fetched movie details
    private String category;
    private List<String> movies; // List of movie IDs

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
