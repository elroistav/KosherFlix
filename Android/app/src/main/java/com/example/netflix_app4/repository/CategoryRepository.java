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
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
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

    // Fetch categories using a callback

    public void getCategories(String userId, CategoryCallback callback) {
        Log.d(TAG, "Starting getCategories for userId: " + userId);
        apiService.getCategories(userId).enqueue(new Callback<CategoriesResponse>() {
            @Override
            public void onResponse(Call<CategoriesResponse> call, Response<CategoriesResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "Successfully fetched categories response");
                    CategoriesResponse categoriesResponse = response.body();
                    Log.d(TAG, "Number of promoted categories: " +
                            (categoriesResponse.getPromotedCategories() != null ?
                                    categoriesResponse.getPromotedCategories().size() : 0));
                    fetchMovieDetails(categoriesResponse, userId, callback);
                } else {
                    Log.e(TAG, "Failed to fetch categories. Response code: " + response.code());
                    if (response.errorBody() != null) {
                        try {
                            Log.e(TAG, "Error body: " + response.errorBody().string());
                        } catch (IOException e) {
                            Log.e(TAG, "Error reading error body", e);
                        }
                    }
                    callback.onError("Failed to fetch categories.");
                }
            }

            @Override
            public void onFailure(Call<CategoriesResponse> call, Throwable t) {
                Log.e(TAG, "Network error fetching categories", t);
                callback.onError(t.getMessage());
            }
        });
    }

    private void fetchMovieDetails(CategoriesResponse response, String userId, CategoryCallback callback) {
        Set<String> movieIds = new HashSet<>();
        for (CategoryPromoted category : response.getPromotedCategories()) {
            movieIds.addAll(category.getMovies());
            Log.d(TAG, "Category: " + category.getCategory() + " has " +
                    category.getMovies().size() + " movies");
        }

        Log.d(TAG, "Total unique movies to fetch: " + movieIds.size());
        CountDownLatch latch = new CountDownLatch(movieIds.size());
        AtomicBoolean hasError = new AtomicBoolean(false);
        List<MovieModel> fetchedMovies = Collections.synchronizedList(new ArrayList<>());

        for (String movieId : movieIds) {
            Log.d(TAG, "Fetching movie details for ID: " + movieId);
            apiService.getMovieById(movieId, userId).enqueue(new Callback<MovieModel>() {
                @Override
                public void onResponse(Call<MovieModel> call, Response<MovieModel> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        MovieModel movie = response.body();
                        Log.d(TAG, "Successfully fetched movie: " + movie.getTitle());
                        fetchedMovies.add(movie);
                        latch.countDown();
                        Log.d(TAG, "Remaining movies to fetch: " + latch.getCount());
                    } else {
                        Log.e(TAG, "Failed to fetch movie " + movieId +
                                ". Response code: " + response.code());
                        hasError.set(true);
                        latch.countDown();
                    }
                }

                @Override
                public void onFailure(Call<MovieModel> call, Throwable t) {
                    Log.e(TAG, "Network error fetching movie " + movieId, t);
                    hasError.set(true);
                    latch.countDown();
                }
            });
        }

        // נוסיף קוד שמחכה ללאצ' ומעדכן את הקולבק
        AppDatabase.databaseWriteExecutor.execute(() -> {
            try {
                boolean completed = latch.await(30, TimeUnit.SECONDS);
                if (!completed) {
                    Log.e(TAG, "Timeout waiting for movie fetches");
                    callback.onError("Timeout fetching movies");
                    return;
                }

                if (hasError.get()) {
                    Log.e(TAG, "Errors occurred while fetching movies");
                    callback.onError("Failed to fetch all movies");
                    return;
                }

                Log.d(TAG, "Successfully fetched all " + fetchedMovies.size() +
                        " movies. Calling callback.onSuccess");
                callback.onSuccess(response);
            } catch (InterruptedException e) {
                Log.e(TAG, "Interrupted while waiting for movies", e);
                callback.onError("Interrupted while fetching movies");
            }
        });
    }

    public void getRandomMovie(Context context, String userId, RandomMovieCallback callback) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            List<MovieEntity> cachedMovies = database.movieDao().getAllMovies();
            if (!cachedMovies.isEmpty()) {
                MovieEntity randomMovie = cachedMovies.get(new Random().nextInt(cachedMovies.size()));

                // Fetch full details from API
                apiService.getMovieById(randomMovie.getId(), userId).enqueue(new Callback<MovieModel>() {
                    @Override
                    public void onResponse(Call<MovieModel> call, Response<MovieModel> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            Log.d(TAG, "Fetched full movie details: " + response.body().getTitle() + "VideoUrl: " + response.body().getVideoUrl());
                            callback.onSuccess(response.body()); // Return the detailed movie
                        } else {
                            Log.e(TAG, "Failed to fetch full movie details: " + response.message());
                            callback.onError("Failed to fetch movie details");
                        }
                    }

                    @Override
                    public void onFailure(Call<MovieModel> call, Throwable t) {
                        Log.e(TAG, "Error fetching full movie details: " + t.getMessage());
                        callback.onError(t.getMessage());
                    }
                });
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
                List<CategoryMovieCrossRef> crossRefs = EntityConverter.convertResponseToCategoryMovieCrossRefs(response);
                List<LastWatchedEntity> lastWatchedEntities = EntityConverter.convertLastWatchedToEntities(response.getLastWatched());

                // Create an AtomicInteger to track completed movie fetches
                AtomicInteger movieFetchCount = new AtomicInteger(0);
                int totalMoviesToFetch = response.getPromotedCategories()
                        .stream()
                        .mapToInt(cat -> cat.getMovies().size())
                        .sum();

                // Insert categories first
                AppDatabase.databaseWriteExecutor.execute(() -> {
                    database.categoryDao().insertCategories(categories);
                });

                // Fetch and insert movies
                for (CategoryPromoted category : response.getPromotedCategories()) {
                    for (String movieId : category.getMovies()) {
                        apiService.getMovieById(movieId, userId).enqueue(new Callback<MovieModel>() {
                            @Override
                            public void onResponse(Call<MovieModel> call, Response<MovieModel> movieResponse) {
                                if (movieResponse.isSuccessful() && movieResponse.body() != null) {
                                    MovieModel movie = movieResponse.body();
                                    Log.d("MovieApp", "Fetched movie: " + movie.getTitle());

                                    AppDatabase.databaseWriteExecutor.execute(() -> {
                                        // Insert the movie
                                        database.movieDao().insertMovie(convertMovieModelToEntity(movie));

                                        // If this was the last movie to be fetched, insert cross-references
                                        if (movieFetchCount.incrementAndGet() == totalMoviesToFetch) {
                                            database.runInTransaction(() -> {
                                                database.categoryDao().insertCategoryMovieCrossRefs(crossRefs);
                                                database.lastWatchedDao().insertLastWatched(lastWatchedEntities);
                                            });
                                        }
                                    });
                                }
                            }

                            @Override
                            public void onFailure(Call<MovieModel> call, Throwable t) {
                                Log.e("MovieApp", "Error fetching movie details: " + t.getMessage());
                                // Still increment counter even on failure to maintain consistency
                                movieFetchCount.incrementAndGet();
                            }
                        });
                    }
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




