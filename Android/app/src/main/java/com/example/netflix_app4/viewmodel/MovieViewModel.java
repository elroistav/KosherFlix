package com.example.netflix_app4.viewmodel;

import android.content.Context;
import android.net.Uri;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.netflix_app4.model.MovieModel;
import com.example.netflix_app4.repository.MovieRepository;

import java.util.List;

public class MovieViewModel extends ViewModel {
    private final MutableLiveData<MovieModel> movieLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> operationSuccessLiveData = new MutableLiveData<>();
    private final MovieRepository movieRepository;

    public MovieViewModel() {
        movieRepository = MovieRepository.getInstance();
    }

    // Original methods
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



    // New add movie with files
    public void addMovie(MovieModel movie, List<String> categories,Uri thumbnailUri, Uri videoUri, String userId, Context context) {
        movieRepository.addMovie(movie, categories, thumbnailUri, videoUri, userId, context, new MovieRepository.MovieCallback() {
            @Override
            public void onSuccess(MovieModel movie) {
                movieLiveData.postValue(movie);
                operationSuccessLiveData.postValue(true);
            }

            @Override
            public void onError(String error) {
                errorLiveData.postValue(error);
                operationSuccessLiveData.postValue(false);
            }
        });
    }

    public void updateMovie(String movieId, MovieModel movie, List<String> categories, Uri thumbnailUri, Uri videoUri, String userId, Context context) {
        movieRepository.updateMovie(movieId, movie, categories, thumbnailUri, videoUri, userId, context,
                new MovieRepository.MovieCallback() {
                    @Override
                    public void onSuccess(MovieModel updatedMovie) {
                        movieLiveData.postValue(updatedMovie);
                        operationSuccessLiveData.postValue(true);
                    }

                    @Override
                    public void onError(String error) {
                        errorLiveData.postValue(error);
                        operationSuccessLiveData.postValue(false);
                    }
                });
    }

    // New update movie with files
    public void updateMovieWithFiles(String movieId, MovieModel movie, Uri thumbnailUri, Uri videoUri, String userId, Context context) {
        /*movieRepository.updateMovie(movieId, movie, thumbnailUri, videoUri, userId, context, new MovieRepository.MovieCallback() {
            @Override
            public void onSuccess(MovieModel movie) {
                movieLiveData.postValue(movie);
                operationSuccessLiveData.postValue(true);
            }

            @Override
            public void onError(String error) {
                errorLiveData.postValue(error);
                operationSuccessLiveData.postValue(false);
            }
        });*/
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

    // Getters for LiveData
    public LiveData<MovieModel> getMovieLiveData() {
        return movieLiveData;
    }

    public LiveData<String> getErrorLiveData() {
        return errorLiveData;
    }

    public LiveData<Boolean> getOperationSuccess() {
        return operationSuccessLiveData;
    }
}