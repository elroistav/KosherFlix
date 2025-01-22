package com.example.netflix_app4;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {
    private Context context;
    private List<CategoryPromoted> categoryList;

    public CategoryAdapter(Context context, List<CategoryPromoted> categoryList) {
        this.context = context;
        this.categoryList = categoryList != null ? categoryList : new ArrayList<>();
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.category_item, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        CategoryPromoted category = categoryList.get(position);
        holder.categoryTitle.setText(category.getCategory());

        // Fetch movie details for movie IDs and pass them to MovieAdapter
        List<String> movieIds = category.getMovies();

        // Check if there are any movie IDs to process
        if (movieIds == null || movieIds.isEmpty()) {
            Log.d("CategoryAdapter", "No movies to fetch for category " + category.getCategory());
            return;
        }

        Log.d("CategoryAdapter", "Fetching movie details for category: " + category.getCategory());
        Log.d("CategoryAdapter", "Movie IDs: " + movieIds);

        List<MovieModel> movieDetails = new ArrayList<>();

        for (String movieId : movieIds) {
            MovieApiService apiService = RetrofitClient.getRetrofitInstance().create(MovieApiService.class);
            apiService.getMovieById(movieId, "678c10fe72b00e76a2d02581").enqueue(new Callback<MovieModel>() {
                @Override
                public void onResponse(Call<MovieModel> call, Response<MovieModel> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        movieDetails.add(response.body());
                        Log.d("CategoryAdapter", "Fetched movie: " + response.body().getTitle());
                    } else {
                        Log.e("CategoryAdapter", "Failed to fetch movie details: Response was not successful.");
                    }

                    // Update RecyclerView when all details are fetched
                    if (movieDetails.size() == movieIds.size()) {
                        Log.d("CategoryAdapter", "All movies fetched for category " + category.getCategory());
                        Log.d("CategoryAdapter", "Fetched movies: " + movieDetails);

                        // Initialize and set the MovieAdapter
                        MovieAdapter movieAdapter = new MovieAdapter(context, movieDetails);
                        holder.moviesRecyclerView.setAdapter(movieAdapter);
                        holder.moviesRecyclerView.setLayoutManager(
                                new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                        );
                    }
                }

                @Override
                public void onFailure(Call<MovieModel> call, Throwable t) {
                    Log.e("CategoryAdapter", "Failed to fetch movie details for ID " + movieId + ": " + t.getMessage());
                }
            });
        }
    }


    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public void updateData(List<CategoryPromoted> newCategories) {
        categoryList.clear();
        if (newCategories != null) {
            categoryList.addAll(newCategories);
        }
        notifyDataSetChanged(); // Refresh the RecyclerView
    }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView categoryTitle;
        RecyclerView moviesRecyclerView;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryTitle = itemView.findViewById(R.id.categoryTitle);
            moviesRecyclerView = itemView.findViewById(R.id.moviesRecyclerView);
        }
    }
}



