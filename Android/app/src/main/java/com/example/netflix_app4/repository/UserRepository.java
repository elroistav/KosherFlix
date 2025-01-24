package com.example.netflix_app4.repository;

import android.app.Application;
import androidx.lifecycle.LiveData;

import com.example.netflix_app4.db.AppDatabase;
import com.example.netflix_app4.db.UserDao;
import com.example.netflix_app4.model.User;
import com.example.netflix_app4.network.ApiClient;
import com.example.netflix_app4.network.ApiService;
import com.example.netflix_app4.util.AppExecutors;
import com.example.netflix_app4.model.LoginRequest;
import com.example.netflix_app4.model.LoginResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import androidx.annotation.NonNull;

public class UserRepository {
    private final UserDao userDao;
    private final ApiService apiService;
    private final LiveData<User> loggedInUser;

    public UserRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        userDao = db.userDao();
        apiService = ApiClient.getInstance().create(ApiService.class);
        loggedInUser = userDao.getLoggedInUser();
    }

    public void login(String username, String password, OnLoginCallback callback) {
        LoginRequest loginRequest = new LoginRequest(username, password);
        apiService.login(loginRequest).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<LoginResponse> call, @NonNull Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String token = response.body().getToken();
                    // Save to Room database
                    AppExecutors.getInstance().diskIO().execute(() -> {
                        User user = new User(username, password);
                        user.setToken(token);
                        user.setLoggedIn(true);
                        userDao.insert(user);
                        callback.onSuccess(token);
                    });
                } else {
                    callback.onError("Login failed");
                }
            }

            @Override
            public void onFailure(@NonNull Call<LoginResponse> call, @NonNull Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    public void logout(User user) {
        if (user != null) {
            AppExecutors.getInstance().diskIO().execute(() -> {
                user.setLoggedIn(false);
                user.setToken(null);
                userDao.update(user);
            });
        }
    }

    public LiveData<User> getLoggedInUser() {
        return loggedInUser;
    }

    public interface OnLoginCallback {
        void onSuccess(String token);
        void onError(String message);
    }
}