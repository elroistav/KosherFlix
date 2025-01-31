package com.example.netflix_app4.view;



import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcel;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.netflix_app4.R;
import com.example.netflix_app4.model.MovieModel;

public class MovieEditDialog extends Dialog {
    private MovieModel originalMovie;
    private OnMovieSaveListener saveListener;
    private boolean isSaving = false;

    public interface OnMovieSaveListener {
        void onMovieSave(MovieModel updatedMovie);
    }

    public MovieEditDialog(@NonNull Context context, MovieModel movie,
                           OnMovieSaveListener saveListener) {
        super(context);
        this.originalMovie = movie;
        this.saveListener = saveListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_edit_movie1);

        // Find views
        EditText titleEditText = findViewById(R.id.editTextMovieTitle);
        EditText descriptionEditText = findViewById(R.id.editTextMovieDescription);
        EditText thumbnailEditText = findViewById(R.id.editTextMovieThumbnail);
        EditText videoUrlEditText = findViewById(R.id.editTextMovieVideoUrl);
        EditText ratingEditText = findViewById(R.id.editTextMovieRating);
        EditText lengthEditText = findViewById(R.id.editTextMovieLength);
        EditText directorEditText = findViewById(R.id.editTextMovieDirector);
        EditText releaseDateEditText = findViewById(R.id.editTextMovieReleaseDate);
        EditText languageEditText = findViewById(R.id.editTextMovieLanguage);
        Button saveButton = findViewById(R.id.buttonSave);
        Button cancelButton = findViewById(R.id.buttonCancel);
        TextView errorTextView = findViewById(R.id.textViewError);

        // Clear any previous state
        isSaving = false;

        // Populate initial values
        titleEditText.setText(originalMovie.getTitle());
        descriptionEditText.setText(originalMovie.getDescription());
        thumbnailEditText.setText(originalMovie.getThumbnail());
        videoUrlEditText.setText(originalMovie.getVideoUrl());
        ratingEditText.setText(String.valueOf(originalMovie.getRating()));
        lengthEditText.setText(String.valueOf(originalMovie.getLength()));
        directorEditText.setText(originalMovie.getDirector());
        releaseDateEditText.setText(originalMovie.getReleaseDate());
        languageEditText.setText(originalMovie.getLanguage());

        // Cancel button
        cancelButton.setOnClickListener(v -> {
            isSaving = false;
            dismiss();
        });

        // Save button
        saveButton.setOnClickListener(v -> {
            if (isSaving) {
                return; // Prevent double-saving
            }

            // Reset error
            errorTextView.setText("");
            errorTextView.setVisibility(View.GONE);

            // Validate inputs
            String title = titleEditText.getText().toString().trim();
            String description = descriptionEditText.getText().toString().trim();
            String thumbnail = thumbnailEditText.getText().toString().trim();
            String videoUrl = videoUrlEditText.getText().toString().trim();
            String director = directorEditText.getText().toString().trim();
            String releaseDate = releaseDateEditText.getText().toString().trim();
            String language = languageEditText.getText().toString().trim();

            // Validate required fields
            if (TextUtils.isEmpty(title)) {
                errorTextView.setText("Movie title is required");
                errorTextView.setVisibility(View.VISIBLE);
                return;
            }

            // Parse numeric values
            double rating;
            int length;
            try {
                rating = Double.parseDouble(ratingEditText.getText().toString());
                if (rating < 0 || rating > 10) {
                    errorTextView.setText("Rating must be between 0 and 10");
                    errorTextView.setVisibility(View.VISIBLE);
                    return;
                }
            } catch (NumberFormatException e) {
                errorTextView.setText("Invalid rating value");
                errorTextView.setVisibility(View.VISIBLE);
                return;
            }

            try {
                length = Integer.parseInt(lengthEditText.getText().toString());
                if (length <= 0) {
                    errorTextView.setText("Length must be greater than 0");
                    errorTextView.setVisibility(View.VISIBLE);
                    return;
                }
            } catch (NumberFormatException e) {
                errorTextView.setText("Invalid length value");
                errorTextView.setVisibility(View.VISIBLE);
                return;
            }

            // Disable save button and show saving state
            saveButton.setEnabled(false);
            saveButton.setText("Saving...");
            isSaving = true;

            try {
                MovieModel updatedMovie = createUpdatedMovie(
                        title, description, thumbnail, videoUrl,
                        rating, length, director, releaseDate, language
                );
                if (saveListener != null) {
                    saveListener.onMovieSave(updatedMovie);
                }
            } catch (Exception e) {
                errorTextView.setText("Error creating updated movie");
                errorTextView.setVisibility(View.VISIBLE);
                saveButton.setEnabled(true);
                saveButton.setText("Save Changes");
                isSaving = false;
            }
        });
    }

    private MovieModel createUpdatedMovie(String title, String description,
                                          String thumbnail, String videoUrl,
                                          double rating, int length,
                                          String director, String releaseDate,
                                          String language) {
        Parcel parcel = null;
        try {
            parcel = Parcel.obtain();

            // Write the data to parcel
            parcel.writeString(originalMovie.getId());
            parcel.writeString(title);
            parcel.writeString(description);
            parcel.writeDouble(rating);
            parcel.writeInt(length);
            parcel.writeString(director);
            parcel.writeTypedList(originalMovie.getCategories());
            parcel.writeString(language);
            parcel.writeString(releaseDate);
            parcel.writeString(thumbnail);
            parcel.writeString(videoUrl);

            // Reset position for reading
            parcel.setDataPosition(0);

            // Create new instance using CREATOR
            return MovieModel.CREATOR.createFromParcel(parcel);
        } finally {
            if (parcel != null) {
                parcel.recycle();
            }
        }
    }

    @Override
    public void dismiss() {
        isSaving = false;
        super.dismiss();
    }

    public void handleSaveResult(boolean success, String errorMessage) {
        new Handler(Looper.getMainLooper()).post(() -> {
            if (!isShowing() || getWindow() == null) {
                return;
            }

            isSaving = false;

            Button saveButton = findViewById(R.id.buttonSave);
            TextView errorTextView = findViewById(R.id.textViewError);

            if (saveButton != null) {
                saveButton.setEnabled(true);
                saveButton.setText("Save Changes");
            }

            if (errorTextView != null) {
                if (!success && errorMessage != null) {
                    errorTextView.setText(errorMessage);
                    errorTextView.setVisibility(View.VISIBLE);
                } else {
                    errorTextView.setText("");
                    errorTextView.setVisibility(View.GONE);
                }
            }

            if (success) {
                dismiss();
            }
        });
    }
}