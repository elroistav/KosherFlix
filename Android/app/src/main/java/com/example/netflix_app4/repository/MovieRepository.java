package com.example.netflix_app4.repository;

import com.example.netflix_app4.network.MovieApiService;
import com.example.netflix_app4.network.RetrofitClient;
import com.example.netflix_app4.model.MovieModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieRepository {
    private final MovieApiService apiService;

    public MovieRepository() {
        apiService = RetrofitClient.getRetrofitInstance().create(MovieApiService.class);
    }

    public void getMovieById(String movieId, String userId, MovieCallback callback) {
        apiService.getMovieById(movieId, userId).enqueue(new Callback<MovieModel>() {
            @Override
            public void onResponse(Call<MovieModel> call, Response<MovieModel> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Failed to fetch movie.");
                }
            }

            @Override
            public void onFailure(Call<MovieModel> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public void searchMovies(String query, String userId, SearchCallback callback) {
        apiService.searchMovies(query, userId).enqueue(new Callback<List<MovieModel>>() {
            @Override
            public void onResponse(Call<List<MovieModel>> call, Response<List<MovieModel>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Failed to search movies.");
                }
            }

            @Override
            public void onFailure(Call<List<MovieModel>> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public interface MovieCallback {
        void onSuccess(MovieModel movie);
        void onError(String error);
    }

    public interface SearchCallback {
        void onSuccess(List<MovieModel> movies);
        void onError(String error);
    }
}