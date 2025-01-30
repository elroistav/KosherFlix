package com.example.netflix_app4.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.netflix_app4.model.MovieModel;
import com.example.netflix_app4.repository.MovieRepository;

public class MovieViewModel extends ViewModel {
    private final MutableLiveData<MovieModel> movieLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();

    private final MutableLiveData<Boolean> operationSuccessLiveData = new MutableLiveData<>();

    private final MovieRepository movieRepository;

    public MovieViewModel() {
        movieRepository = MovieRepository.getInstance();
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

    public void updateMovie(String movieId, MovieModel movie, String userId) {
        movieRepository.updateMovie(movieId, movie, userId, new MovieRepository.MovieOperationCallback() {
            @Override
            public void onSuccess() {
                operationSuccessLiveData.postValue(true);
            }

            @Override
            public void onError(String error) {
                errorLiveData.postValue(error);
                operationSuccessLiveData.postValue(false);
            }
        });
    }

    public void deleteMovie(String movieId, String userId) {
        movieRepository.deleteMovie(movieId, userId, new MovieRepository.MovieOperationCallback() {
            @Override
            public void onSuccess() {
                operationSuccessLiveData.postValue(true);
            }

            @Override
            public void onError(String error) {
                errorLiveData.postValue(error);
                operationSuccessLiveData.postValue(false);
            }
        });
    }

    // Expose LiveData for operation success status
    public LiveData<Boolean> getOperationSuccess() {
        return operationSuccessLiveData;
    }

    public void addMovie(MovieModel movie, String userId) {
        movieRepository.addMovie(movie, userId, new MovieRepository.MovieOperationCallback() {
            @Override
            public void onSuccess() {
                operationSuccessLiveData.postValue(true);
            }

            @Override
            public void onError(String error) {
                errorLiveData.postValue(error);
                operationSuccessLiveData.postValue(false);
            }
        });
    }
}

