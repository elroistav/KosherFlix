package com.example.netflix_app4.model;

import java.util.ArrayList;
import java.util.List;

public class CategoryPromoted {
    private final String category;
    private final boolean isPromoted;
    private final List<String> movies; // List of movie IDs
    private List<MovieModel> moviesDetails; // To store fetched movie details

    public CategoryPromoted(String category, boolean isPromoted) {
        this.category = category;
        this.isPromoted = isPromoted;
        this.movies = new ArrayList<>();
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

    public void addMovie(String movieId) {
        if (!movies.contains(movieId)) {
            movies.add(movieId);
        }
    }

    public void addMovieDetails(MovieModel movie) {
        // Add to moviesDetails only if we have its ID in the movies list
        if (movies.contains(movie.getId())) {
            // Remove existing if present to avoid duplicates
            moviesDetails.removeIf(m -> m.getId().equals(movie.getId()));
            moviesDetails.add(movie);
        }
    }

    public void setMoviesDetails(List<MovieModel> details) {
        this.moviesDetails = new ArrayList<>();
        for (MovieModel movie : details) {
            addMovieDetails(movie);
        }
    }

    public boolean isPromoted() {
        return isPromoted;
    }
}