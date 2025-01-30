package com.example.netflix_app4.repository;

import android.content.Context;
import android.util.Log;

import com.example.netflix_app4.model.CategoriesListResponse;
import com.example.netflix_app4.model.CategoryModel;
import com.example.netflix_app4.model.CategoryPromoted;
import com.example.netflix_app4.model.MovieModel;
import com.example.netflix_app4.network.MovieApiService;
import com.example.netflix_app4.network.RetrofitClient;
import com.example.netflix_app4.model.CategoriesResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoryRepository {
    private static final String TAG = "CategoryRepository";
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

    public interface AllCategoriesCallback {
        void onSuccess(List<CategoryModel> categories);
        void onError(String error);
    }

    public void getAllCategories(String userId, AllCategoriesCallback callback) {
        Log.d("CategoryRepository", "Fetching all categories for user: " + userId);
        apiService.getAllCategories(userId)
                .enqueue(new Callback<CategoriesListResponse>() {
                    @Override
                    public void onResponse(Call<CategoriesListResponse> call, Response<CategoriesListResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            List<CategoryModel> categories = response.body().getCategories();
                            Log.d("CategoryRepository", "Successfully fetched " + categories.size() + " categories");
                            callback.onSuccess(categories);
                        } else {
                            try {
                                String errorBody = response.errorBody() != null ?
                                        response.errorBody().string() : "No error body";
                                Log.e("CategoryRepository", "Failed to fetch ALL categories. Status: "
                                        + response.code() + ", Error: " + errorBody);
                            } catch (IOException e) {
                                Log.e("CategoryRepository", "Error reading error body", e);
                            }
                            callback.onError("Failed to fetch categories");
                        }
                    }

                    @Override
                    public void onFailure(Call<CategoriesListResponse> call, Throwable t) {
                        Log.e("CategoryRepository", "Network call failed", t);
                        callback.onError("Network error: " + t.getMessage());
                    }
                });
    }


    public interface CategoryOperationCallback {
        void onSuccess();
        void onError(String error);
    }

    public interface CategoryUpdateCallback {
        void onSuccess(CategoryModel updatedCategory);
        void onError(String error);
    }

    // Delete category
    public void deleteCategory(String categoryId, String userId, CategoryOperationCallback callback) {
        Log.d(TAG, "Deleting category: " + categoryId);
        apiService.deleteCategory(categoryId, userId)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            Log.d(TAG, "Category deleted successfully");
                            callback.onSuccess();
                        } else {
                            Log.e(TAG, "Failed to delete category: " + response.code());
                            callback.onError("Failed to delete category");
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Log.e(TAG, "Error deleting category", t);
                        callback.onError("Error deleting category: " + t.getMessage());
                    }
                });
    }

    // Update category
    public void updateCategory(String categoryId, CategoryModel category, String userId, CategoryUpdateCallback callback) {
        Log.d(TAG, "Updating category: " + categoryId);
        apiService.updateCategory(categoryId, category, userId)
                .enqueue(new Callback<CategoryModel>() {
                    @Override
                    public void onResponse(Call<CategoryModel> call, Response<CategoryModel> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            Log.d(TAG, "Category updated successfully");
                            callback.onSuccess(response.body());
                        } else {
                            Log.e(TAG, "Failed to update category: " + response.code());
                            callback.onError("Failed to update category");
                        }
                    }

                    @Override
                    public void onFailure(Call<CategoryModel> call, Throwable t) {
                        Log.e(TAG, "Error updating category", t);
                        callback.onError("Error updating category: " + t.getMessage());
                    }
                });
    }

    // Add new category
    public void addCategory(CategoryModel category, String userId, CategoryUpdateCallback callback) {
        Log.d(TAG, "Adding new category");
        apiService.addCategory(category, userId)
                .enqueue(new Callback<CategoryModel>() {
                    @Override
                    public void onResponse(Call<CategoryModel> call, Response<CategoryModel> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            Log.d(TAG, "Category added successfully");
                            callback.onSuccess(response.body());
                        } else {
                            Log.e(TAG, "Failed to add category: " + response.code());
                            callback.onError("Failed to add category");
                        }
                    }

                    @Override
                    public void onFailure(Call<CategoryModel> call, Throwable t) {
                        Log.e(TAG, "Error adding category", t);
                        callback.onError("Error adding category: " + t.getMessage());
                    }
                });
    }
}




