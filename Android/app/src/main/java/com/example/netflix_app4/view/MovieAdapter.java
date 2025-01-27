package com.example.netflix_app4.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.netflix_app4.R;
import com.example.netflix_app4.model.MovieModel;

import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {
    private Context context;
    private List<MovieModel> movies;
    private CategoryAdapter.OnMovieClickListener movieClickListener;

    public MovieAdapter(Context context, List<MovieModel> movies, CategoryAdapter.OnMovieClickListener movieClickListener) {
        this.context = context;
        this.movies = movies;
        this.movieClickListener = movieClickListener;
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_movie, parent, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        MovieModel movie = movies.get(position);
        holder.bind(movie);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, MovieDetailsActivity.class);
            intent.putExtra("movieDetails", movie); // Pass the selected movie as an extra
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    public class MovieViewHolder extends RecyclerView.ViewHolder {
        private ImageView movieThumbnail;
        private TextView movieTitle;

        public MovieViewHolder(View itemView) {
            super(itemView);
            movieThumbnail = itemView.findViewById(R.id.movieThumbnail);
            movieTitle = itemView.findViewById(R.id.movieTitle);

            // Handle item click
            itemView.setOnClickListener(v -> {
                int position = getBindingAdapterPosition();
                if (position != RecyclerView.NO_POSITION && movieClickListener != null) {
                    movieClickListener.onMovieClick(movies.get(position));
                }
            });
        }

        public void bind(MovieModel movie) {
            Glide.with(context)
                    .load(movie.getThumbnail())
                    .placeholder(R.drawable.placeholder_image) // Add a placeholder for better UX
                    .into(movieThumbnail);
            movieTitle.setText(movie.getTitle());
        }
    }
}


