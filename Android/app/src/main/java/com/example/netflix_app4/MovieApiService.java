package com.example.netflix_app4;

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
    @GET("categories/")
    Call<List<CategoryModel>> getCategories(
            @Header("user-id") String userId // User ID passed in the header
    );
}
