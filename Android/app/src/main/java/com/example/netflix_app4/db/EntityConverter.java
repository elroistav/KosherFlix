package com.example.netflix_app4.db;

import com.example.netflix_app4.model.CategoriesResponse;
import com.example.netflix_app4.model.CategoryModel;
import com.example.netflix_app4.model.CategoryPromoted;
import com.example.netflix_app4.model.LastWatched;
import com.example.netflix_app4.model.MovieModel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class EntityConverter {

    public static List<CategoryEntity> convertResponseToCategories(CategoriesResponse response) {
        List<CategoryEntity> categories = new ArrayList<>();
        for (CategoryPromoted category : response.getPromotedCategories()) {
            categories.add(new CategoryEntity(category.getCategory(), true));
        }
        return categories;
    }

    public static List<MovieEntity> convertResponseToMovies(CategoriesResponse response) {
        List<MovieEntity> movies = new ArrayList<>();
        Set<String> movieIds = new HashSet<>();

        for (CategoryPromoted category : response.getPromotedCategories()) {
            for (String movieId : category.getMovies()) {
                if (!movieIds.contains(movieId)) {
                    movies.add(new MovieEntity(movieId, "Unknown", "placeholder.jpg"));
                    movieIds.add(movieId);
                }
            }
        }
        return movies;
    }

    public static List<CategoryMovieCrossRef> convertResponseToCategoryMovieCrossRefs(CategoriesResponse response) {
        List<CategoryMovieCrossRef> crossRefs = new ArrayList<>();
        for (CategoryPromoted category : response.getPromotedCategories()) {
            for (String movieId : category.getMovies()) {
                crossRefs.add(new CategoryMovieCrossRef(category.getCategory(), movieId));
            }
        }
        return crossRefs;
    }

    public static List<LastWatchedEntity> convertLastWatchedToEntities(LastWatched lastWatched) {
        List<LastWatchedEntity> lastWatchedEntities = new ArrayList<>();
        for (String movieId : lastWatched.getMovies()) {
            lastWatchedEntities.add(new LastWatchedEntity(movieId));
        }
        return lastWatchedEntities;
    }
}

