package com.example.netflix_app4.view;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcel;
import android.provider.OpenableColumns;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.example.netflix_app4.R;
import com.example.netflix_app4.model.MovieModel;

import java.util.ArrayList;

public class MovieAddDialog extends Dialog {
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int PICK_VIDEO_REQUEST = 2;

    private OnMovieSaveListener saveListener;
    private boolean isSaving = false;
    private Uri selectedImageUri;
    private Uri selectedVideoUri;
    private ImageView thumbnailPreview;
    private TextView videoFileName;

    private final Activity activity;

    private ArrayList<String> selectedCategories = new ArrayList<>();

    public interface OnMovieSaveListener {
        void onMovieSave(MovieModel newMovie, Uri thumbnailUri, Uri videoUri, ArrayList<String> categories);
    }

    public MovieAddDialog(@NonNull Context context, OnMovieSaveListener saveListener) {
        super(context);
        this.saveListener = saveListener;
        if (!(context instanceof Activity)) {
            throw new IllegalArgumentException("Context must be an Activity");
        }
        this.activity = (Activity) context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_add_movie);

        initializeViews();
        setupListeners();
    }

    private void initializeViews() {
        thumbnailPreview = findViewById(R.id.thumbnailPreview);
        videoFileName = findViewById(R.id.videoFileName);
    }

    private void setupListeners() {
        findViewById(R.id.buttonChooseThumbnail).setOnClickListener(v -> {
            if (hasRequiredPermissions()) {
                openImagePicker();
            } else {
                requestPermissions();
            }
        });

        findViewById(R.id.buttonChooseVideo).setOnClickListener(v -> {
            if (hasRequiredPermissions()) {
                openVideoPicker();
            } else {
                requestPermissions();
            }
        });

        findViewById(R.id.buttonSave).setOnClickListener(v -> validateAndSave());
        findViewById(R.id.buttonCancel).setOnClickListener(v -> dismiss());
    }

    private boolean hasRequiredPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return ActivityCompat.checkSelfPermission(getContext(),
                    Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(getContext(),
                    Manifest.permission.READ_MEDIA_VIDEO) == PackageManager.PERMISSION_GRANTED;
        } else {
            return ActivityCompat.checkSelfPermission(getContext(),
                    Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        }
    }

    private void requestPermissions() {
        Activity activity = (Activity) getContext();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(activity,
                    new String[]{
                            Manifest.permission.READ_MEDIA_IMAGES,
                            Manifest.permission.READ_MEDIA_VIDEO
                    },
                    100);
        } else {
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    100);
        }
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        activity.startActivityForResult(
                Intent.createChooser(intent, "Select Thumbnail"),
                PICK_IMAGE_REQUEST
        );
    }

    private void openVideoPicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("video/*");
        activity.startActivityForResult(
                Intent.createChooser(intent, "Select Video"),
                PICK_VIDEO_REQUEST
        );
    }

    private void validateAndSave() {
        if (isSaving) return;

        EditText titleInput = findViewById(R.id.editTextMovieTitle);
        String title = titleInput.getText().toString().trim();
        if (TextUtils.isEmpty(title)) {
            showError("Title is required");
            return;
        }

        if (selectedImageUri == null) {
            showError("Please select a thumbnail image");
            return;
        }

        if (selectedVideoUri == null) {
            showError("Please select a video");
            return;
        }

        EditText categoriesInput = findViewById(R.id.editTextMovieCategories);
        String categoriesText = categoriesInput.getText().toString().trim();
        selectedCategories = new ArrayList<>();

        if (!TextUtils.isEmpty(categoriesText)) {
            // Split categories by comma and trim whitespace
            String[] categoryArray = categoriesText.split(",");
            for (String category : categoryArray) {
                String trimmedCategory = category.trim();
                if (!TextUtils.isEmpty(trimmedCategory)) {
                    selectedCategories.add(trimmedCategory);
                }
            }
        }

        try {
            double rating = Double.parseDouble(((EditText) findViewById(R.id.editTextMovieRating)).getText().toString());
            int length = Integer.parseInt(((EditText) findViewById(R.id.editTextMovieLength)).getText().toString());

            MovieModel newMovie = createMovieModel(
                    title,
                    ((EditText) findViewById(R.id.editTextMovieDescription)).getText().toString(),
                    rating,
                    length,
                    ((EditText) findViewById(R.id.editTextMovieDirector)).getText().toString(),
                    ((EditText) findViewById(R.id.editTextMovieReleaseDate)).getText().toString(),
                    ((EditText) findViewById(R.id.editTextMovieLanguage)).getText().toString()
            );

            Intent resultIntent = new Intent();
            resultIntent.putStringArrayListExtra("categories", selectedCategories);
            activity.setResult(Activity.RESULT_OK, resultIntent);
            activity.finish();


            isSaving = true;
            findViewById(R.id.buttonSave).setEnabled(false);

            if (saveListener != null) {
                saveListener.onMovieSave(newMovie, selectedImageUri, selectedVideoUri, selectedCategories);
            }
        } catch (NumberFormatException e) {
            showError("Invalid number format in rating or length");
        }
    }

    private MovieModel createMovieModel(String title, String description,
                                        double rating, int length, String director,
                                        String releaseDate, String language) {
        Parcel parcel = Parcel.obtain();
        try {
            parcel.writeString(null);
            parcel.writeString(title);
            parcel.writeString(description);
            parcel.writeDouble(rating);
            parcel.writeInt(length);
            parcel.writeString(director);
            parcel.writeTypedList(new ArrayList<>());
            parcel.writeString(language);
            parcel.writeString(releaseDate);
            parcel.writeString("");
            parcel.writeString("");

            parcel.setDataPosition(0);
            return MovieModel.CREATOR.createFromParcel(parcel);
        } finally {
            parcel.recycle();
        }
    }

    private void showError(String message) {
        TextView errorText = findViewById(R.id.textViewError);
        errorText.setText(message);
        errorText.setVisibility(View.VISIBLE);
    }

    public void handleFileSelection(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            if (requestCode == PICK_IMAGE_REQUEST) {
                selectedImageUri = data.getData();
                thumbnailPreview.setImageURI(selectedImageUri);
                thumbnailPreview.setVisibility(View.VISIBLE);
            } else if (requestCode == PICK_VIDEO_REQUEST) {
                selectedVideoUri = data.getData();
                String fileName = getFileName(selectedVideoUri);
                videoFileName.setText(fileName);
                videoFileName.setVisibility(View.VISIBLE);
            }
        }
    }

    @SuppressLint("Range")
    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = getContext().getContentResolver()
                    .query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    public void handleSaveResult(boolean success, String errorMessage) {
        new Handler(Looper.getMainLooper()).post(() -> {
            if (!isShowing()) return;

            isSaving = false;
            Button saveButton = findViewById(R.id.buttonSave);
            saveButton.setEnabled(true);
            saveButton.setText("Save");

            if (success) {
                dismiss();
            } else {
                showError(errorMessage != null ? errorMessage : "Failed to save movie");
            }
        });
    }
}
