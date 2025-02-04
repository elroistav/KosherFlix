package com.example.netflix_app4.model;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import com.example.netflix_app4.db.CategoryEntity;
import com.example.netflix_app4.db.CategoryMovieCrossRef;
import com.example.netflix_app4.db.MovieEntity;

import java.util.List;

public class CategoryWithMovies {
    @Embedded
    public CategoryEntity category;

    @Relation(
            parentColumn = "name",  // this comes from CategoryEntity
            entityColumn = "id",  // this comes from MovieEntity
            associateBy = @Junction(
                    value = CategoryMovieCrossRef.class,
                    // These should match the column names in CategoryMovieCrossRef
                    parentColumn = "category",    // maps to CategoryEntity's id
                    entityColumn = "movieId"        // maps to MovieEntity's id
            )
    )
    public List<MovieEntity> movies;
}

