package com.example.netflix_app4.repository;

import androidx.annotation.NonNull;
import com.example.netflix_app4.model.LoginRequest;
import com.example.netflix_app4.model.LoginResponse;
import com.example.netflix_app4.network.ApiService;
import com.example.netflix_app4.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginRepository {
    private final ApiService apiService;

    public LoginRepository() {
        apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);
    }

    public void login(LoginRequest loginRequest, OnLoginCallback callback) {
        apiService.login(loginRequest).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<LoginResponse> call, @NonNull Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body().getToken());
                } else {
                    callback.onError("Login failed - Invalid credentials");
                }
            }

            @Override
            public void onFailure(@NonNull Call<LoginResponse> call, @NonNull Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    public interface OnLoginCallback {
        void onSuccess(String token);
        void onError(String message);
    }
}