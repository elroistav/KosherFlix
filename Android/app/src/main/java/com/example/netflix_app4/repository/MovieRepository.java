package com.example.netflix_app4.repository;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;

import com.example.netflix_app4.model.CategoriesListResponse;
import com.example.netflix_app4.model.CategoryModel;
import com.example.netflix_app4.model.MovieModel;
import com.example.netflix_app4.db.MovieEntity;
import com.example.netflix_app4.model.CategoriesResponse;
import com.example.netflix_app4.network.MovieApiService;
import com.example.netflix_app4.network.RetrofitClient;

import org.json.JSONArray;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.Map;
import java.util.stream.Collectors;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MovieRepository {
    private static MovieRepository instance;
    private final MovieApiService movieApiService;

    public MovieRepository() {
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

    // Get categories
    public interface CategoryConversionCallback {
        void onSuccess(List<String> categoryIds);
        void onError(String error);
    }

    public interface MovieDetailsCallback {
        void onSuccess(List<MovieEntity> movies);
        void onError(String error);
    }

    public void getMovieDetails(List<String> movieIds, String userId, MovieDetailsCallback callback) {
        List<MovieEntity> movieEntities = new ArrayList<>();
        AtomicInteger counter = new AtomicInteger(movieIds.size()); // Track remaining requests
        AtomicBoolean errorOccurred = new AtomicBoolean(false);

        for (String movieId : movieIds) {
            movieApiService.getMovieById(movieId, userId).enqueue(new Callback<MovieModel>() {
                @Override
                public void onResponse(Call<MovieModel> call, Response<MovieModel> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        MovieModel movie = response.body();
                        movieEntities.add(new MovieEntity(movie.getId(), movie.getTitle(), movie.getThumbnail()));
                    }

                    if (counter.decrementAndGet() == 0 && !errorOccurred.get()) {
                        callback.onSuccess(movieEntities); // Call success when all requests are done
                    }
                }

                @Override
                public void onFailure(Call<MovieModel> call, Throwable t) {
                    errorOccurred.set(true);
                    callback.onError("Failed to fetch movie details: " + t.getMessage());
                }
            });
        }
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

    public void addMovie(MovieModel movie, List<String> categories, Uri thumbnailUri, Uri videoUri, String userId, Context context, MovieCallback callback) {
        try {
            // Convert movie data to RequestBody objects
            RequestBody title = RequestBody.create(MediaType.parse("text/plain"), movie.getTitle());
            RequestBody description = RequestBody.create(MediaType.parse("text/plain"), movie.getDescription());
            RequestBody rating = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(movie.getRating()));
            RequestBody length = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(movie.getLength()));
            RequestBody director = RequestBody.create(MediaType.parse("text/plain"), movie.getDirector());
            RequestBody releaseDate = RequestBody.create(MediaType.parse("text/plain"), movie.getReleaseDate());
            RequestBody language = RequestBody.create(MediaType.parse("text/plain"), movie.getLanguage());

            // Convert categories list to JSON format
            String categoriesJson = new Gson().toJson(categories);
            RequestBody categoriesBody = RequestBody.create(MediaType.parse("text/plain"), categoriesJson);


            // Convert URIs to MultipartBody.Parts
            MultipartBody.Part thumbnailPart = uriToMultipartBodyPart(context, thumbnailUri, "thumbnail");
            MultipartBody.Part videoPart = uriToMultipartBodyPart(context, videoUri, "videoUrl");

            movieApiService.addMovie(
                    title, description, rating, length, director, releaseDate, language,
                    categoriesBody, thumbnailPart, videoPart, userId
            ).enqueue(new Callback<MovieModel>() {
                @Override
                public void onResponse(Call<MovieModel> call, Response<MovieModel> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        callback.onSuccess(response.body());
                    } else {
                        callback.onError("Failed to add movie");
                    }
                }

                @Override
                public void onFailure(Call<MovieModel> call, Throwable t) {
                    callback.onError(t.getMessage());
                }
            });
        } catch (IOException e) {
            callback.onError("Error processing files: " + e.getMessage());
        }
    }

    private MultipartBody.Part uriToMultipartBodyPart(Context context, Uri uri, String partName) throws IOException {
        ContentResolver resolver = context.getContentResolver();
        String mimeType = resolver.getType(uri);
        String fileName = getFileName(context, uri);

        InputStream inputStream = resolver.openInputStream(uri);
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        int nRead;
        byte[] data = new byte[16384]; // 16KB buffer

        while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }

        buffer.flush();
        byte[] bytes = buffer.toByteArray();

        RequestBody requestFile = RequestBody.create(MediaType.parse(mimeType), bytes);
        return MultipartBody.Part.createFormData(partName, fileName, requestFile);
    }

    private String getFileName(Context context, Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = context.getContentResolver()
                    .query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (index != -1) {
                        result = cursor.getString(index);
                    }
                }
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    // Similarly for updateMovie...
    public void updateMovie(String movieId, MovieModel movie, Uri thumbnailUri, Uri videoUri,
                            String userId, Context context, MovieCallback callback) {
        try {
            RequestBody title = RequestBody.create(MediaType.parse("text/plain"), movie.getTitle());
            RequestBody description = RequestBody.create(MediaType.parse("text/plain"), movie.getDescription());
            RequestBody rating = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(movie.getRating()));
            RequestBody length = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(movie.getLength()));
            RequestBody director = RequestBody.create(MediaType.parse("text/plain"), movie.getDirector());
            RequestBody releaseDate = RequestBody.create(MediaType.parse("text/plain"), movie.getReleaseDate());
            RequestBody language = RequestBody.create(MediaType.parse("text/plain"), movie.getLanguage());

            MultipartBody.Part thumbnailPart = null;
            MultipartBody.Part videoPart = null;

            // Only convert URIs if they are provided (for optional file updates)
            if (thumbnailUri != null) {
                thumbnailPart = uriToMultipartBodyPart(context, thumbnailUri, "thumbnail");
            }
            if (videoUri != null) {
                videoPart = uriToMultipartBodyPart(context, videoUri, "video");
            }

            movieApiService.updateMovie(
                    movieId, title, description, rating, length, director, releaseDate, language,
                    thumbnailPart, videoPart, userId
            ).enqueue(new Callback<MovieModel>() {
                @Override
                public void onResponse(Call<MovieModel> call, Response<MovieModel> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        callback.onSuccess(response.body());
                    } else {
                        callback.onError("Failed to update movie");
                    }
                }

                @Override
                public void onFailure(Call<MovieModel> call, Throwable t) {
                    callback.onError(t.getMessage());
                }
            });
        } catch (IOException e) {
            callback.onError("Error processing files: " + e.getMessage());
        }
    }
}