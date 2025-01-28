package com.example.netflix_app4.repository;

import android.app.Application;
import android.util.Log;
import androidx.lifecycle.LiveData;

import com.example.netflix_app4.db.AppDatabase;
import com.example.netflix_app4.db.UserDao;
import com.example.netflix_app4.model.User;
import com.example.netflix_app4.network.ApiClient;
import com.example.netflix_app4.network.ApiService;
import com.example.netflix_app4.network.RetrofitClient;
import com.example.netflix_app4.util.AppExecutors;
import com.example.netflix_app4.model.LoginRequest;
import com.example.netflix_app4.model.LoginResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import androidx.annotation.NonNull;

public class UserRepository {
    private static final String TAG = "UserRepository";

    private final UserDao userDao;
    private final ApiService apiService;
    private final LiveData<User> loggedInUser;

    public UserRepository(Application application) {
        Log.d(TAG, "UserRepository: Initializing repository");
        AppDatabase db = AppDatabase.getDatabase(application);
        userDao = db.userDao();
        Retrofit retrofit = RetrofitClient.getRetrofitInstance();
        apiService = retrofit.create(ApiService.class);
        loggedInUser = userDao.getLoggedInUser();
    }

    public void login(String username, String password, OnLoginCallback callback) {
        Log.d(TAG, "login: Attempting login for username: " + username);
        LoginRequest loginRequest = new LoginRequest(username, password);
        apiService.login(loginRequest).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<LoginResponse> call, @NonNull Response<LoginResponse> response) {
                Log.d(TAG, "onResponse: Received response for login request");
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "onResponse: Login successful, token received");
                    String token = response.body().getToken();

                    // Save to Room database
                    AppExecutors.getInstance().diskIO().execute(() -> {
                        Log.d(TAG, "onResponse: Saving user to database");
                        User user = new User(username, password);
                        user.setToken(token);
                        user.setLoggedIn(true);
                        userDao.insert(user);
                        Log.d(TAG, "onResponse: User saved successfully");
                        callback.onSuccess(token);
                    });
                } else {
                    Log.d(TAG, "onResponse: Login failed, response not successful or body null");
                    callback.onError("Login failed");
                }
            }

            @Override
            public void onFailure(@NonNull Call<LoginResponse> call, @NonNull Throwable t) {
                Log.e(TAG, "onFailure: Network error during login: " + t.getMessage());
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    public void logout(User user) {
        if (user != null) {
            Log.d(TAG, "logout: Logging out user: " );
            AppExecutors.getInstance().diskIO().execute(() -> {
                user.setLoggedIn(false);
                user.setToken(null);
                userDao.update(user);
                Log.d(TAG, "logout: User logged out successfully");
            });
        } else {
            Log.d(TAG, "logout: No user to log out");
        }
    }

    public LiveData<User> getLoggedInUser() {
        Log.d(TAG, "getLoggedInUser: Fetching logged-in user");
        return loggedInUser;
    }

    public interface OnLoginCallback {
        void onSuccess(String token);
        void onError(String message);
    }
}
