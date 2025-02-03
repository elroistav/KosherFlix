package com.example.netflix_app4.view;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.netflix_app4.model.CategoryModel;
import com.example.netflix_app4.model.UserInfo;
import com.example.netflix_app4.network.MovieApiService;
import com.example.netflix_app4.R;
import com.example.netflix_app4.network.RetrofitClient;
import com.example.netflix_app4.model.CategoryPromoted;
import com.example.netflix_app4.model.MovieModel;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {
    private Context context;
    private List<Object> categories;
    private String userId;
    private final UserInfo userInfo;

    public CategoryAdapter(Context context, List<?> categories, UserInfo userInfo) {
        this.context = context;
        this.categories = new ArrayList<>();
        if (categories != null) {
            this.categories.addAll(categories);
        }
        this.userInfo = userInfo;
        this.userId = userInfo.getUserId();
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.category_item, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Object category = categories.get(position);

        String categoryTitle;
        List<String> movieIds;

        if (category instanceof CategoryPromoted) {
            CategoryPromoted promotedCategory = (CategoryPromoted) category;
            categoryTitle = promotedCategory.getCategory();
            movieIds = promotedCategory.getMovies();
        } else if (category instanceof CategoryModel) {
            CategoryModel categoryModel = (CategoryModel) category;
            categoryTitle = categoryModel.getName();
            movieIds = categoryModel.getMovies();
        } else {
            return;
        }

        holder.categoryTitle.setText(categoryTitle);

        if (movieIds == null || movieIds.isEmpty()) {
            Log.d("CategoryAdapter", "No movies to fetch for category " + categoryTitle);
            return;
        }

        List<MovieModel> movieDetails = new ArrayList<>();
        for (String movieId : movieIds) {
            MovieApiService apiService = RetrofitClient.getRetrofitInstance().create(MovieApiService.class);
            apiService.getMovieById(movieId, userId).enqueue(new Callback<>() {
                @Override
                public void onResponse(Call<MovieModel> call, Response<MovieModel> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        movieDetails.add(response.body());
                        Log.d("CategoryAdapter", "Fetched movie: " + response.body().getTitle());
                    } else {
                        Log.e("CategoryAdapter", "Failed to fetch movie details.");
                    }

                    if (movieDetails.size() == movieIds.size()) {
                        Log.d("CategoryAdapter", "All movies fetched for category " + categoryTitle);
                        MovieAdapter movieAdapter = new MovieAdapter(context, movieDetails, userInfo);
                        holder.moviesRecyclerView.setAdapter(movieAdapter);
                        holder.moviesRecyclerView.setLayoutManager(
                                new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                        );
                    }
                }

                @Override
                public void onFailure(Call<MovieModel> call, Throwable t) {
                    Log.e("CategoryAdapter", "Failed to fetch movie details: " + t.getMessage());
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public void updateData(List<?> newCategories) {
        categories.clear();
        if (newCategories != null) {
            categories.addAll(newCategories);
        }
        notifyDataSetChanged();
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