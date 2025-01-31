package com.example.netflix_app4.view;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.netflix_app4.R;
import com.example.netflix_app4.util.FileUtil;
import com.example.netflix_app4.viewmodel.RegisterViewModel;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;


public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = "RegisterActivity";
    private static final int PICK_IMAGE_REQUEST = 1;

    // View references
    private TextInputLayout usernameLayout;
    private TextInputLayout nameLayout;
    private TextInputLayout emailLayout;
    private TextInputLayout passwordLayout;
    private TextInputEditText usernameInput;
    private TextInputEditText nameInput;
    private TextInputEditText emailInput;
    private TextInputEditText passwordInput;
    private ImageView profileImagePreview;
    private Uri selectedImageUri;
    private RegisterViewModel registerViewModel;

    // Regex patterns
    private static final Pattern USERNAME_REGEX = Pattern.compile("^[A-Za-z0-9_-]{3,15}$");
    private static final Pattern NAME_REGEX = Pattern.compile("^[A-Za-z ]+$");
    private static final Pattern EMAIL_REGEX = Pattern.compile(
            "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$");
    private static final Pattern PASSWORD_REGEX = Pattern.compile(
            "^(?=.*[a-zA-Z])(?=.*\\d).{8,}$");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: RegisterActivity started");
        setContentView(R.layout.activity_register);

        initializeViews();
        setupViewModel();
        observeViewModel();
    }

    private void initializeViews() {
        Log.d(TAG, "initializeViews: Initializing view components");

        // Initialize TextInputLayouts
        usernameLayout = findViewById(R.id.usernameLayout);
        nameLayout = findViewById(R.id.nameLayout);
        emailLayout = findViewById(R.id.emailLayout);
        passwordLayout = findViewById(R.id.passwordLayout);

        // Initialize EditTexts
        usernameInput = findViewById(R.id.usernameInput);
        nameInput = findViewById(R.id.nameInput);
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);

        // Initialize ImageView
        profileImagePreview = findViewById(R.id.profileImagePreview);

        // Setup click listeners
        findViewById(R.id.selectImageButton).setOnClickListener(v -> openImagePicker());
        findViewById(R.id.registerButton).setOnClickListener(v -> attemptRegistration());
    }

    private void setupViewModel() {
        Log.d(TAG, "setupViewModel: Setting up ViewModel");
        registerViewModel = new ViewModelProvider(this).get(RegisterViewModel.class);
    }

    private void observeViewModel() {
        Log.d(TAG, "observeViewModel: Observing ViewModel LiveData");

        registerViewModel.getRegistrationResult().observe(this, success -> {
            if (success) {
                Log.d(TAG, "observeViewModel: Registration successful");
                Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, LoginActivity.class));
                finish();
            }
        });

        registerViewModel.getError().observe(this, error -> {
            if (error != null) {
                Log.d(TAG, "observeViewModel: Registration failed with error: " + error);
                View rootView = findViewById(android.R.id.content);
                Snackbar.make(rootView, error, Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void openImagePicker() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Profile Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
            profileImagePreview.setVisibility(View.VISIBLE);
            profileImagePreview.setImageURI(selectedImageUri);
        }
    }

    private void attemptRegistration() {
        Log.d(TAG, "attemptRegistration: Validating registration data");

        // Reset errors
        usernameLayout.setError(null);
        nameLayout.setError(null);
        emailLayout.setError(null);
        passwordLayout.setError(null);

        String username = usernameInput.getText() != null ? usernameInput.getText().toString().trim() : "";
        String name = nameInput.getText() != null ? nameInput.getText().toString().trim() : "";
        String email = emailInput.getText() != null ? emailInput.getText().toString().trim() : "";
        String password = passwordInput.getText() != null ? passwordInput.getText().toString().trim() : "";

        boolean cancel = false;
        View focusView = null;

        // Validate all fields
        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(name) ||
                TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            if (TextUtils.isEmpty(username)) {
                usernameLayout.setError(getString(R.string.error_field_required));
                focusView = usernameInput;
            }
            if (TextUtils.isEmpty(name)) {
                nameLayout.setError(getString(R.string.error_field_required));
                if (focusView == null) focusView = nameInput;
            }
            if (TextUtils.isEmpty(email)) {
                emailLayout.setError(getString(R.string.error_field_required));
                if (focusView == null) focusView = emailInput;
            }
            if (TextUtils.isEmpty(password)) {
                passwordLayout.setError(getString(R.string.error_field_required));
                if (focusView == null) focusView = passwordInput;
            }
            cancel = true;
        }

        // Validate username format
        if (!TextUtils.isEmpty(username) && !USERNAME_REGEX.matcher(username).matches()) {
            usernameLayout.setError(getString(R.string.error_invalid_username));
            focusView = usernameInput;
            cancel = true;
        }

        // Validate name format
        if (!TextUtils.isEmpty(name) && !NAME_REGEX.matcher(name).matches()) {
            nameLayout.setError(getString(R.string.error_invalid_name));
            if (focusView == null) focusView = nameInput;
            cancel = true;
        }

        // Validate email format
        if (!TextUtils.isEmpty(email) && !EMAIL_REGEX.matcher(email).matches()) {
            emailLayout.setError(getString(R.string.error_invalid_email));
            if (focusView == null) focusView = emailInput;
            cancel = true;
        }

        // Validate password format
        if (!TextUtils.isEmpty(password) && !PASSWORD_REGEX.matcher(password).matches()) {
            passwordLayout.setError(getString(R.string.error_invalid_password));
            if (focusView == null) focusView = passwordInput;
            cancel = true;
        }

        // Validate image selection
        if (selectedImageUri == null) {
            Toast.makeText(this, getString(R.string.error_profile_picture_required), Toast.LENGTH_SHORT).show();
            cancel = true;
        }

        if (cancel) {
            if (focusView != null) {
                focusView.requestFocus();
            }
        } else {
            // Convert Uri to File
            try {
                File imageFile = FileUtil.from(this, selectedImageUri);
                registerViewModel.register(username, name, email, password, imageFile);
            } catch (IOException e) {
                Log.e(TAG, "Error converting uri to file", e);
                Toast.makeText(this, "Error processing image", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
