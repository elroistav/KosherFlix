package com.example.netflix_app4.viewmodel;


import static com.example.netflix_app4.db.EntityConverter.convertLastWatchedToEntities;
import static com.example.netflix_app4.db.EntityConverter.convertResponseToCategoryMovieCrossRefs;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.example.netflix_app4.db.CategoryEntity;
import com.example.netflix_app4.db.CategoryMovieCrossRef;
import com.example.netflix_app4.db.LastWatchedEntity;
import com.example.netflix_app4.db.MovieEntity;
import com.example.netflix_app4.model.CategoriesResponse;
import com.example.netflix_app4.model.CategoryModel;
import com.example.netflix_app4.model.CategoryPromoted;
import com.example.netflix_app4.model.CategoryWithMovies;
import com.example.netflix_app4.model.LastWatched;
import com.example.netflix_app4.model.MovieModel;
import com.example.netflix_app4.network.MovieApiService;
import com.example.netflix_app4.network.RetrofitClient;
import com.example.netflix_app4.repository.CategoryRepository;
import com.example.netflix_app4.db.AppDatabase;
import com.example.netflix_app4.repository.MovieRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class CategoryViewModel extends AndroidViewModel {
    private static final String TAG = "CategoryViewModel";

    private final MutableLiveData<List<CategoryPromoted>> promotedCategoriesLiveData = new MutableLiveData<>();
    private final MutableLiveData<LastWatched> lastWatchedLiveData = new MutableLiveData<>();
    private final MutableLiveData<MovieModel> randomMovieLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();

    private final MutableLiveData<List<CategoryModel>> allCategoriesLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> operationSuccessLiveData = new MutableLiveData<>();
    private AppDatabase database;
    private final MovieRepository movieRepository;


    private final MovieApiService movieApiService;
    private final CategoryRepository categoryRepository;




    public CategoryViewModel(@NonNull Application application) {
        super(application);
        // Initialize the Retrofit instance and MovieApiService
        Retrofit retrofit = RetrofitClient.getRetrofitInstance();
        movieApiService = retrofit.create(MovieApiService.class);
        categoryRepository = new CategoryRepository(application.getApplicationContext());
        movieRepository = new MovieRepository(); // Initialize movieRepository
        database = AppDatabase.getDatabase(application);
    }

    // Expose LiveData for promoted categories
    public LiveData<List<CategoryPromoted>> getPromotedCategoriesLiveData() {
        return promotedCategoriesLiveData;
    }

    // Expose LiveData for last watched
    public LiveData<LastWatched> getLastWatchedLiveData() {
        return lastWatchedLiveData;
    }

    // Expose LiveData for random movie
    public LiveData<MovieModel> getRandomMovieLiveData() {
        return randomMovieLiveData;
    }

    // Expose LiveData for errors
    public LiveData<String> getErrorLiveData() {
        return errorLiveData;
    }

    public void fetchCategories(String userId) {
        Log.d(TAG, "fetchCategories: Starting with userId: " + userId);
        new Handler(Looper.getMainLooper()).post(() -> {
            Log.d(TAG, "fetchCategories: Requesting cached data from Room");
            LiveData<List<CategoryWithMovies>> cachedData = database.categoryDao().getPromotedCategories();

            cachedData.observeForever(new Observer<List<CategoryWithMovies>>() {
                @Override
                public void onChanged(List<CategoryWithMovies> categoryWithMoviesList) {
                    Log.d(TAG, "onChanged: Cached data received from Room: " +
                            (categoryWithMoviesList != null ? categoryWithMoviesList.size() + " categories" : "null"));

                    if (categoryWithMoviesList != null) {
                        for (CategoryWithMovies category : categoryWithMoviesList) {
                            Log.d(TAG, "Category: " + category.category.getName() +
                                    ", Movies count: " + (category.movies != null ? category.movies.size() : "null"));
                        }
                    }

                    cachedData.removeObserver(this);

                    if (categoryWithMoviesList != null && !categoryWithMoviesList.isEmpty()) {
                        Log.d(TAG, "onChanged: Using cached data");
                        promotedCategoriesLiveData.postValue(convertWithMoviesToPromoted(categoryWithMoviesList));
                        fetchLastWatched(userId);
                    } else {
                        Log.d(TAG, "onChanged: No cached data, fetching from server");
                        fetchFromServer(userId);
                    }
                }
            });
        });
    }

    private void fetchLastWatched(String userId) {
        Log.d(TAG, "fetchFromServer: Starting server fetch for userId: " + userId);
        categoryRepository.getCategories(userId, new CategoryRepository.CategoryCallback() {
            @Override
            public void onSuccess(CategoriesResponse response) {
                new Handler(Looper.getMainLooper()).post(() -> {
                    Log.d(TAG, "Received " + (response.getLastWatched() != null ? response.getLastWatched().getMovies().size() : "null") + " last watched movies");
                    lastWatchedLiveData.setValue(response.getLastWatched());
                });
            }

            @Override
            public void onError(String error) {
                Log.e(TAG, "Server fetch error: " + error);
                errorLiveData.postValue(error);
            }
        });
    }

    private void fetchFromServer(String userId) {
        Log.d(TAG, "fetchFromServer: Starting server fetch for userId: " + userId);
        categoryRepository.getCategories(userId, new CategoryRepository.CategoryCallback() {
            @Override
            public void onSuccess(CategoriesResponse response) {
                Log.d(TAG, "Server response successful with " +
                        (response.getPromotedCategories() != null ? response.getPromotedCategories().size() : "0") +
                        " promoted categories");

                AppDatabase.databaseWriteExecutor.execute(() -> {
                    try {
                        database.runInTransaction(() -> {
                            // 1. First insert all categories
                            List<CategoryEntity> categoryEntities = convertResponseToEntities(response);
                            Log.d(TAG, "Transaction: Inserting " + categoryEntities.size() + " categories");
                            database.categoryDao().insertCategories(categoryEntities);

                            // 2. Get all movie IDs that need to be fetched
                            Set<String> movieIds = new HashSet<>();
                            for (CategoryPromoted category : response.getPromotedCategories()) {
                                movieIds.addAll(category.getMovies());
                                Log.d(TAG, "Category " + category.getCategory() +
                                        " has " + category.getMovies().size() + " movies");
                            }
                            Log.d(TAG, "Total unique movies to fetch: " + movieIds.size());

                            // 3. Check which movies we already have
                            List<String> existingMovies = database.movieDao().getExistingMovieIds(new ArrayList<>(movieIds));
                            Log.d(TAG, "Found " + existingMovies.size() + " existing movies in Room");
                            movieIds.removeAll(existingMovies);
                            Log.d(TAG, "Need to fetch " + movieIds.size() + " new movies");

                            // 4. Fetch missing movies
                            if (!movieIds.isEmpty()) {
                                CountDownLatch latch = new CountDownLatch(movieIds.size());
                                List<MovieEntity> newMovies = Collections.synchronizedList(new ArrayList<>());

                                for (String movieId : movieIds) {
                                    Log.d(TAG, "Fetching movie: " + movieId);
                                    movieRepository.getMovieById(movieId, userId, new MovieRepository.MovieCallback() {
                                        @Override
                                        public void onSuccess(MovieModel movie) {
                                            Log.d(TAG, "Successfully fetched movie: " + movie.getTitle());
                                            newMovies.add(convertToMovieEntity(movie));
                                            latch.countDown();
                                        }

                                        @Override
                                        public void onError(String error) {
                                            Log.e(TAG, "Error fetching movie " + movieId + ": " + error);
                                            latch.countDown();
                                        }
                                    });
                                }

                                try {
                                    Log.d(TAG, "Waiting for all movie fetches to complete");
                                    latch.await(30, TimeUnit.SECONDS);
                                    Log.d(TAG, "Completed fetching " + newMovies.size() + " movies");
                                } catch (InterruptedException e) {
                                    Log.e(TAG, "Interrupted while waiting for movie fetches", e);
                                    errorLiveData.postValue("Error fetching movies: " + e.getMessage());
                                    return;
                                }

                                // 5. Insert all new movies
                                if (!newMovies.isEmpty()) {
                                    Log.d(TAG, "Inserting " + newMovies.size() + " new movies into Room");
                                    database.movieDao().insertMovies(newMovies);
                                }
                            }

                            // 6. Finally insert cross references
                            List<CategoryMovieCrossRef> crossRefs = convertResponseToCategoryMovieCrossRefs(response);
                            Log.d(TAG, "Inserting " + crossRefs.size() + " category-movie cross references");
                            database.categoryDao().insertCategoryMovieCrossRefs(crossRefs);
                        });

                        // Update UI
                        new Handler(Looper.getMainLooper()).post(() -> {
                            Log.d(TAG, "Updating UI with fetched data");
                            promotedCategoriesLiveData.setValue(response.getPromotedCategories());
                            Log.d(TAG, "Received " + (response.getLastWatched() != null ? response.getLastWatched().getMovies().size() : "null") + " last watched movies");
                            lastWatchedLiveData.setValue(response.getLastWatched());
                        });
                    } catch (Exception e) {
                        Log.e(TAG, "Error in Room transaction", e);
                        errorLiveData.postValue("Error saving data: " + e.getMessage());
                    }
                });
            }

            @Override
            public void onError(String error) {
                Log.e(TAG, "Server fetch error: " + error);
                errorLiveData.postValue(error);
            }
        });
    }

    private MovieEntity convertToMovieEntity(MovieModel movie) {
        return new MovieEntity(
                movie.getId(),
                movie.getTitle(),
                movie.getThumbnail()
        );
    }




    private List<CategoryPromoted> convertEntitiesToPromoted(List<CategoryEntity> entities) {
        List<CategoryPromoted> promotedCategories = new ArrayList<>();
        for (CategoryEntity entity : entities) {
            promotedCategories.add(new CategoryPromoted(entity.getName(), entity.isPromoted()));
        }
        return promotedCategories;
    }

    public List<CategoryPromoted> convertWithMoviesToPromoted(List<CategoryWithMovies> categoryWithMoviesList) {
        Map<String, CategoryPromoted> categoryMap = new HashMap<>();

        for (CategoryWithMovies categoryWithMovies : categoryWithMoviesList) {
            String categoryName = categoryWithMovies.category.getName();

            // Get or create CategoryPromoted
            CategoryPromoted promotedCategory = categoryMap.computeIfAbsent(
                    categoryName,
                    name -> new CategoryPromoted(name, categoryWithMovies.category.isPromoted())
            );

            // Add movie IDs and create MovieModels
            if (categoryWithMovies.movies != null) {
                for (MovieEntity movie : categoryWithMovies.movies) {
                    promotedCategory.addMovie(movie.getId());

                    // Create MovieModel from MovieEntity
                    MovieModel movieModel = new MovieModel(
                            movie.getId(),
                            movie.getTitle(),
                            "",  // description
                            0.0, // rating
                            0,   // length
                            "", // director
                            new ArrayList<>(), // categories
                            "", // language
                            "", // releaseDate
                            movie.getThumbnail(),
                            "" // videoUrl
                    );
                    promotedCategory.addMovieDetails(movieModel);
                }
            }
        }

        return new ArrayList<>(categoryMap.values());
    }

    private List<CategoryEntity> convertResponseToEntities(CategoriesResponse response) {
        List<CategoryEntity> entities = new ArrayList<>();
        for (CategoryPromoted model : response.getPromotedCategories()) {
            entities.add(new CategoryEntity(model.getCategory(), true));
        }
        return entities;
    }


    // Fetch a random movie
    public void fetchRandomMovie(Context context, String userId) {
        categoryRepository.getRandomMovie(context, userId, new CategoryRepository.RandomMovieCallback() {
            @Override
            public void onSuccess(MovieModel randomMovie) {
                randomMovieLiveData.postValue(randomMovie);
            }

            @Override
            public void onError(String error) {
                errorLiveData.postValue(error);
            }
        });
    }

    // Expose LiveData for all categories
    public LiveData<List<CategoryModel>> getAllCategoriesLiveData() {
        return allCategoriesLiveData;
    }

    public void fetchAllCategories(String userId) {
        categoryRepository.getAllCategories(userId, new CategoryRepository.AllCategoriesCallback() {
            @Override
            public void onSuccess(List<CategoryModel> categories) {
                allCategoriesLiveData.postValue(categories);
            }

            @Override
            public void onError(String error) {
                errorLiveData.postValue(error);
            }
        });
    }

    // Delete category
    public void deleteCategory(String categoryId, String userId) {
        categoryRepository.deleteCategory(categoryId, userId, new CategoryRepository.CategoryOperationCallback() {
            @Override
            public void onSuccess() {
                operationSuccessLiveData.postValue(true);
                // Refresh categories list
                fetchAllCategories(userId);
            }

            @Override
            public void onError(String error) {
                errorLiveData.postValue(error);
            }
        });
    }

    // Update category
    public void updateCategory(String categoryId, CategoryModel category, String userId) {
        categoryRepository.updateCategory(categoryId, category, userId, new CategoryRepository.CategoryUpdateCallback() {
            @Override
            public void onSuccess(CategoryModel updatedCategory) {
                operationSuccessLiveData.postValue(true);
                // Refresh categories list
                fetchAllCategories(userId);
            }

            @Override
            public void onError(String error) {
                errorLiveData.postValue(error);
            }
        });
    }

    // Add category
    public void addCategory(CategoryModel category, String userId) {
        categoryRepository.addCategory(category, userId, new CategoryRepository.CategoryUpdateCallback() {
            @Override
            public void onSuccess(CategoryModel newCategory) {
                operationSuccessLiveData.postValue(true);
                // Refresh categories list
                fetchAllCategories(userId);
            }

            @Override
            public void onError(String error) {
                errorLiveData.postValue(error);
            }
        });
    }

    // Additional LiveData getters
    public LiveData<Boolean> getOperationSuccess() {
        return operationSuccessLiveData;
    }
}



