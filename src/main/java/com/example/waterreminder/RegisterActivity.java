package com.example.waterreminder;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
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

public class RegisterActivity extends AppCompatActivity {

    private TextInputLayout tilUsername, tilEmail, tilPassword, tilConfirmPassword;
    private TextInputEditText etUsername, etEmail, etPassword, etConfirmPassword;
    private MaterialButton btnRegister, btnBack;
    private View registerCard;
    private UserManager userManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        userManager = UserManager.getInstance(this);
        initViews();
        setupListeners();
    }

    private void initViews() {
        tilUsername = findViewById(R.id.tilUsername);
        tilEmail = findViewById(R.id.tilEmail);
        tilPassword = findViewById(R.id.tilPassword);
        tilConfirmPassword = findViewById(R.id.tilConfirmPassword);
        
        etUsername = findViewById(R.id.etUsername);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        
        btnRegister = findViewById(R.id.btnRegister);
        btnBack = findViewById(R.id.btnBack);
        registerCard = findViewById(R.id.registerCard);
    }

    private void setupListeners() {
        btnRegister.setOnClickListener(v -> attemptRegister());
        btnBack.setOnClickListener(v -> finish());

        // 添加焦点变化监听器，清除错误提示
        setupFocusChangeListener(etUsername, tilUsername);
        setupFocusChangeListener(etEmail, tilEmail);
        setupFocusChangeListener(etPassword, tilPassword);
        setupFocusChangeListener(etConfirmPassword, tilConfirmPassword);
    }

    private void setupFocusChangeListener(TextInputEditText editText, TextInputLayout inputLayout) {
        editText.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                inputLayout.setError(null);
            }
        });
    }

    private void attemptRegister() {
        // 重置错误提示
        tilUsername.setError(null);
        tilEmail.setError(null);
        tilPassword.setError(null);
        tilConfirmPassword.setError(null);

        String username = etUsername.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString();
        String confirmPassword = etConfirmPassword.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // 检查确认密码
        if (TextUtils.isEmpty(confirmPassword)) {
            tilConfirmPassword.setError("请确认密码");
            focusView = etConfirmPassword;
            cancel = true;
            shakeView(tilConfirmPassword);
        } else if (!password.equals(confirmPassword)) {
            tilConfirmPassword.setError("两次输入的密码不一致");
            focusView = etConfirmPassword;
            cancel = true;
            shakeView(tilConfirmPassword);
        }

        // 检查密码
        if (TextUtils.isEmpty(password)) {
            tilPassword.setError("请输入密码");
            focusView = etPassword;
            cancel = true;
            shakeView(tilPassword);
        } else if (password.length() < 6) {
            tilPassword.setError("密码长度至少为6位");
            focusView = etPassword;
            cancel = true;
            shakeView(tilPassword);
        }

        // 检查邮箱
        if (TextUtils.isEmpty(email)) {
            tilEmail.setError("请输入邮箱");
            focusView = etEmail;
            cancel = true;
            shakeView(tilEmail);
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.setError("请输入有效的邮箱地址");
            focusView = etEmail;
            cancel = true;
            shakeView(tilEmail);
        }

        // 检查用户名
        if (TextUtils.isEmpty(username)) {
            tilUsername.setError("请输入用户名");
            focusView = etUsername;
            cancel = true;
            shakeView(tilUsername);
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            // 尝试注册
            if (userManager.register(username, password, email)) {
                showSuccessAnimation();
            } else {
                showRegisterError();
            }
        }
    }

    private void shakeView(View view) {
        Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
        view.startAnimation(shake);
    }

    private void showSuccessAnimation() {
        // 禁用所有输入
        setInputsEnabled(false);

        // 创建圆形收缩动画
        int cx = registerCard.getWidth() / 2;
        int cy = registerCard.getHeight() / 2;
        float finalRadius = (float) Math.hypot(cx, cy);

        Animator anim = ViewAnimationUtils.createCircularReveal(registerCard, cx, cy, finalRadius, 0);
        anim.setDuration(500);

        // 动画结束后关闭活动
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                registerCard.setVisibility(View.INVISIBLE);
                Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        anim.start();
    }

    private void showRegisterError() {
        tilUsername.setError("用户名已存在");
        shakeView(tilUsername);
    }

    private void setInputsEnabled(boolean enabled) {
        etUsername.setEnabled(enabled);
        etEmail.setEnabled(enabled);
        etPassword.setEnabled(enabled);
        etConfirmPassword.setEnabled(enabled);
        btnRegister.setEnabled(enabled);
        btnBack.setEnabled(enabled);
    }
} 