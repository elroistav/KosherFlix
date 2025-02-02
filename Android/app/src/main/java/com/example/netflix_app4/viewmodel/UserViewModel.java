package com.example.netflix_app4.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.netflix_app4.db.User;
import com.example.netflix_app4.repository.UserRepository;

public class UserViewModel extends AndroidViewModel {
    private final UserRepository repository;
    private final LiveData<User> currentUser;

    public UserViewModel(@NonNull Application application) {
        super(application);
        repository = new UserRepository(application);
        currentUser = repository.getLoggedInUser();
    }

    public LiveData<User> getUser() {
        return currentUser;
    }

    public void logout() {
        repository.logout(currentUser.getValue());
    }
}