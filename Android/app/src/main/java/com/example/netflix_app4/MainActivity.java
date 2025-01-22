package com.example.netflix_app4;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private RecyclerView categoriesRecyclerView;
    private CategoryAdapter categoryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        categoriesRecyclerView = findViewById(R.id.categoriesRecyclerView);
        categoriesRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        categoryAdapter = new CategoryAdapter(this, new ArrayList<>());
        categoriesRecyclerView.setAdapter(categoryAdapter);

        Button fetchButton = findViewById(R.id.fetchButton);
        fetchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchCategories();
            }
        });
    }

    private void fetchCategories() {
        Log.d("MainActivity", "Fetching categories...");
        MovieApiService apiService = RetrofitClient.getRetrofitInstance().create(MovieApiService.class);

        apiService.getCategories("678c10fe72b00e76a2d02581").enqueue(new Callback<CategoriesResponse>() {
            @Override
            public void onResponse(Call<CategoriesResponse> call, Response<CategoriesResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    CategoriesResponse categoriesResponse = response.body();

                    List<CategoryPromoted> promotedCategories = categoriesResponse.getPromotedCategories();
                    LastWatched lastWatched = categoriesResponse.getLastWatched();

                    Log.d("MainActivity", "Promoted Categories: " + promotedCategories);
                    Log.d("MainActivity", "Last Watched: " + lastWatched);

                    // Pass promoted categories to adapter
                    categoryAdapter.updateData(promotedCategories);

                    // Optionally handle lastWatched
                    if (lastWatched != null) {
                        Log.d("MainActivity", "Last watched category: " + lastWatched.getCategory());
                        Log.d("MainActivity", "Last watched movies: " + lastWatched.getMovies());
                    }
                } else {
                    Log.e("MainActivity", "Failed to fetch categories: " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<CategoriesResponse> call, Throwable t) {
                Log.e("MainActivity", "Error fetching categories: " + t.getMessage());
            }
        });
    }

    private void fetchMovieById(String movieId) {
        MovieApiService apiService = RetrofitClient.getRetrofitInstance().create(MovieApiService.class);
        apiService.getMovieById(movieId, "678c10fe72b00e76a2d02581").enqueue(new Callback<MovieModel>() {
            @Override
            public void onResponse(Call<MovieModel> call, Response<MovieModel> response) {
                if (response.isSuccessful() && response.body() != null) {
                    MovieModel movie = response.body();
                    // Handle the fetched movie (e.g., update UI or display details)
                } else {
                    Toast.makeText(MainActivity.this, "Failed to fetch movie", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<MovieModel> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}