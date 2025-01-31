package com.example.netflix_app4.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.netflix_app4.model.MovieModel;
import com.example.netflix_app4.repository.MovieRepository;

import java.util.List;

public class SearchViewModel extends ViewModel {
    private final MutableLiveData<List<MovieModel>> searchResults = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MovieRepository movieRepository;

    public SearchViewModel() {
        movieRepository = MovieRepository.getInstance();
    }

    public LiveData<List<MovieModel>> getSearchResults() {
        return searchResults;
    }

    public LiveData<String> getError() {
        return error;
    }

    public void searchMovies(String query, String userId) {
        movieRepository.searchMovies(query, userId, new MovieRepository.SearchCallback() {
            @Override
            public void onSuccess(List<MovieModel> movies) {
                searchResults.postValue(movies);
            }

            @Override
            public void onError(String errorMessage) {
                error.postValue(errorMessage);
            }
        });
    }
}