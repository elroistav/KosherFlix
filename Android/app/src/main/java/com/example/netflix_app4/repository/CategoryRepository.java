package com.example.netflix_app4.repository;


import android.content.Context;
import android.util.Log;

import com.example.netflix_app4.db.AppDatabase;
import com.example.netflix_app4.db.CategoryDao;
import com.example.netflix_app4.db.CategoryEntity;
import com.example.netflix_app4.db.CategoryMovieCrossRef;
import com.example.netflix_app4.db.EntityConverter;
import com.example.netflix_app4.db.LastWatchedDao;
import com.example.netflix_app4.db.LastWatchedEntity;
import com.example.netflix_app4.db.MovieDao;
import com.example.netflix_app4.db.MovieEntity;
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
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoryRepository {
    private static final String TAG = "CategoryRepository";
    private final MovieApiService apiService;
    private AppDatabase database;
    private CategoryDao categoryDao;
    private MovieDao movieDao;
    private LastWatchedDao lastWatchedDao;


    public CategoryRepository(Context context) {
        apiService = RetrofitClient.getRetrofitInstance().create(MovieApiService.class);
        database = AppDatabase.getDatabase(context.getApplicationContext());  // Fix: Use application context
        categoryDao = database.categoryDao();
        movieDao = database.movieDao();
        lastWatchedDao = database.lastWatchedDao();
    }

    public void insertCategories(CategoriesResponse response) {
        List<CategoryEntity> categories = EntityConverter.convertResponseToCategories(response);
        List<MovieEntity> movies = EntityConverter.convertResponseToMovies(response);
        List<CategoryMovieCrossRef> crossRefs = EntityConverter.convertResponseToCategoryMovieCrossRefs(response);
        List<LastWatchedEntity> lastWatchedEntities = EntityConverter.convertLastWatchedToEntities(response.getLastWatched());

        AppDatabase.databaseWriteExecutor.execute(() -> {
            categoryDao.insertCategories(categories);
            movieDao.insertMovies(movies);
            categoryDao.insertCategoryMovieCrossRefs(crossRefs);
            lastWatchedDao.insertLastWatched(lastWatchedEntities);
        });
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

    public void getRandomMovie(Context context, String userId, RandomMovieCallback callback) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            List<MovieEntity> cachedMovies = database.movieDao().getAllMovies();
            if (!cachedMovies.isEmpty()) {
                MovieEntity randomMovie = cachedMovies.get(new Random().nextInt(cachedMovies.size()));
                callback.onSuccess(convertEntityToMovieModel(randomMovie));
            } else {
                fetchMoviesFromApi(userId, callback);
            }
        });
    }

    private void fetchMoviesFromApi(String userId, RandomMovieCallback callback) {
        getCategories(userId, new CategoryCallback() {
            @Override
            public void onSuccess(CategoriesResponse response) {
                List<CategoryEntity> categories = EntityConverter.convertResponseToCategories(response);
                List<MovieEntity> movies = EntityConverter.convertResponseToMovies(response);
                List<CategoryMovieCrossRef> crossRefs = EntityConverter.convertResponseToCategoryMovieCrossRefs(response);
                List<LastWatchedEntity> lastWatchedEntities = EntityConverter.convertLastWatchedToEntities(response.getLastWatched());

                AppDatabase.databaseWriteExecutor.execute(() -> {
                    database.categoryDao().insertCategories(categories);
                    database.movieDao().insertMovies(movies);
                    database.categoryDao().insertCategoryMovieCrossRefs(crossRefs);
                    database.lastWatchedDao().insertLastWatched(lastWatchedEntities);
                });

                // Select a random movie from the API response
                List<String> allMovieIds = response.getPromotedCategories()
                        .stream()
                        .flatMap(cat -> cat.getMovies().stream())
                        .collect(Collectors.toList());

                if (!allMovieIds.isEmpty()) {
                    String randomMovieId = allMovieIds.get(new Random().nextInt(allMovieIds.size()));
                    apiService.getMovieById(randomMovieId, userId).enqueue(new Callback<MovieModel>() {
                        @Override
                        public void onResponse(Call<MovieModel> call, Response<MovieModel> movieResponse) {
                            if (movieResponse.isSuccessful() && movieResponse.body() != null) {
                                MovieModel movie = movieResponse.body();
                                AppDatabase.databaseWriteExecutor.execute(() -> {
                                    database.movieDao().insertMovie(convertMovieModelToEntity(movie));
                                });
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
                    callback.onError("No movies found.");
                }
            }

            @Override
            public void onError(String error) {
                callback.onError(error);
            }
        });
    }

    private MovieEntity convertMovieModelToEntity(MovieModel model) {
        return new MovieEntity(
                model.getId(),      // Correct ID field
                model.getTitle(),
                model.getThumbnail()
        );
    }

    private MovieModel convertEntityToMovieModel(MovieEntity entity) {
        return new MovieModel(
                entity.getId(),
                entity.getTitle(),
                "", // Default description (missing in MovieEntity)
                0.0, // Default rating
                0, // Default length
                "", // Default director
                new ArrayList<>(), // Empty categories list
                "", // Default language
                "", // Default releaseDate
                entity.getThumbnail(),
                "" // Default video URL
        );
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
                        // Treat both successful responses and 204 No Content as success
                        if (response.isSuccessful() || response.code() == 204) {
                            Log.d(TAG, "Category updated successfully");
                            // Simply call success with no modification
                            callback.onSuccess(category);
                        } else {
                            Log.e(TAG, "Failed to update category. Response code: " + response.code());
                            callback.onError("Failed to update category");
                        }
                    }

                    @Override
                    public void onFailure(Call<CategoryModel> call, Throwable t) {
                        Log.e(TAG, "Network error updating category", t);
                        callback.onError("Network error: " + t.getMessage());
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
                        // Log detailed information about the response
                        Log.d(TAG, "Add Category Response Code: " + response.code());
                        Log.d(TAG, "Is Successful: " + response.isSuccessful());

                        try {
                            if (response.body() != null) {
                                Log.d(TAG, "Response Body: " + response.body().toString());
                            } else {
                                Log.d(TAG, "Response Body is null");
                            }

                            String errorBody = response.errorBody() != null ?
                                    response.errorBody().string() : "No error body";
                            Log.d(TAG, "Error Body: " + errorBody);
                        } catch (IOException e) {
                            Log.e(TAG, "Error logging response details", e);
                        }

                        if (response.isSuccessful() && response.body() != null) {
                            Log.d(TAG, "Category added successfully");
                            callback.onSuccess(response.body());
                        } else {
                            Log.e(TAG, "Failed to add category");
                            callback.onError("Failed to add category");
                        }
                    }

                    @Override
                    public void onFailure(Call<CategoryModel> call, Throwable t) {
                        Log.e(TAG, "Network error adding category", t);
                        callback.onError("Network error: " + t.getMessage());
                    }
                });
    }
}




