package com.example.netflix_app4.db;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

@Entity(tableName = "categories")
public class CategoryEntity {
    @PrimaryKey
    @NonNull
    private String name;
    private boolean isPromoted;

    public CategoryEntity(@NonNull String name, boolean isPromoted) {
        this.name = name;
        this.isPromoted = isPromoted;
    }

    public String getName() { return name; }
    public boolean isPromoted() { return isPromoted; }
}



