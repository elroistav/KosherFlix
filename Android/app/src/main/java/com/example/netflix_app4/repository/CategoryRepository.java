package com.example.netflix_app4.repository;

import android.content.Context;
import android.util.Log;

import com.example.netflix_app4.model.CategoryPromoted;
import com.example.netflix_app4.model.MovieModel;
import com.example.netflix_app4.network.Config;
import com.example.netflix_app4.network.MovieApiService;
import com.example.netflix_app4.network.RetrofitClient;
import com.example.netflix_app4.model.CategoriesResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoryRepository {
    private final MovieApiService apiService;

    public CategoryRepository() {
        apiService = RetrofitClient.getRetrofitInstance().create(MovieApiService.class);
    }

    // Fetch categories using a callback
    public void getCategories(String userId, CategoryCallback callback) {
        apiService.getCategories(userId).enqueue(new Callback<CategoriesResponse>() {
            @Override
            public void onResponse(Call<CategoriesResponse> call, Response<CategoriesResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Failed to fetch categories.");
                }
            }

            @Override
            public void onFailure(Call<CategoriesResponse> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    // Fetch a random movie by first fetching categories
    public void getRandomMovie(Context context, String userId, RandomMovieCallback callback) {
        Log.d("CategoryRepository", "Fetching random movie for user: " + userId);
        getCategories(userId, new CategoryCallback() {
            @Override
            public void onSuccess(CategoriesResponse response) {
                Log.d("CategoryRepository", "Fetched categories successfully.");
                List<CategoryPromoted> promotedCategories = response.getPromotedCategories();
                List<String> allMovieIds = new ArrayList<>();

                // Gather all movie IDs from promoted categories
                for (CategoryPromoted category : promotedCategories) {
                    allMovieIds.addAll(category.getMovies());
                }

                if (!allMovieIds.isEmpty()) {
                    // Pick a random movie ID
                    String randomMovieId = allMovieIds.get(new Random().nextInt(allMovieIds.size()));
                    Log.d("CategoryRepository", "Random movie ID: " + randomMovieId);

                    // Fetch movie details for the random movie ID
                    apiService.getMovieById(randomMovieId, userId).enqueue(new Callback<MovieModel>() {
                        @Override
                        public void onResponse(Call<MovieModel> call, Response<MovieModel> movieResponse) {
                            if (movieResponse.isSuccessful() && movieResponse.body() != null) {
                                MovieModel movie = movieResponse.body();
                                Log.d("CategoryRepository", "Fetched random movie: " + movie.getTitle());
                                // Set the video URL for the demo video
                                //movie.setVideoUrl(Config.getBaseUrl() + "/uploads/lion.mp4");
                                Log.d("CategoryRepository", "videoUrl: " + movie.getVideoUrl());
                                callback.onSuccess(movie);
                            } else {
                                callback.onError("Failed to fetch movie details.");
                            }
                        }

                        @Override
                        public void onFailure(Call<MovieModel> call, Throwable t) {
                            callback.onError("Error fetching movie details: " + t.getMessage());
                        }
                    });
                } else {
                    callback.onError("No movies found in promoted categories.");
                }
            }

            @Override
            public void onError(String error) {
                Log.e("CategoryRepository", "Failed to fetch categories: " + error);
                callback.onError(error);
            }
        });
    }

    // Callback interfaces
    public interface CategoryCallback {
        void onSuccess(CategoriesResponse response);
        void onError(String error);
    }

    public interface RandomMovieCallback {
        void onSuccess(MovieModel randomMovie);
        void onError(String error);
    }
}




