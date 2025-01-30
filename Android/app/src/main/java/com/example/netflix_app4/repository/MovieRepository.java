package com.example.netflix_app4.repository;
import com.example.netflix_app4.model.CategoriesResponse;
import com.example.netflix_app4.network.MovieApiService;
import com.example.netflix_app4.network.RetrofitClient;
import com.example.netflix_app4.model.MovieModel;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MovieRepository {
    private static MovieRepository instance;
    private final MovieApiService movieApiService;

    private MovieRepository() {
        Retrofit retrofit = RetrofitClient.getRetrofitInstance();
        this.movieApiService = retrofit.create(MovieApiService.class);
    }

    // Singleton pattern to ensure only one instance of the repository
    public static synchronized MovieRepository getInstance() {
        if (instance == null) {
            instance = new MovieRepository();
        }
        return instance;
    }

    // Fetch categories using a callback
    public void getCategories(String userId, CategoryCallback callback) {
        movieApiService.getCategories(userId).enqueue(new Callback<CategoriesResponse>() {
            @Override
            public void onResponse(Call<CategoriesResponse> call, Response<CategoriesResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Failed to fetch categories.");
                }
            }

            @Override
            public void onFailure(Call<CategoriesResponse> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    // Fetch movie details using a callback
    public void getMovieById(String movieId, String userId, MovieCallback callback) {
        movieApiService.getMovieById(movieId, userId).enqueue(new Callback<MovieModel>() {
            @Override
            public void onResponse(Call<MovieModel> call, Response<MovieModel> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Failed to fetch movie details.");
                }
            }

            @Override
            public void onFailure(Call<MovieModel> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public void searchMovies(String query, String userId, SearchCallback callback) {
        movieApiService.searchMovies(query, userId).enqueue(new Callback<List<MovieModel>>() {
            @Override
            public void onResponse(Call<List<MovieModel>> call, Response<List<MovieModel>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Failed to search movies.");
                }
            }

            @Override
            public void onFailure(Call<List<MovieModel>> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    // Category callback interface
    public interface CategoryCallback {
        void onSuccess(CategoriesResponse response);
        void onError(String error);
    }

    // Movie callback interface
    public interface MovieCallback {
        void onSuccess(MovieModel movie);
        void onError(String error);
    }

    public interface SearchCallback {
        void onSuccess(List<MovieModel> movies);
        void onError(String error);
    }

    public interface MovieOperationCallback {
        void onSuccess();
        void onError(String error);
    }

    public void updateMovie(String movieId, MovieModel movie, String userId, MovieOperationCallback callback) {
        movieApiService.updateMovie(movieId, movie, userId).enqueue(new Callback<MovieModel>() {
            @Override
            public void onResponse(Call<MovieModel> call, Response<MovieModel> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess();
                } else {
                    callback.onError("Failed to update movie");
                }
            }

            @Override
            public void onFailure(Call<MovieModel> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public void deleteMovie(String movieId, String userId, MovieOperationCallback callback) {
        movieApiService.deleteMovie(movieId, userId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess();
                } else {
                    callback.onError("Failed to delete movie");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public void addMovie(MovieModel movie, String userId, MovieOperationCallback callback) {
        movieApiService.addMovie(movie, userId).enqueue(new Callback<MovieModel>() {
            @Override
            public void onResponse(Call<MovieModel> call, Response<MovieModel> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess();
                } else {
                    callback.onError("Failed to add movie");
                }
            }

            @Override
            public void onFailure(Call<MovieModel> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }
}