package com.example.netflix_app4.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.netflix_app4.model.User;
import com.example.netflix_app4.repository.UserRepository;

public class LoginViewModel extends AndroidViewModel {
    private final UserRepository repository;
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final LiveData<User> loggedInUser;
    private final MutableLiveData<Boolean> loginResult = new MutableLiveData<>();

    public LoginViewModel(@NonNull Application application) {
        super(application);
        repository = new UserRepository(application);
        loggedInUser = repository.getLoggedInUser();
    }

    public void login(String username, String password) {
        repository.login(username, password, new UserRepository.OnLoginCallback() {
            @Override
            public void onSuccess(String token) {
                loginResult.postValue(true);
            }

            @Override
            public void onError(String message) {
                error.postValue(message);
                loginResult.postValue(false);
            }
        });
    }

    public LiveData<Boolean> getLoginResult() {
        return loginResult;
    }

    public LiveData<User> getLoggedInUser() {
        return loggedInUser;
    }

    public LiveData<String> getError() {
        return error;
    }
}