package com.example.bakehouse;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class UserSessionHelper {
    private static final String PREF_NAME = "UserPrefs";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Context context;

    public UserSessionHelper(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    // Save user login session
    public void saveUserSession(int userId, String email) {
        editor.putInt(KEY_USER_ID, userId);
        editor.putString(KEY_EMAIL, email);
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.apply();
    }

    // Get user ID
    public int getUserId() {
        return sharedPreferences.getInt(KEY_USER_ID, -1);
    }

    // Get user email
    public String getUserEmail() {
        return sharedPreferences.getString(KEY_EMAIL, null);
    }

    // Check if user is logged in
    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    // Clear user session (logout)
    public void clearSession() {
        editor.clear();
        editor.apply();
    }

    // Check login status and redirect if needed
    public void checkLoginStatus() {
        if (!isLoggedIn()) {
            Intent intent = new Intent(context, LogInActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }
}