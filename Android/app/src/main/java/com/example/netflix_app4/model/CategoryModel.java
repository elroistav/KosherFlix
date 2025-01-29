package com.example.netflix_app4.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

public class CategoryModel implements Parcelable {
    private final String _id;
    private String name;
    private String description;
    private boolean promoted;
    private List<String> movies;

    private final MutableLiveData<List<CategoryModel>> allCategoriesLiveData = new MutableLiveData<>();


    protected CategoryModel(Parcel in) {
        _id = in.readString();
        name = in.readString();
        description = in.readString();
        promoted = in.readByte() != 0;
        movies = in.createStringArrayList();
    }

    public static final Creator<CategoryModel> CREATOR = new Creator<>() {
        @Override
        public CategoryModel createFromParcel(Parcel in) {
            return new CategoryModel(in);
        }

        @Override
        public CategoryModel[] newArray(int size) {
            return new CategoryModel[size];
        }
    };

    // Getters
    public String getId() {
        return _id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public boolean isPromoted() {
        return promoted;
    }

    public List<String> getMovies() {
        return movies;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(_id);
        parcel.writeString(name);
        parcel.writeString(description);
        parcel.writeByte((byte) (promoted ? 1 : 0));
        parcel.writeStringList(movies);
    }
}
