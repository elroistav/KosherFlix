package com.example.netflix_app4;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {
    private Context context;
    private List<MovieModel> movieList;

    public MovieAdapter(Context context, List<MovieModel> movieList) {
        this.context = context;
        this.movieList = movieList;
        Log.d("MovieAdapter", "Adapter initialized with movie list size: " + movieList.size());

    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_movie, parent, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        MovieModel movie = movieList.get(position);
        Log.d("MainActivity", "Binding movie: " + movie.getTitle()); // Debugging
        Log.d("MainActivity", "Movie thumbnail: " + movie.getThumbnail()); // Debugging
        Log.d("MainActivity", "Movie description: " + movie.getDescription()); // Debugging
        Log.d("MainActivity", "Movie video URL: " + movie.getVideoUrl()); // Debugging
        holder.movieTitle.setText(movie.getTitle());
        Glide.with(context).load(movie.getThumbnail()).into(holder.movieThumbnail);

        holder.itemView.setOnClickListener(v -> {
            Toast.makeText(context, "Clicked on: " + movie.getTitle(), Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }

    public static class MovieViewHolder extends RecyclerView.ViewHolder {
        TextView movieTitle;
        ImageView movieThumbnail;

        public MovieViewHolder(@NonNull View itemView) {
            super(itemView);
            movieTitle = itemView.findViewById(R.id.movieTitle);
            movieThumbnail = itemView.findViewById(R.id.movieThumbnail);
        }
    }
}

