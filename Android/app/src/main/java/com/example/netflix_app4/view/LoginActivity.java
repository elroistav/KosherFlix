package com.example.netflix_app4.view;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.netflix_app4.R;
import com.example.netflix_app4.viewmodel.LoginViewModel;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {
    // View references
    private TextInputLayout usernameLayout;
    private TextInputLayout passwordLayout;
    private TextInputEditText usernameInput;
    private TextInputEditText passwordInput;
    private LoginViewModel loginViewModel;

    // Regex patterns and constants
    private static final Pattern USERNAME_REGEX = Pattern.compile("^[A-Za-z0-9_-]{3,15}$");
    private static final Pattern PASSWORD_REGEX = Pattern.compile("^(?=.*[a-zA-Z])(?=.*\\d)[A-Za-z\\d!@#$%^&*]{8,}$");
    private static final int MIN_PASSWORD_LENGTH = 8;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initializeViews();
        setupViewModel();
        observeViewModel();
    }

    private void initializeViews() {
        usernameLayout = findViewById(R.id.usernameLayout);
        passwordLayout = findViewById(R.id.passwordLayout);
        usernameInput = findViewById(R.id.usernameInput);
        passwordInput = findViewById(R.id.passwordInput);
        findViewById(R.id.loginButton).setOnClickListener(v -> attemptLogin());
    }

    private void setupViewModel() {
        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
    }

    private void observeViewModel() {
        loginViewModel.getLoginResult().observe(this, success -> {
            if (success) {
                startActivity(new Intent(this, MainActivity.class));
                finish();
            }
        });

        loginViewModel.getError().observe(this, error -> {
            if (error != null) {
                Toast.makeText(this, getString(R.string.error_prefix, error), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void attemptLogin() {
        // Reset errors
        usernameLayout.setError(null);
        passwordLayout.setError(null);

        String username = usernameInput.getText() != null ? usernameInput.getText().toString() : "";
        String password = passwordInput.getText() != null ? passwordInput.getText().toString() : "";

        boolean cancel = false;
        TextInputLayout focusView = null;

        // Validate username and password
        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            if (TextUtils.isEmpty(username)) {
                usernameLayout.setError(getString(R.string.error_username_required));
                focusView = usernameLayout;
            }
            if (TextUtils.isEmpty(password)) {
                passwordLayout.setError(getString(R.string.error_password_required));
                if (focusView == null) focusView = passwordLayout;
            }
            cancel = true;
        }

        // Username regex check
        if (!TextUtils.isEmpty(username) && !USERNAME_REGEX.matcher(username).matches()) {
            usernameLayout.setError(getString(R.string.error_username_format));
            if (focusView == null) focusView = usernameLayout;
            cancel = true;
        }

        // Password validation
        if (!TextUtils.isEmpty(password)) {
            if (password.length() < MIN_PASSWORD_LENGTH) {
                passwordLayout.setError(getString(R.string.error_password_length));
                if (focusView == null) focusView = passwordLayout;
                cancel = true;
            } else if (!PASSWORD_REGEX.matcher(password).matches()) {
                passwordLayout.setError(getString(R.string.error_password_format));
                if (focusView == null) focusView = passwordLayout;
                cancel = true;
            }
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            loginViewModel.login(username, password);
        }
    }
}