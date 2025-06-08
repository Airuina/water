package com.example.waterreminder.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;
import com.example.waterreminder.model.User;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class UserManager {
    private static final String PREF_NAME = "user_data";
    private static final String KEY_CURRENT_USER = "current_user";
    private static final String KEY_USERS = "users";
    private static UserManager instance;
    private final SharedPreferences preferences;
    private final Gson gson;
    private User currentUser;
    private Map<String, User> users;

    private UserManager(Context context) {
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
        loadUsers();
        loadCurrentUser();
    }

    public static synchronized UserManager getInstance(Context context) {
        if (instance == null) {
            instance = new UserManager(context.getApplicationContext());
        }
        return instance;
    }

    private void loadUsers() {
        String usersJson = preferences.getString(KEY_USERS, "{}");
        // 使用 TypeToken 正确指定反序列化类型
        java.lang.reflect.Type type = new com.google.gson.reflect.TypeToken<java.util.HashMap<String, com.example.waterreminder.model.User>>(){}.getType();
        users = gson.fromJson(usersJson, type);
        if (users == null) {
            users = new java.util.HashMap<>();
        }
    }

    private void loadCurrentUser() {
        String userJson = preferences.getString(KEY_CURRENT_USER, null);
        if (userJson != null) {
            currentUser = gson.fromJson(userJson, User.class);
        }
    }

    private void saveUsers() {
        String usersJson = gson.toJson(users);
        preferences.edit().putString(KEY_USERS, usersJson).apply();
    }

    private void saveCurrentUser() {
        if (currentUser != null) {
            String userJson = gson.toJson(currentUser);
            preferences.edit().putString(KEY_CURRENT_USER, userJson).apply();
        } else {
            preferences.edit().remove(KEY_CURRENT_USER).apply();
        }
    }

    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());
            return Base64.encodeToString(hash, Base64.NO_WRAP);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return password; // 降级处理
        }
    }

    public boolean register(String username, String password, String email) {
        if (users.containsKey(username)) {
            return false; // 用户已存在
        }

        String hashedPassword = hashPassword(password);
        User newUser = new User(username, hashedPassword, email);
        users.put(username, newUser);
        saveUsers();
        return true;
    }

    public boolean login(String username, String password) {
        User user = users.get(username);
        if (user != null && user.getPassword().equals(hashPassword(password))) {
            currentUser = user;
            saveCurrentUser();
            return true;
        }
        return false;
    }

    public void logout() {
        currentUser = null;
        saveCurrentUser();
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }

    public boolean updateUserProfile(String newEmail) {
        if (currentUser != null) {
            currentUser.setEmail(newEmail);
            users.put(currentUser.getUsername(), currentUser);
            saveUsers();
            saveCurrentUser();
            return true;
        }
        return false;
    }

    public boolean changePassword(String oldPassword, String newPassword) {
        if (currentUser != null && currentUser.getPassword().equals(hashPassword(oldPassword))) {
            currentUser.setPassword(hashPassword(newPassword));
            users.put(currentUser.getUsername(), currentUser);
            saveUsers();
            saveCurrentUser();
            return true;
        }
        return false;
    }

    public boolean authenticate(String username, String password) {
        User user = users.get(username);
        if (user == null) {
            return false;
        }
        return user.getPassword().equals(hashPassword(password));
    }
}