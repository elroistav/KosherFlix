package com.example.netflix_app4.network;

import com.example.netflix_app4.model.CategoriesListResponse;
import com.example.netflix_app4.model.CategoriesResponse;
import com.example.netflix_app4.model.CategoryModel;
import com.example.netflix_app4.model.MovieModel;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
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

    @GET("categories")
    Call<CategoriesListResponse> getAllCategories(@Header("user-id") String userId);

    @DELETE("categories/{id}")
    Call<Void> deleteCategory(@Path("id") String categoryId, @Header("user-id") String userId);

    @PATCH("categories/{id}")
    Call<CategoryModel> updateCategory(@Path("id") String categoryId, @Body CategoryModel category, @Header("user-id") String userId);

    @POST("categories")
    Call<CategoryModel> addCategory(@Body CategoryModel category, @Header("user-id") String userId);

    @PUT("movies/{id}")
    Call<MovieModel> updateMovie(
            @Path("id") String movieId,
            @Body MovieModel movie,
            @Header("user-id") String userId
    );

    @DELETE("movies/{id}")
    Call<Void> deleteMovie(
            @Path("id") String movieId,
            @Header("user-id") String userId
    );

    @Multipart
    @POST("movies")
    Call<MovieModel> addMovie(
            @Part("title") RequestBody title,
            @Part("description") RequestBody description,
            @Part("rating") RequestBody rating,
            @Part("length") RequestBody length,
            @Part("director") RequestBody director,
            @Part("releaseDate") RequestBody releaseDate,
            @Part("language") RequestBody language,
            @Part("categories[]") List<RequestBody> categories,
            @Part MultipartBody.Part thumbnail,
            @Part MultipartBody.Part videoUrl,
            @Header("user-id") String userId
    );

    @Multipart
    @PUT("movies/{movieId}")
    Call<MovieModel> updateMovie(
            @Path("movieId") String movieId,
            @Part("title") RequestBody title,
            @Part("description") RequestBody description,
            @Part("rating") RequestBody rating,
            @Part("length") RequestBody length,
            @Part("director") RequestBody director,
            @Part("releaseDate") RequestBody releaseDate,
            @Part("language") RequestBody language,
            @Part("categories[]") List<RequestBody> categories,
            @Part MultipartBody.Part thumbnail,
            @Part MultipartBody.Part video,
            @Header("user-id") String userId
    );

    @POST("movies/{id}/recommend")
    Call<Void> recommendMovie(
            @Path("id") String movieId,
            @Header("user-id") String userId
    );
    //.


}

