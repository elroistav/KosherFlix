package com.example.netflix_app4.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import android.app.Application;
import androidx.lifecycle.AndroidViewModel;

import com.example.netflix_app4.db.AppDatabase;
import com.example.netflix_app4.db.CategoryDao;
import com.example.netflix_app4.db.MovieDao;
import com.example.netflix_app4.db.MovieEntity;
import com.example.netflix_app4.model.CategoryPromoted;
import com.example.netflix_app4.model.LastWatched;
import com.example.netflix_app4.model.MovieModel;
import com.example.netflix_app4.model.UserInfo;
import com.example.netflix_app4.repository.MovieRepository;
import com.example.netflix_app4.repository.UserRepository;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HomeScreenViewModel extends AndroidViewModel {
    private static final String TAG = "HomeScreenViewModel";

    private final CategoryViewModel categoryViewModel;
    private final MovieViewModel movieViewModel;
    private final UserRepository userRepository;

    // Original LiveData
    private final MutableLiveData<String> userId;

    // New LiveData for token validation
    private final MutableLiveData<UserInfo> userInfoLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> validationErrorLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoadingLiveData = new MutableLiveData<>();
    private final MovieDao movieDao;
    private final CategoryDao categoryDao;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public HomeScreenViewModel(@NonNull Application application) {
        super(application);
        AppDatabase db = AppDatabase.getDatabase(application);
        movieDao = db.movieDao();
        categoryDao = db.categoryDao();
        categoryViewModel = new CategoryViewModel(application);
        movieViewModel = new MovieViewModel();
        userRepository = new UserRepository(application);
        userId = new MutableLiveData<>();
    }

    public List<MovieEntity> getMoviesFromRoom() {
        return movieDao.getAllMovies();
    }

    public void insertMovies(List<MovieEntity> movies) {
        executorService.execute(() -> movieDao.insertMovies(movies));
    }

    public void validateToken(String token) {
        isLoadingLiveData.setValue(true);
        userRepository.validateToken(token, new UserRepository.OnTokenValidationCallback() {
            @Override
            public void onSuccess(UserInfo userInfo) {
                userInfoLiveData.postValue(userInfo);
                isLoadingLiveData.postValue(false);
                // After successful validation, set userId and fetch initial data
                setUserId(userInfo.getUserId());
            }

            @Override
            public void onError(String error) {
                validationErrorLiveData.postValue(error);
                isLoadingLiveData.postValue(false);
            }
        });
    }

    // Original methods
    public void setUserId(String id) {
        userId.setValue(id);
        categoryViewModel.fetchCategories(id);
    }

    public LiveData<List<CategoryPromoted>> getPromotedCategoriesLiveData() {
        return categoryViewModel.getPromotedCategoriesLiveData();
    }

    public LiveData<LastWatched> getLastWatchedLiveData() {
        return categoryViewModel.getLastWatchedLiveData();
    }

    public LiveData<MovieModel> getRandomMovieLiveData() {
        return movieViewModel.getMovieLiveData();
    }

    public LiveData<String> getErrorLiveData() {
        return movieViewModel.getErrorLiveData();
    }

    public void fetchRandomMovie(String movieId) {
        String id = userId.getValue();
        if (id != null) {
            movieViewModel.fetchMovieById(movieId, id);
        }
    }

    // New getters for token validation
    public LiveData<UserInfo> getUserInfo() {
        return userInfoLiveData;
    }

    public LiveData<String> getValidationError() {
        return validationErrorLiveData;
    }

    public LiveData<Boolean> isLoading() {
        return isLoadingLiveData;
    }
}