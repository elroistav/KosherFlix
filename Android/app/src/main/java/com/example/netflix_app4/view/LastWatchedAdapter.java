package com.example.netflix_app4.view;

import android.content.Context;
import android.util.Log;
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
import java.util.List;
import com.example.netflix_app4.view.CategoryAdapter;

public class LastWatchedAdapter extends RecyclerView.Adapter<LastWatchedAdapter.ViewHolder> {
    private final Context context;
    private List<MovieModel> movies;
    private final CategoryAdapter.OnMovieClickListener movieClickListener;
    public interface OnMovieClickListener {
        void onMovieClick(MovieModel movie);
    }

    public LastWatchedAdapter(Context context, List<MovieModel> movies,
                              CategoryAdapter.OnMovieClickListener movieClickListener) {
        this.context = context;
        this.movies = movies;
        this.movieClickListener = movieClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_movie, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MovieModel movie = movies.get(position);
        holder.bind(movie);
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    public void updateData(List<MovieModel> newMovies) {
        Log.d("LastWatchedAdapter", "Updating data with " + newMovies.size() + " movies");
        this.movies = newMovies;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView movieThumbnail;
        private final TextView movieTitle;


        ViewHolder(@NonNull View itemView) {
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

        void bind(MovieModel movie) {
            movieTitle.setText(movie.getTitle());
            Glide.with(context)
                    .load(movie.getThumbnail())
                    .placeholder(R.drawable.placeholder_image)
                    .into(movieThumbnail);
        }
    }
}