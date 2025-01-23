package com.example.netflix_app4.network;

import com.example.netflix_app4.model.CategoriesResponse;
import com.example.netflix_app4.model.MovieModel;

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
}
