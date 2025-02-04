package com.example.netflix_app4.db;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import com.example.netflix_app4.model.CategoryWithMovies;

import java.util.List;

@Dao
public interface CategoryDao {
    @Query("DELETE FROM categories")
    void deleteAllCategories();

    @Query("DELETE FROM category_movie_cross_ref")
    void deleteAllCrossRefs();

    @Transaction
    @Query("SELECT DISTINCT c.*, m.* FROM categories c " +
            "LEFT JOIN category_movie_cross_ref cr ON c.name = cr.category " +
            "LEFT JOIN movies m ON cr.movieId = m.id " +
            "WHERE c.isPromoted = 1")
    LiveData<List<CategoryWithMovies>> getPromotedCategories();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertCategories(List<CategoryEntity> categories);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertCategoryMovieCrossRefs(List<CategoryMovieCrossRef> crossRefs);

    @Query("SELECT * FROM category_movie_cross_ref")
    List<CategoryMovieCrossRef> getAllCrossRefs();
}