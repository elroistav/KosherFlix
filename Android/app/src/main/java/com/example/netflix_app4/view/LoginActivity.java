package com.example.netflix_app4.view;

import com.google.android.material.snackbar.Snackbar;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import android.util.Log;
import com.example.netflix_app4.network.Config;

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

    private static final String TAG = "LoginActivity";

    // Regex patterns and constants
    private static final Pattern USERNAME_REGEX = Pattern.compile("^[A-Za-z0-9_-]{3,15}$");
    private static final Pattern PASSWORD_REGEX = Pattern.compile(
            "^(?=.*[a-zA-Z])(?=.*\\d).{8,}$"
    );
    private static final int MIN_PASSWORD_LENGTH = 8;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: LoginActivity started");
        setContentView(R.layout.activity_login);
        Config.loadConfig(this);

        initializeViews();
        setupViewModel();
        observeViewModel();
    }

    private void initializeViews() {
        Log.d(TAG, "initializeViews: Initializing view components");
        usernameLayout = findViewById(R.id.usernameLayout);
        passwordLayout = findViewById(R.id.passwordLayout);
        usernameInput = findViewById(R.id.usernameInput);
        passwordInput = findViewById(R.id.passwordInput);
        findViewById(R.id.loginButton).setOnClickListener(v -> {
            Log.d(TAG, "Login button clicked");
            attemptLogin();
        });

        findViewById(R.id.goToRegisterButton).setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });
    }

    private void setupViewModel() {
        Log.d(TAG, "setupViewModel: Setting up ViewModel");
        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
    }

    private void observeViewModel() {
        Log.d(TAG, "observeViewModel: Observing ViewModel LiveData");

        loginViewModel.getLoginResult().observe(this, success -> {
            if (success) {
                Log.d(TAG, "observeViewModel: Login successful");

                loginViewModel.getToken().observe(this, token -> {
                    if (token != null) {
                        Log.d(TAG, "Token received: " + token);
                        Intent intent = new Intent(this, HomeScreenActivity.class);
                        intent.putExtra("USER_TOKEN", token);
                        startActivity(intent);
                        finish();
                    }
                });
            }
        });

        loginViewModel.getError().observe(this, error -> {
            if (error != null) {
                Log.d(TAG, "observeViewModel: Login failed with error: " + error);

                View rootView = findViewById(android.R.id.content);
                Snackbar.make(rootView, error, Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void attemptLogin() {
        Log.d(TAG, "attemptLogin: Validating login credentials");

        // Reset errors
        usernameLayout.setError(null);
        passwordLayout.setError(null);

        String username = usernameInput.getText() != null ? usernameInput.getText().toString().trim() : "";
        String password = passwordInput.getText() != null ? passwordInput.getText().toString().trim() : "";

        Log.d(TAG, "attemptLogin: Username = " + username);
        Log.d(TAG, "attemptLogin: Password length = " + password.length());

        boolean cancel = false;
        TextInputLayout focusView = null;

        // Validate username and password
        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            Log.d(TAG, "attemptLogin: Empty username or password");
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
            Log.d(TAG, "attemptLogin: Invalid username format");
            usernameLayout.setError(getString(R.string.error_username_format));
            if (focusView == null) focusView = usernameLayout;
            cancel = true;
        }

        // Password validation
        if (!TextUtils.isEmpty(password)) {
            if (password.length() < MIN_PASSWORD_LENGTH) {
                Log.d(TAG, "attemptLogin: Password too short");
                passwordLayout.setError(getString(R.string.error_password_length));
                if (focusView == null) focusView = passwordLayout;
                cancel = true;
            } else if (!PASSWORD_REGEX.matcher(password).matches()) {
                Log.d(TAG, "attemptLogin: Invalid password format");
                passwordLayout.setError(getString(R.string.error_password_format));
                if (focusView == null) focusView = passwordLayout;
                cancel = true;
            }
        }

        if (cancel) {
            Log.d(TAG, "attemptLogin: Validation failed, focusing on error");
            focusView.requestFocus();
        } else {
            Log.d(TAG, "attemptLogin: Validation passed, attempting login with ViewModel");
            loginViewModel.login(username, password);
        }
    }


}
