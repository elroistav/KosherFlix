package com.example.netflix_app4.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.netflix_app4.repository.UserRepository;

import java.io.File;

public class RegisterViewModel extends AndroidViewModel {
    private static final String TAG = "RegisterViewModel";

    private final UserRepository repository;
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<Boolean> registrationResult = new MutableLiveData<>();

    public RegisterViewModel(@NonNull Application application) {
        super(application);
        repository = new UserRepository(application);
    }

    public void register(String userName, String name, String email,
                         String password, File profilePicture) {
        repository.register(userName, name, email, password, profilePicture,
                new UserRepository.OnRegisterCallback() {
                    @Override
                    public void onSuccess(String message) {
                        registrationResult.postValue(true);
                    }

                    @Override
                    public void onError(String message) {
                        error.postValue(message);
                        registrationResult.postValue(false);
                    }
                });
    }

    public LiveData<Boolean> getRegistrationResult() {
        return registrationResult;
    }

    public LiveData<String> getError() {
        return error;
    }
}
