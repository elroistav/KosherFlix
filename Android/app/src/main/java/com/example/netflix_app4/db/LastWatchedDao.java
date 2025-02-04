package com.example.netflix_app4.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface LastWatchedDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertLastWatched(List<LastWatchedEntity> lastWatchedMovies);

    @Query("SELECT * FROM last_watched")
    LiveData<List<LastWatchedEntity>> getLastWatchedMovies();
}
