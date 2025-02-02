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
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class CategoryViewModel extends AndroidViewModel {
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
        new Handler(Looper.getMainLooper()).post(() -> {
            LiveData<List<CategoryWithMovies>> cachedCategoriesWithMovies = database.categoryDao().getPromotedCategories();

            cachedCategoriesWithMovies.observeForever(categoryWithMoviesList -> {
                if (categoryWithMoviesList != null && !categoryWithMoviesList.isEmpty()) {
                    List<CategoryPromoted> categories = convertWithMoviesToPromoted(categoryWithMoviesList);
                    promotedCategoriesLiveData.postValue(categories);
                } else {
                    AppDatabase.databaseWriteExecutor.execute(() -> {
                        categoryRepository.getCategories(userId, new CategoryRepository.CategoryCallback() {
                            @Override
                            public void onSuccess(CategoriesResponse response) {
                                List<CategoryEntity> categoryEntities = convertResponseToEntities(response);
                                List<CategoryMovieCrossRef> crossRefs = convertResponseToCategoryMovieCrossRefs(response);
                                List<LastWatchedEntity> lastWatchedEntities = convertLastWatchedToEntities(response.getLastWatched());

                                // Step 1: Get unique movie IDs from response
                                Set<String> movieIds = new HashSet<>();
                                for (CategoryPromoted category : response.getPromotedCategories()) {
                                    movieIds.addAll(category.getMovies());
                                }

                                // Step 2: Query Room for existing movie IDs
                                List<String> existingMovies = database.movieDao().getExistingMovieIds(new ArrayList<>(movieIds));
                                movieIds.removeAll(existingMovies); // Keep only missing movies

                                if (!movieIds.isEmpty()) {
                                    // Step 3: Fetch missing movie details from API
                                    movieRepository.getMovieDetails(new ArrayList<>(movieIds), userId, new MovieRepository.MovieDetailsCallback() { // Pass userId here
                                        @Override
                                        public void onSuccess(List<MovieEntity> movies) {
                                            Log.d("CategoryViewModel", "Fetched missing movies: " + movies);
                                            AppDatabase.databaseWriteExecutor.execute(() -> {
                                                database.movieDao().insertMovies(movies);
                                                database.categoryDao().insertCategories(categoryEntities);
                                                database.categoryDao().insertCategoryMovieCrossRefs(crossRefs);
                                                database.lastWatchedDao().insertLastWatched(lastWatchedEntities);

                                                new Handler(Looper.getMainLooper()).post(() -> {
                                                    promotedCategoriesLiveData.setValue(response.getPromotedCategories());
                                                    lastWatchedLiveData.setValue(response.getLastWatched());
                                                });
                                            });
                                        }

                                        @Override
                                        public void onError(String error) {
                                            new Handler(Looper.getMainLooper()).post(() -> errorLiveData.setValue(error));
                                        }
                                    });
                                } else {
                                    // If no missing movies, insert categories and update UI
                                    AppDatabase.databaseWriteExecutor.execute(() -> {
                                        database.categoryDao().insertCategories(categoryEntities);
                                        database.categoryDao().insertCategoryMovieCrossRefs(crossRefs);
                                        database.lastWatchedDao().insertLastWatched(lastWatchedEntities);
                                    });

                                    new Handler(Looper.getMainLooper()).post(() -> {
                                        promotedCategoriesLiveData.setValue(response.getPromotedCategories());
                                        lastWatchedLiveData.setValue(response.getLastWatched());
                                    });
                                }
                            }

                            @Override
                            public void onError(String error) {
                                new Handler(Looper.getMainLooper()).post(() -> errorLiveData.setValue(error));
                            }
                        });
                    });
                }
            });
        });
    }




    private List<CategoryPromoted> convertEntitiesToPromoted(List<CategoryEntity> entities) {
        List<CategoryPromoted> promotedCategories = new ArrayList<>();
        for (CategoryEntity entity : entities) {
            promotedCategories.add(new CategoryPromoted(entity.getName(), entity.isPromoted()));
        }
        return promotedCategories;
    }

    public List<CategoryPromoted> convertWithMoviesToPromoted(List<CategoryWithMovies> categoryWithMoviesList) {
        List<CategoryPromoted> promotedCategories = new ArrayList<>();
        for (CategoryWithMovies categoryWithMovies : categoryWithMoviesList) {
            promotedCategories.add(new CategoryPromoted(categoryWithMovies.category.getName(), categoryWithMovies.category.isPromoted()));
        }
        return promotedCategories;
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



