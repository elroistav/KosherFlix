package com.example.netflix_app4.repository;

import com.example.netflix_app4.network.MovieApiService;
import com.example.netflix_app4.network.RetrofitClient;
import com.example.netflix_app4.model.CategoriesResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoryRepository {
    private final MovieApiService apiService;

    public CategoryRepository() {
        apiService = RetrofitClient.getRetrofitInstance().create(MovieApiService.class);
    }

    public void getCategories(String userId, CategoryCallback callback) {
        apiService.getCategories(userId).enqueue(new Callback<CategoriesResponse>() {
            @Override
            public void onResponse(Call<CategoriesResponse> call, Response<CategoriesResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Failed to fetch categories.");
                }
            }

            @Override
            public void onFailure(Call<CategoriesResponse> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public interface CategoryCallback {
        void onSuccess(CategoriesResponse response);
        void onError(String error);
    }
}

