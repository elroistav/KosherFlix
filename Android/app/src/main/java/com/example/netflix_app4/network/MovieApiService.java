package com.example.netflix_app4.network;

import com.example.netflix_app4.model.CategoriesResponse;
import com.example.netflix_app4.model.MovieModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;

public interface MovieApiService {
    // Fetch a movie by its ID
    @GET("movies/{id}/")
    Call<MovieModel> getMovieById(
            @Path("id") String movieId, // Movie ID passed as a path parameter
            @Header("user-id") String userId // User ID passed in the header
    );

    // Fetch all categories
    @GET("movies/")
    Call<CategoriesResponse> getCategories(
            @Header("user-id") String userId // User ID passed in the header
    );

    @GET("movies/{id}/recommend")
    Call<List<String>> getRecommendations(
            @Path("id") String movieId,
            @Header("user-id") String userId
    );

    // Search movies
    @GET("movies/search/{query}")
    Call<List<MovieModel>> searchMovies(
            @Path("query") String query,
            @Header("user-id") String userId
    );
}