package com.example.netflix_app4.db;

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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertCategories(List<CategoryEntity> categories);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertCategoryMovieCrossRefs(List<CategoryMovieCrossRef> crossRefs);

    @Query("SELECT * FROM categories WHERE isPromoted = 1")
    @Transaction
    LiveData<List<CategoryWithMovies>> getPromotedCategories();
}


