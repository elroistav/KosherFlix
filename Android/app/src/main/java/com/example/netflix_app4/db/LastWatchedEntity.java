package com.example.netflix_app4.db;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "last_watched")
public class LastWatchedEntity {
    @PrimaryKey
    @NonNull
    private String movieId;

    public LastWatchedEntity(@NonNull String movieId) {
        this.movieId = movieId;
    }

    public String getMovieId() { return movieId; }
}

