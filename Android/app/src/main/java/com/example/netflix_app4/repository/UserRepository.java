package com.example.netflix_app4.repository;

import android.app.Application;
import android.util.Log;
import androidx.lifecycle.LiveData;

import com.example.netflix_app4.db.AppDatabase;
import com.example.netflix_app4.db.UserDao;
import com.example.netflix_app4.model.RegisterResponse;
import com.example.netflix_app4.model.TokenResponse;
import com.example.netflix_app4.db.User;
import com.example.netflix_app4.model.UserInfo;
import com.example.netflix_app4.network.ApiService;
import com.example.netflix_app4.network.Config;
import com.example.netflix_app4.network.RetrofitClient;
import com.example.netflix_app4.util.AppExecutors;
import com.example.netflix_app4.model.LoginRequest;
import com.example.netflix_app4.model.LoginResponse;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import androidx.annotation.NonNull;

import java.io.File;

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
                    // Response not successful, handle specific cases
                    if (response.code() == 401) {
                        Log.d(TAG, "onResponse: Invalid username or password");
                        callback.onError("Invalid username or password");
                    } else {
                        Log.d(TAG, "onResponse: Login failed, response not successful or body null");
                        callback.onError("Login failed due to server error");
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<LoginResponse> call, @NonNull Throwable t) {
                Log.e(TAG, "onFailure: Network error during login: " + t.getMessage());
                // If it's a network issue, provide a specific message
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    public void validateToken(String token, OnTokenValidationCallback callback) {
        apiService.validateToken("Bearer " + token).enqueue(new Callback<TokenResponse>() {
            @Override
            public void onResponse(Call<TokenResponse> call, Response<TokenResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    TokenResponse tokenResponse = response.body();
                    UserInfo userInfo = new UserInfo(
                            tokenResponse.getName(),
                            Config.getBaseUrl() + "/" + tokenResponse.getAvatar(),
                            tokenResponse.getUserId(),
                            token,
                            tokenResponse.isAdmin()
                    );
                    callback.onSuccess(userInfo);
                } else {
                    callback.onError("Token validation failed");
                }
            }

            @Override
            public void onFailure(Call<TokenResponse> call, Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    public interface OnTokenValidationCallback {
        void onSuccess(UserInfo userInfo);
        void onError(String error);
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

    public void register(String userName, String name, String email, String password,
                         File profilePicture, OnRegisterCallback callback) {
        Log.d(TAG, "register: Attempting registration for username: " + userName);

        // Create MultipartBody.Part from profile picture
        RequestBody requestFile = RequestBody.create(
                MediaType.parse("image/*"), profilePicture);
        MultipartBody.Part body = MultipartBody.Part.createFormData(
                "profilePicture", profilePicture.getName(), requestFile);

        // Create request bodies for text fields
        RequestBody userNameBody = RequestBody.create(MediaType.parse("text/plain"), userName);
        RequestBody nameBody = RequestBody.create(MediaType.parse("text/plain"), name);
        RequestBody emailBody = RequestBody.create(MediaType.parse("text/plain"), email);
        RequestBody passwordBody = RequestBody.create(MediaType.parse("text/plain"), password);

        apiService.register(userNameBody, nameBody, emailBody, passwordBody, body)
                .enqueue(new Callback<RegisterResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<RegisterResponse> call,
                                           @NonNull Response<RegisterResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            Log.d(TAG, "onResponse: Registration successful");
                            callback.onSuccess(response.body().getMessage());
                        } else {
                            Log.d(TAG, "onResponse: Registration failed");
                            callback.onError("Registration failed");
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<RegisterResponse> call,
                                          @NonNull Throwable t) {
                        Log.e(TAG, "onFailure: Network error during registration: " + t.getMessage());
                        callback.onError("Network error: " + t.getMessage());
                    }
                });
    }

    public interface OnRegisterCallback {
        void onSuccess(String message);
        void onError(String message);
    }
}
