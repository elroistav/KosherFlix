package com.example.netflix_app4.view;

import android.content.Context;
import android.content.Intent;
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
import com.example.netflix_app4.model.UserInfo;
import com.example.netflix_app4.network.Config;

import java.util.List;

public class LastWatchedAdapter extends RecyclerView.Adapter<LastWatchedAdapter.ViewHolder> {
    private final Context context;
    private List<MovieModel> movies;
    private final UserInfo userInfo;

    public LastWatchedAdapter(Context context, List<MovieModel> movies, UserInfo userInfo) {
        this.context = context;
        this.movies = movies;
        this.userInfo = userInfo;
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
                if (position != RecyclerView.NO_POSITION) {
                    Intent intent = new Intent(context, MovieDetailsActivity.class);
                    intent.putExtra("movieDetails", movies.get(position));
                    intent.putExtra("USER_INFO", userInfo);
                    context.startActivity(intent);
                }
            });
        }

        void bind(MovieModel movie) {
            movieTitle.setText(movie.getTitle());
            Glide.with(context)
                    .load(Config.getBaseUrl() + "/" + movie.getThumbnail())
                    .placeholder(R.drawable.placeholder_image)
                    .into(movieThumbnail);
        }
    }
}