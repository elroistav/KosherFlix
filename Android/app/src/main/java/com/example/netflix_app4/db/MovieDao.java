package com.example.netflix_app4.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import java.util.List;

@Dao
public interface MovieDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMovie(MovieEntity movie);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMovies(List<MovieEntity> movies);

    @Query("SELECT * FROM movies")
    List<MovieEntity> getAllMovies();

    @Query("SELECT * FROM movies WHERE id = :movieId LIMIT 1")
    MovieEntity getMovieById(String movieId);

    @Query("SELECT id FROM movies WHERE id IN (:movieIds)")
    List<String> getExistingMovieIds(List<String> movieIds);

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMoviesWithTransaction(List<MovieEntity> movies);
}

