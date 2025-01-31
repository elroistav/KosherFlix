package com.example.netflix_app4.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcel;
import android.text.TextUtils;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.netflix_app4.R;
import com.example.netflix_app4.model.CategoryModel;
import com.google.android.material.textfield.TextInputLayout;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

public class CategoryEditDialog extends Dialog {
    private CategoryModel originalCategory;
    private OnCategorySaveListener saveListener;
    private boolean isSaving = false;

    public interface OnCategorySaveListener {
        void onCategorySave(CategoryModel updatedCategory);
    }

    public CategoryEditDialog(@NonNull Context context, CategoryModel category,
                              OnCategorySaveListener saveListener) {
        super(context);
        this.originalCategory = category;
        this.saveListener = saveListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_edit_category);

        // Find views
        EditText nameEditText = findViewById(R.id.editTextCategoryName);
        EditText descriptionEditText = findViewById(R.id.editTextCategoryDescription);
        CheckBox promotedCheckBox = findViewById(R.id.checkBoxPromoted);
        Button saveButton = findViewById(R.id.buttonSave);
        Button cancelButton = findViewById(R.id.buttonCancel);
        TextView errorTextView = findViewById(R.id.textViewError);

        // Clear any previous state
        isSaving = false;

        // Populate initial values
        nameEditText.setText(originalCategory.getName());
        descriptionEditText.setText(originalCategory.getDescription());
        promotedCheckBox.setChecked(originalCategory.isPromoted());

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
            errorTextView.setVisibility(TextView.GONE);

            // Validate inputs
            String name = nameEditText.getText().toString().trim();
            String description = descriptionEditText.getText().toString().trim();
            boolean isPromoted = promotedCheckBox.isChecked();

            // Validate name
            if (TextUtils.isEmpty(name)) {
                errorTextView.setText("Category name is required");
                errorTextView.setVisibility(TextView.VISIBLE);
                return;
            }

            // Disable save button and show saving state
            saveButton.setEnabled(false);
            saveButton.setText("Saving...");
            isSaving = true;

            try {
                CategoryModel updatedCategory = createUpdatedCategory(name, description, isPromoted);
                if (saveListener != null) {
                    saveListener.onCategorySave(updatedCategory);
                }
            } catch (Exception e) {
                errorTextView.setText("Error creating updated category");
                errorTextView.setVisibility(TextView.VISIBLE);
                saveButton.setEnabled(true);
                saveButton.setText("Save Changes");
                isSaving = false;
            }
        });
    }

    private CategoryModel createUpdatedCategory(String name, String description, boolean isPromoted) {
        Parcel parcel = null;
        try {
            parcel = Parcel.obtain();

            // Write the data to parcel
            parcel.writeString(originalCategory.getId());
            parcel.writeString(name);
            parcel.writeString(description);
            parcel.writeByte((byte) (isPromoted ? 1 : 0));
            parcel.writeStringList(originalCategory.getMovies());

            // Reset position for reading
            parcel.setDataPosition(0);

            // Create new instance using CREATOR
            return CategoryModel.CREATOR.createFromParcel(parcel);
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
                    errorTextView.setVisibility(TextView.VISIBLE);
                } else {
                    errorTextView.setText("");
                    errorTextView.setVisibility(TextView.GONE);
                }
            }

            if (success) {
                dismiss();
            }
        });
    }
}