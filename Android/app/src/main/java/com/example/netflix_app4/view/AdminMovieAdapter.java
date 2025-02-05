package com.example.netflix_app4.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.netflix_app4.R;
import com.example.netflix_app4.model.MovieModel;
import com.example.netflix_app4.model.UserInfo;
import com.example.netflix_app4.network.Config;
import com.example.netflix_app4.network.MovieApiService;
import com.example.netflix_app4.network.RetrofitClient;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminMovieAdapter extends RecyclerView.Adapter<AdminMovieAdapter.AdminMovieViewHolder> {
    private static final String TAG = "AdminMovieAdapter";
    private final Context context;
    private final List<MovieModel> movies;
    private final UserInfo userInfo;
    private final MovieApiService apiService;

    public interface OnMovieEditListener {
        void onMovieEdit(MovieModel movie);
    }

    public interface OnMovieDeleteListener {
        void onMovieDelete(String movieId);
    }

    private final OnMovieEditListener editListener;
    private final OnMovieDeleteListener deleteListener;

    public AdminMovieAdapter(Context context,
                             List<MovieModel> movies,
                             UserInfo userInfo,
                             OnMovieEditListener editListener,
                             OnMovieDeleteListener deleteListener) {
        this.context = context;
        this.movies = movies;
        this.userInfo = userInfo;
        this.editListener = editListener;
        this.deleteListener = deleteListener;
        this.apiService = RetrofitClient.getRetrofitInstance().create(MovieApiService.class);
    }

    @NonNull
    @Override
    public AdminMovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_admin_movie, parent, false);
        return new AdminMovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminMovieViewHolder holder, int position) {
        MovieModel movie = movies.get(position);
        holder.bind(movie);
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    class AdminMovieViewHolder extends RecyclerView.ViewHolder {
        private final ImageView movieThumbnail;
        private final TextView movieTitle;
        private final ImageButton editButton;
        private final ImageButton deleteButton;

        public AdminMovieViewHolder(@NonNull View itemView) {
            super(itemView);
            movieThumbnail = itemView.findViewById(R.id.movieThumbnail);
            movieTitle = itemView.findViewById(R.id.movieTitle);
            editButton = itemView.findViewById(R.id.editMovieButton);
            deleteButton = itemView.findViewById(R.id.deleteMovieButton);
        }

        public void bind(MovieModel movie) {
            // Set movie title
            movieTitle.setText(movie.getTitle());

            // Load thumbnail using Glide
            Glide.with(context)
                    .load(Config.getBaseUrl() + "/" + movie.getThumbnail())
                    .placeholder(R.drawable.placeholder_image)
                    .into(movieThumbnail);

            // Setup click listeners for viewing movie details
            itemView.setOnClickListener(v -> {
                Intent intent = new Intent(context, MovieDetailsActivity.class);
                intent.putExtra("movieDetails", movie);
                intent.putExtra("USER_INFO", userInfo);
                context.startActivity(intent);
            });

            editButton.setOnClickListener(v -> {
                if (editListener != null) {
                    editListener.onMovieEdit(movie);
                }
            });

            deleteButton.setOnClickListener(v -> showDeleteConfirmation(movie));
        }

        private boolean isDialogShowing = false;

        private void showDeleteConfirmation(MovieModel movie) {
            if (isDialogShowing) {
                Log.d(TAG, "Dialog is already showing, returning.");
                return;
            }

            isDialogShowing = true;
            Log.d(TAG, "Showing delete confirmation for movie: " + movie.getTitle());

            // Hide FAB buttons before showing the dialog
            FloatingActionButton addMovieFab = ((Activity) context).findViewById(R.id.addMovieFab);
            FloatingActionButton addCategoryFab = ((Activity) context).findViewById(R.id.addCategoryFab);
            addMovieFab.setVisibility(View.GONE);
            addCategoryFab.setVisibility(View.GONE);
            Log.d(TAG, "FAB buttons hidden.");

            AlertDialog deleteDialog = new AlertDialog.Builder(context)
                    .setTitle("Delete Movie")
                    .setMessage("Are you sure you want to delete this movie?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        Log.d(TAG, "Yes clicked for movie: " + movie.getTitle());

                        new Handler(Looper.getMainLooper()).postDelayed(() -> {
                            if (deleteListener != null) {
                                deleteListener.onMovieDelete(movie.getId());
                            }

                            addMovieFab.setVisibility(View.VISIBLE);
                            addCategoryFab.setVisibility(View.VISIBLE);
                            Log.d(TAG, "FAB buttons visible again.");

                            dialog.dismiss();
                            isDialogShowing = false;
                        }, 200);
                    })
                    .setNegativeButton("No", (dialog, which) -> {
                        Log.d(TAG, "No clicked for movie: " + movie.getTitle());
                        isDialogShowing = false;

                        addMovieFab.setVisibility(View.VISIBLE);
                        addCategoryFab.setVisibility(View.VISIBLE);
                        Log.d(TAG, "FAB buttons visible again after cancel.");

                        dialog.dismiss();
                    })
                    .create();
            deleteDialog.show();
        }
    }

    public void updateMovies(List<MovieModel> newMovies) {
        movies.clear();
        movies.addAll(newMovies);
        notifyDataSetChanged();
    }
}