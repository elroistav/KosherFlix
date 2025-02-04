package com.example.netflix_app4.db;

import androidx.room.TypeConverter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.List;

public class CategoryConverter {
    @TypeConverter
    public static String fromList(List<String> categories) {
        if (categories == null) return null;
        return new Gson().toJson(categories);
    }

    @TypeConverter
    public static List<String> toList(String categoryJson) {
        if (categoryJson == null) return null;
        Type listType = new TypeToken<List<String>>() {}.getType();
        return new Gson().fromJson(categoryJson, listType);
    }
}

