package com.example.netflix_app4.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.netflix_app4.R;
import com.example.netflix_app4.model.MovieModel;
import com.example.netflix_app4.network.Config;

import java.util.ArrayList;
import java.util.List;

public class SearchResultsAdapter extends RecyclerView.Adapter<SearchResultsAdapter.SearchViewHolder> {
    private final Context context;
    private List<MovieModel> movies;
    private final OnMovieClickListener movieClickListener;

    public interface OnMovieClickListener {
        void onMovieClick(MovieModel movie);
    }

    public SearchResultsAdapter(Context context, OnMovieClickListener listener) {
        this.context = context;
        this.movies = new ArrayList<>();
        this.movieClickListener = listener;
    }

    @NonNull
    @Override
    public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_movie, parent, false);
        return new SearchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchViewHolder holder, int position) {
        MovieModel movie = movies.get(position);
        holder.bind(movie);
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    public void updateData(List<MovieModel> newMovies) {
        this.movies = newMovies;
        notifyDataSetChanged();
    }

    class SearchViewHolder extends RecyclerView.ViewHolder {
        private final ImageView movieThumbnail;
        private final TextView movieTitle;

        public SearchViewHolder(@NonNull View itemView) {
            super(itemView);
            movieThumbnail = itemView.findViewById(R.id.movieThumbnail);
            movieTitle = itemView.findViewById(R.id.movieTitle);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && movieClickListener != null) {
                    movieClickListener.onMovieClick(movies.get(position));
                }
            });
        }

        public void bind(MovieModel movie) {
            movieTitle.setText(movie.getTitle());
            Glide.with(context)
                    .load(Config.getBaseUrl() + "/" + movie.getThumbnail())
                    .placeholder(R.drawable.placeholder_image)
                    .into(movieThumbnail);
        }
    }
}