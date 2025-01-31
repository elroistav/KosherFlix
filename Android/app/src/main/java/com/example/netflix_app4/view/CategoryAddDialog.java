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
import java.util.ArrayList;

public class CategoryAddDialog extends Dialog {
    private OnCategorySaveListener saveListener;
    private boolean isSaving = false;

    public interface OnCategorySaveListener {
        void onCategorySave(CategoryModel newCategory);
    }

    public CategoryAddDialog(@NonNull Context context, OnCategorySaveListener saveListener) {
        super(context);
        this.saveListener = saveListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_add_category);

        // Find views
        EditText nameEditText = findViewById(R.id.editTextCategoryName);
        EditText descriptionEditText = findViewById(R.id.editTextCategoryDescription);
        CheckBox promotedCheckBox = findViewById(R.id.checkBoxPromoted);
        Button saveButton = findViewById(R.id.buttonSave);
        Button cancelButton = findViewById(R.id.buttonCancel);
        TextView errorTextView = findViewById(R.id.textViewError);

        // Clear any previous state
        isSaving = false;

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
                CategoryModel newCategory = createNewCategory(name, description, isPromoted);
                if (saveListener != null) {
                    saveListener.onCategorySave(newCategory);
                }
            } catch (Exception e) {
                errorTextView.setText("Error creating new category");
                errorTextView.setVisibility(TextView.VISIBLE);
                saveButton.setEnabled(true);
                saveButton.setText("Add Category");
                isSaving = false;
            }
        });
    }

    private CategoryModel createNewCategory(String name, String description, boolean isPromoted) {
        Parcel parcel = null;
        try {
            parcel = Parcel.obtain();

            // Write the data to parcel
            parcel.writeString(null); // id is null for new category
            parcel.writeString(name);
            parcel.writeString(description);
            parcel.writeByte((byte) (isPromoted ? 1 : 0));
            parcel.writeStringList(new ArrayList<>()); // empty movies list

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
                saveButton.setText("Add Category");
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