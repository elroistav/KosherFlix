package com.example.netflix_app4.viewmodel;

import android.app.Application;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.netflix_app4.db.User;
import com.example.netflix_app4.repository.UserRepository;

public class LoginViewModel extends AndroidViewModel {
    private static final String TAG = "LoginViewModel";

    private final UserRepository repository;
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final LiveData<User> loggedInUser;
    private final MutableLiveData<Boolean> loginResult = new MutableLiveData<>();
    private final MutableLiveData<String> tokenLiveData = new MutableLiveData<>();

    public LoginViewModel(@NonNull Application application) {
        super(application);
        Log.d(TAG, "LoginViewModel: ViewModel initialized");
        repository = new UserRepository(application);
        loggedInUser = repository.getLoggedInUser();
    }

    public void login(String username, String password) {
        Log.d(TAG, "login: Attempting to login with username: " + username);
        repository.login(username, password, new UserRepository.OnLoginCallback() {
            @Override
            public void onSuccess(String token) {
                Log.d(TAG, "onSuccess: Login successful, token: " + token);
                tokenLiveData.postValue(token);
                loginResult.postValue(true);
            }

            @Override
            public void onError(String message) {
                Log.d(TAG, "onError: Login failed with error: " + message);
                error.postValue(message);
                loginResult.postValue(false);
            }
        });
    }

    public LiveData<Boolean> getLoginResult() {
        Log.d(TAG, "getLoginResult: Returning login result LiveData");
        return loginResult;
    }

    public LiveData<User> getLoggedInUser() {
        Log.d(TAG, "getLoggedInUser: Returning logged-in user LiveData");
        return loggedInUser;
    }

    public LiveData<String> getToken() {
        return tokenLiveData;
    }

    public LiveData<String> getError() {
        Log.d(TAG, "getError: Returning error LiveData");
        return error;
    }
}
