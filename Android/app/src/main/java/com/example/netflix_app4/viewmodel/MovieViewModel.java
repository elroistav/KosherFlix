package com.example.netflix_app4.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.netflix_app4.model.MovieModel;
import com.example.netflix_app4.repository.MovieRepository;

public class MovieViewModel extends ViewModel {
    private final MutableLiveData<MovieModel> movieLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();

    private final MovieRepository movieRepository;

    public MovieViewModel() {
        movieRepository = new MovieRepository();
    }

    // Expose LiveData for a movie
    public LiveData<MovieModel> getMovieLiveData() {
        return movieLiveData;
    }

    // Expose LiveData for errors
    public LiveData<String> getErrorLiveData() {
        return errorLiveData;
    }

    // Fetch movie by ID and update LiveData
    public void fetchMovieById(String movieId, String userId) {
        movieRepository.getMovieById(movieId, userId, new MovieRepository.MovieCallback() {
            @Override
            public void onSuccess(MovieModel movie) {
                movieLiveData.postValue(movie);
            }

            @Override
            public void onError(String error) {
                errorLiveData.postValue(error);
            }
        });
    }
}
