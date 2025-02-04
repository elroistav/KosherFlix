package com.example.netflix_app4.db;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.google.gson.annotations.SerializedName;

import org.jetbrains.annotations.NotNull;
import java.util.List;

@Entity(tableName = "movies")
public class MovieEntity {
    @PrimaryKey
    @NonNull
    @SerializedName("_id")  // MongoDB uses _id
    private String id;
    private String title;
    private String thumbnail;

    public MovieEntity(@NonNull String id, String title, String thumbnail) {
        this.id = id;
        this.title = title;
        this.thumbnail = thumbnail;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getThumbnail() { return thumbnail; }
}

