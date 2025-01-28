package com.example.netflix_app4.viewmodel;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.netflix_app4.model.CategoriesResponse;
import com.example.netflix_app4.model.CategoryPromoted;
import com.example.netflix_app4.model.LastWatched;
import com.example.netflix_app4.model.MovieModel;
import com.example.netflix_app4.network.MovieApiService;
import com.example.netflix_app4.network.RetrofitClient;
import com.example.netflix_app4.repository.CategoryRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class CategoryViewModel extends ViewModel {
    private final MutableLiveData<List<CategoryPromoted>> promotedCategoriesLiveData = new MutableLiveData<>();
    private final MutableLiveData<LastWatched> lastWatchedLiveData = new MutableLiveData<>();
    private final MutableLiveData<MovieModel> randomMovieLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();

    private final MovieApiService movieApiService;
    private final CategoryRepository categoryRepository;


    public CategoryViewModel() {
        // Initialize the Retrofit instance and MovieApiService
        Retrofit retrofit = RetrofitClient.getRetrofitInstance();
        movieApiService = retrofit.create(MovieApiService.class);
        categoryRepository = new CategoryRepository();
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

    // Fetch categories
    public void fetchCategories(String userId) {
        categoryRepository.getCategories(userId, new CategoryRepository.CategoryCallback() {
            @Override
            public void onSuccess(CategoriesResponse response) {
                promotedCategoriesLiveData.postValue(response.getPromotedCategories());
                lastWatchedLiveData.postValue(response.getLastWatched());
            }

            @Override
            public void onError(String error) {
                errorLiveData.postValue(error);
            }
        });
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
}



