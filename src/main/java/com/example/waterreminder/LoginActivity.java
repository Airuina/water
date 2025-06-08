package com.example.waterreminder;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.example.waterreminder.utils.UserManager;

public class LoginActivity extends AppCompatActivity {

    private TextInputLayout tilUsername, tilPassword;
    private TextInputEditText etUsername, etPassword;
    private MaterialButton btnLogin, btnRegister;
    private View loginCard;
    private UserManager userManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // 检查是否已登录
        if (isLoggedIn()) {
            startMainActivity();
            return;
        }

        userManager = UserManager.getInstance(this);
        initViews();
        setupListeners();
    }

    private void initViews() {
        tilUsername = findViewById(R.id.tilUsername);
        tilPassword = findViewById(R.id.tilPassword);
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);
        loginCard = findViewById(R.id.loginCard);
    }

    private void setupListeners() {
        btnLogin.setOnClickListener(v -> attemptLogin());
        btnRegister.setOnClickListener(v -> {
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        // 添加焦点变化监听器，清除错误提示
        setupFocusChangeListener(etUsername, tilUsername);
        setupFocusChangeListener(etPassword, tilPassword);
    }

    private void setupFocusChangeListener(TextInputEditText editText, TextInputLayout inputLayout) {
        editText.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                inputLayout.setError(null);
            }
        });
    }

    private void attemptLogin() {
        tilUsername.setError(null);
        tilPassword.setError(null);

        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(password)) {
            tilPassword.setError("请输入密码");
            focusView = etPassword;
            cancel = true;
        }

        if (TextUtils.isEmpty(username)) {
            tilUsername.setError("请输入用户名");
            focusView = etUsername;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            if (userManager.authenticate(username, password)) {
                // 保存登录状态
                saveLoginState(username);
                // 登录成功动画
                showSuccessAnimation();
            } else {
                showLoginError();
            }
        }
    }

    private void saveLoginState(String username) {
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        prefs.edit()
            .putBoolean("is_logged_in", true)
            .putString("current_user", username)
            .apply();
    }

    private boolean isLoggedIn() {
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        return prefs.getBoolean("is_logged_in", false);
    }

    private void showSuccessAnimation() {
        // 禁用所有输入
        setInputsEnabled(false);

        // 延迟跳转，给动画留出时间
        loginCard.postDelayed(this::startMainActivity, 1000);
    }

    private void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    private void showLoginError() {
        tilUsername.setError("用户名或密码错误");
        tilPassword.setError("用户名或密码错误");
        etPassword.setText("");
    }

    private void setInputsEnabled(boolean enabled) {
        etUsername.setEnabled(enabled);
        etPassword.setEnabled(enabled);
        btnLogin.setEnabled(enabled);
        btnRegister.setEnabled(enabled);
    }
} 