package com.example.netflix_app4.repository;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;

import com.example.netflix_app4.model.CategoriesListResponse;
import com.example.netflix_app4.model.CategoryModel;
import com.example.netflix_app4.model.MovieModel;
import com.example.netflix_app4.network.MovieApiService;
import com.example.netflix_app4.network.RetrofitClient;

import org.json.JSONArray;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

    private MovieRepository() {
        Retrofit retrofit = RetrofitClient.getRetrofitInstance();
        this.movieApiService = retrofit.create(MovieApiService.class);
    }

    public static synchronized MovieRepository getInstance() {
        if (instance == null) {
            instance = new MovieRepository();
        }
        return instance;
    }

    // Callback interfaces
    public interface CategoryCallback {
        void onSuccess(Map<String, List<Map<String, Object>>> response);
        void onError(String error);
    }



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

    /**
     * Converts a list of category names to their corresponding IDs
     * @param categoryNames List of category names to convert
     * @param userId User ID for API authentication
     * @param callback Callback to handle the result
     */
    public void convertCategoryNamesToIds(List<String> categoryNames, String userId, CategoryConversionCallback callback) {
        // First fetch all categories
        movieApiService.getAllCategories(userId).enqueue(new Callback<CategoriesListResponse>() {
            @Override
            public void onResponse(Call<CategoriesListResponse> call, Response<CategoriesListResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<CategoryModel> allCategories = response.body().getCategories();
                    List<String> categoryIds = new ArrayList<>();

                    // For each category name, find matching category and get its ID
                    for (String name : categoryNames) {
                        String normalizedName = name.trim().toLowerCase();

                        for (CategoryModel category : allCategories) {
                            if (category.getName().toLowerCase().equals(normalizedName)) {
                                categoryIds.add(category.getId());
                                break;
                            }
                        }
                    }

                    if (categoryIds.isEmpty()) {
                        callback.onError("No matching categories found");
                    } else {
                        callback.onSuccess(categoryIds);
                    }
                } else {
                    callback.onError("Failed to fetch categories");
                }
            }

            @Override
            public void onFailure(Call<CategoriesListResponse> call, Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    // Updated addMovie method using the new conversion
    public void addMovie(MovieModel movie, List<String> categoryNames, Uri thumbnailUri, Uri videoUri,
                         String userId, Context context, MovieCallback callback) {

        // First convert category names to IDs
        convertCategoryNamesToIds(categoryNames, userId, new CategoryConversionCallback() {
            @Override
            public void onSuccess(List<String> categoryIds) {
                try {
                    // Create request bodies for all fields
                    RequestBody title = RequestBody.create(MediaType.parse("text/plain"), movie.getTitle());
                    RequestBody description = RequestBody.create(MediaType.parse("text/plain"), movie.getDescription());
                    RequestBody rating = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(movie.getRating()));
                    RequestBody length = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(movie.getLength()));
                    RequestBody director = RequestBody.create(MediaType.parse("text/plain"), movie.getDirector());
                    RequestBody releaseDate = RequestBody.create(MediaType.parse("text/plain"), movie.getReleaseDate());
                    RequestBody language = RequestBody.create(MediaType.parse("text/plain"), movie.getLanguage());

                    // Convert URIs to MultipartBody.Parts
                    MultipartBody.Part thumbnailPart = uriToMultipartBodyPart(context, thumbnailUri, "thumbnail");
                    MultipartBody.Part videoPart = uriToMultipartBodyPart(context, videoUri, "videoUrl");

                    // Convert categoryIds list to a JSON array string
                    String categoriesJson = new JSONArray(categoryIds).toString();
                    RequestBody categoriesBody = RequestBody.create(
                            MediaType.parse("application/json"),
                            categoriesJson
                    );

                    // Make the API call
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

            @Override
            public void onError(String error) {
                callback.onError("Failed to convert categories: " + error);
            }
        });
    }

    // Get movie by ID
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

    // Search movies
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

    // Helper method to convert URI to MultipartBody.Part
    private MultipartBody.Part uriToMultipartBodyPart(Context context, Uri uri, String partName) throws IOException {
        ContentResolver resolver = context.getContentResolver();
        String mimeType = resolver.getType(uri);
        String fileName = getFileName(context, uri);

        InputStream inputStream = resolver.openInputStream(uri);
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        int nRead;
        byte[] data = new byte[16384];

        while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }

        buffer.flush();
        byte[] bytes = buffer.toByteArray();

        RequestBody requestFile = RequestBody.create(MediaType.parse(mimeType), bytes);
        return MultipartBody.Part.createFormData(partName, fileName, requestFile);
    }

    // Helper method to get filename from URI
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
}