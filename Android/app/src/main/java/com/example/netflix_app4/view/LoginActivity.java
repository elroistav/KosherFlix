package com.example.netflix_app4.view;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.netflix_app4.R;
import com.example.netflix_app4.model.User;
import com.example.netflix_app4.viewmodel.LoginViewModel;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class LoginActivity extends AppCompatActivity {
    private TextInputLayout usernameLayout;
    private TextInputLayout passwordLayout;
    private TextInputEditText usernameInput;
    private TextInputEditText passwordInput;
    private LoginViewModel loginViewModel;

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
        loginViewModel.getLoggedInUser().observe(this, user -> {
            if (user != null && user.isLoggedIn()) {
                startActivity(new Intent(this, MainActivity.class)
                        .putExtra("userId", user.getId())
                        .putExtra("token", user.getToken()));
                finish();
            }
        });

        loginViewModel.getError().observe(this, error -> {
            if (error != null) {
                Toast.makeText(this, "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void attemptLogin() {
        usernameLayout.setError(null);
        passwordLayout.setError(null);

        CharSequence usernameText = usernameInput.getText();
        CharSequence passwordText = passwordInput.getText();

        String username = usernameText != null ? usernameText.toString() : "";
        String password = passwordText != null ? passwordText.toString() : "";

        boolean cancel = false;
        TextInputLayout focusView = null;

        if (TextUtils.isEmpty(password)) {
            passwordLayout.setError(getString(R.string.error_password_required));
            focusView = passwordLayout;
            cancel = true;
        } else if (password.length() < 8) {
            passwordLayout.setError(getString(R.string.error_password_length));
            focusView = passwordLayout;
            cancel = true;
        }

        if (TextUtils.isEmpty(username)) {
            usernameLayout.setError(getString(R.string.error_username_required));
            focusView = usernameLayout;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            loginViewModel.login(username, password);
        }
    }
}