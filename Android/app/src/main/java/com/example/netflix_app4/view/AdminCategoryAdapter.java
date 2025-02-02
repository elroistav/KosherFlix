package com.example.netflix_app4.view;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.netflix_app4.R;
import com.example.netflix_app4.model.CategoryModel;
import com.example.netflix_app4.model.MovieModel;
import com.example.netflix_app4.model.UserInfo;
import com.example.netflix_app4.network.MovieApiService;
import com.example.netflix_app4.network.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminCategoryAdapter extends RecyclerView.Adapter<AdminCategoryAdapter.AdminCategoryViewHolder> {
    private static final String TAG = "AdminCategoryAdapter";
    private final Context context;
    private final List<CategoryModel> categories;
    private final UserInfo userInfo;
    private final MovieApiService apiService;

    private final AdminMovieAdapter.OnMovieEditListener movieEditListener;
    private final AdminMovieAdapter.OnMovieDeleteListener movieDeleteListener;

    // Callbacks for category operations
    public interface OnCategoryDeleteListener {
        void onCategoryDelete(String categoryId);
    }

    public interface OnCategoryEditListener {
        void onCategoryEdit(CategoryModel category);
    }

    private final OnCategoryDeleteListener deleteListener;
    private final OnCategoryEditListener editListener;

    public AdminCategoryAdapter(Context context,
                                List<CategoryModel> categories,
                                UserInfo userInfo,
                                OnCategoryDeleteListener deleteListener,
                                OnCategoryEditListener editListener,
                                AdminMovieAdapter.OnMovieEditListener movieEditListener,
                                AdminMovieAdapter.OnMovieDeleteListener movieDeleteListener) {
        this.context = context;
        this.categories = new ArrayList<>(categories);
        this.userInfo = userInfo;
        this.deleteListener = deleteListener;
        this.editListener = editListener;
        this.movieEditListener = movieEditListener;
        this.movieDeleteListener = movieDeleteListener;
        this.apiService = RetrofitClient.getRetrofitInstance().create(MovieApiService.class);
    }

    @NonNull
    @Override
    public AdminCategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_admin_category, parent, false);
        return new AdminCategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminCategoryViewHolder holder, int position) {
        CategoryModel category = categories.get(position);
        holder.bind(category);
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public void updateData(List<CategoryModel> newCategories) {
        categories.clear();
        categories.addAll(newCategories);
        notifyDataSetChanged();
    }

    class AdminCategoryViewHolder extends RecyclerView.ViewHolder {
        private final TextView categoryTitle;
        private final ImageButton editButton;
        private final ImageButton deleteButton;
        private final RecyclerView moviesRecyclerView;

        public AdminCategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryTitle = itemView.findViewById(R.id.categoryTitle);
            editButton = itemView.findViewById(R.id.editCategoryButton);
            deleteButton = itemView.findViewById(R.id.deleteCategoryButton);
            moviesRecyclerView = itemView.findViewById(R.id.moviesRecyclerView);
        }

        public void bind(CategoryModel category) {
            categoryTitle.setText(category.getName());

            // Setup edit button
            editButton.setOnClickListener(v -> {
                if (editListener != null) {
                    editListener.onCategoryEdit(category);
                }
            });

            // Setup delete button with confirmation dialog
            deleteButton.setOnClickListener(v -> showDeleteConfirmation(category));

            // Fetch and setup movies
            fetchMoviesForCategory(category);
        }

        private void showDeleteConfirmation(CategoryModel category) {
            new AlertDialog.Builder(context)
                    .setTitle("Delete Category")
                    .setMessage("Are you sure you want to delete this category?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        if (deleteListener != null) {
                            deleteListener.onCategoryDelete(category.getId());
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();
        }

        private void fetchMoviesForCategory(CategoryModel category) {
            List<MovieModel> movieDetails = new ArrayList<>();
            List<String> movieIds = category.getMovies();

            if (movieIds == null || movieIds.isEmpty()) {
                Log.d(TAG, "No movies to fetch for category: " + category.getName());
                return;
            }

            for (String movieId : movieIds) {
                apiService.getMovieById(movieId, userInfo.getUserId())
                        .enqueue(new Callback<MovieModel>() {
                            @Override
                            public void onResponse(Call<MovieModel> call, Response<MovieModel> response) {
                                if (response.isSuccessful() && response.body() != null) {
                                    movieDetails.add(response.body());

                                    if (movieDetails.size() == movieIds.size()) {
                                        // All movies fetched, setup RecyclerView
                                        AdminMovieAdapter movieAdapter = new AdminMovieAdapter(
                                                context,
                                                movieDetails,
                                                userInfo,
                                                movieEditListener,
                                                movieDeleteListener
                                        );
                                        moviesRecyclerView.setAdapter(movieAdapter);
                                        moviesRecyclerView.setLayoutManager(
                                                new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                                        );
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<MovieModel> call, Throwable t) {
                                Log.e(TAG, "Failed to fetch movie details: " + t.getMessage());
                                Toast.makeText(context, "Failed to load movies", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        }
    }

    public interface OnMovieEditListener {
        void onMovieEdit(MovieModel movie);
    }

    public interface OnMovieDeleteListener {
        void onMovieDelete(String movieId);
    }
}