package com.example.netflix_app4.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.netflix_app4.model.CategoriesResponse;
import com.example.netflix_app4.model.CategoryPromoted;
import com.example.netflix_app4.model.LastWatched;
import com.example.netflix_app4.repository.CategoryRepository;

import java.util.List;

public class CategoryViewModel extends ViewModel {
    private final MutableLiveData<List<CategoryPromoted>> promotedCategoriesLiveData = new MutableLiveData<>();
    private final MutableLiveData<LastWatched> lastWatchedLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();

    private final CategoryRepository categoryRepository;

    public CategoryViewModel() {
        categoryRepository = new CategoryRepository();
    }

    // Expose LiveData for promoted categories
    public LiveData<List<CategoryPromoted>> getPromotedCategoriesLiveData() {
        return promotedCategoriesLiveData;
    }

    // Expose LiveData for last watched
    public LiveData<LastWatched> getLastWatchedLiveData() {
        return lastWatchedLiveData;
    }

    // Expose LiveData for errors
    public LiveData<String> getErrorLiveData() {
        return errorLiveData;
    }

    // Fetch categories and update LiveData
    public void fetchCategories(String userId) {
        categoryRepository.getCategories(userId, new CategoryRepository.CategoryCallback() {
            @Override
            public void onSuccess(CategoriesResponse response) {
                promotedCategoriesLiveData.postValue(response.getPromotedCategories());
                lastWatchedLiveData.postValue(response.getLastWatched());
            }

            @Override
            public void onError(String error) {
                errorLiveData.postValue(error);
            }
        });
    }
}
