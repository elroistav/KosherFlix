package com.example.netflix_app4.db;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

@Entity(
        tableName = "category_movie_cross_ref",
        primaryKeys = {"category", "movieId"},
        foreignKeys = {
                @ForeignKey(entity = CategoryEntity.class, parentColumns = "name", childColumns = "category"),
                @ForeignKey(entity = MovieEntity.class, parentColumns = "id", childColumns = "movieId")
        },
        indices = {
                @Index("category"),
                @Index("movieId")
        }
)
public class CategoryMovieCrossRef {
    @NonNull
    @ColumnInfo(name = "category")
    private String category;

    @NonNull
    @ColumnInfo(name = "movieId")
    private String movieId;

    public CategoryMovieCrossRef(@NonNull String category, @NonNull String movieId) {
        this.category = category;
        this.movieId = movieId;
    }

    @NonNull
    public String getCategory() { return category; }
    @NonNull
    public String getMovieId() { return movieId; }

    public void setCategoryId(String categoryId) {
        this.category = categoryId;
    }

    public void setMovieId(String movieId) {
        this.movieId = movieId;
    }
}