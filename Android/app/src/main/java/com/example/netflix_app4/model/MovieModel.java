package com.example.netflix_app4.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class MovieModel implements Parcelable {
    private final String _id;
    private String title;
    private String description;
    private double rating;
    private int length;
    private String director;
    //private List<String> cast;
    private List<CategoryModel> categories; // Update to match the JSON
    private String language;
    private String releaseDate;
    private String thumbnail;
    private String videoUrl;

    public MovieModel(Parcel in) {
        _id = in.readString();
        title = in.readString();
        description = in.readString();
        rating = in.readDouble();
        length = in.readInt();
        director = in.readString();
        categories = in.createTypedArrayList(CategoryModel.CREATOR);
        language = in.readString();
        releaseDate = in.readString();
        thumbnail = in.readString();
        videoUrl = in.readString();
        //cast = in.createStringArrayList();
    }

    public static final Creator<MovieModel> CREATOR = new Creator<>() {
        @Override
        public MovieModel createFromParcel(Parcel in) {
            return new MovieModel(in);
        }

        @Override
        public MovieModel[] newArray(int size) {
            return new MovieModel[size];
        }
    };

    public MovieModel(String id, String title, String description, double rating, int length, String director, List<CategoryModel> categories, String language, String releaseDate, String thumbnail, String videoUrl) {
        this._id = id;
        this.title = title;
        this.description = description;
        this.rating = rating;
        this.length = length;
        this.director = director;
        this.categories = categories;
        this.language = language;
        this.releaseDate = releaseDate;
        this.thumbnail = thumbnail;
        this.videoUrl = videoUrl;
    }

    // Getters
    public String getId() {
        return _id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public double getRating() {
        return rating;
    }

    public int getLength() {
        return length;
    }

    public String getDirector() {
        return director;
    }

    public List<CategoryModel> getCategories() {
        return categories;
    }

    public String getLanguage() {
        return language;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(_id);
        parcel.writeString(title);
        parcel.writeString(description);
        parcel.writeDouble(rating);
        parcel.writeInt(length);
        parcel.writeString(director);
        parcel.writeTypedList(categories);
        parcel.writeString(language);
        parcel.writeString(releaseDate);
        parcel.writeString(thumbnail);
        parcel.writeString(videoUrl);
    }
}
