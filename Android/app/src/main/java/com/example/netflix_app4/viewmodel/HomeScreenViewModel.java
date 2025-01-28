package com.example.netflix_app4.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.netflix_app4.model.CategoryPromoted;
import com.example.netflix_app4.model.LastWatched;
import com.example.netflix_app4.model.MovieModel;
import com.example.netflix_app4.repository.MovieRepository;

import java.util.List;

public class HomeScreenViewModel extends ViewModel {
    private final CategoryViewModel categoryViewModel;
    private final MovieViewModel movieViewModel;
    private final MutableLiveData<String> userId;

    public HomeScreenViewModel() {
        categoryViewModel = new CategoryViewModel();
        movieViewModel = new MovieViewModel();
        userId = new MutableLiveData<>();
    }

    // Set the user ID dynamically
    public void setUserId(String id) {
        userId.setValue(id);
        categoryViewModel.fetchCategories(id);
    }

    // Expose LiveData for categories
    public LiveData<List<CategoryPromoted>> getPromotedCategoriesLiveData() {
        return categoryViewModel.getPromotedCategoriesLiveData();
    }

    // Expose LiveData for last watched
    public LiveData<LastWatched> getLastWatchedLiveData() {
        return categoryViewModel.getLastWatchedLiveData();
    }

    // Expose LiveData for random movie details
    public LiveData<MovieModel> getRandomMovieLiveData() {
        return movieViewModel.getMovieLiveData();
    }

    // Expose LiveData for errors
    public LiveData<String> getErrorLiveData() {
        return movieViewModel.getErrorLiveData();
    }

    // Fetch a random movie
    public void fetchRandomMovie(String movieId) {
        String id = userId.getValue();
        if (id != null) {
            movieViewModel.fetchMovieById(movieId, id);
        }
    }
}


