package com.example.netflix_app4;

import java.util.List;

public class MovieModel {
    private String _id;
    private String title;
    private String description;
    private double rating;
    private int length;
    private String director;
    private List<CategoryModel> categories; // Update to match the JSON
    private String language;
    private String releaseDate;
    private String thumbnail;
    private String videoUrl;

    // Getters
    public String getId() {
        return _id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public double getRating() {
        return rating;
    }

    public int getLength() {
        return length;
    }

    public String getDirector() {
        return director;
    }

    public List<CategoryModel> getCategories() {
        return categories;
    }

    public String getLanguage() {
        return language;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public String getVideoUrl() {
        return videoUrl;
    }
}
