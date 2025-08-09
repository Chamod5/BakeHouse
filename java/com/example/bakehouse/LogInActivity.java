package com.example.bakehouse;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LogInActivity extends AppCompatActivity {

    private static final String TAG = "LogInActivity";

    Button loginButton;
    EditText username, password;
    TextView signUpText;
    ProgressBar progressBar;
    Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        progressBar = findViewById(R.id.progressbar);
        progressBar.setVisibility(View.GONE);

        username = findViewById(R.id.loginUsername);
        password = findViewById(R.id.loginPassword);
        loginButton = findViewById(R.id.loginButton);
        signUpText = findViewById(R.id.signuplink);

        signUpText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LogInActivity.this, RegistrationActivity.class));
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                loginUser();
            }
        });
    }

    private void loginUser() {
        String userEmail = username.getText().toString().trim();
        String userPassword = password.getText().toString().trim();

        if (TextUtils.isEmpty(userEmail)) {
            Toast.makeText(this, "Email is empty", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
            return;
        }

        if (TextUtils.isEmpty(userPassword)) {
            Toast.makeText(this, "Password is empty", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
            return;
        }

        AppUtils.trustEveryone();
        progressBar.setVisibility(View.VISIBLE);

        String url = DbLink.BASE_URL + "login.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    progressBar.setVisibility(View.GONE);

                    // Log the response to debug
                    //Log.d(TAG, "Server response: " + response);

                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        String status = jsonResponse.getString("status");
                        String message = jsonResponse.getString("message");

                        if (status.equals("success")) {
                            int userId = jsonResponse.getInt("user_id");

                            //Log.d(TAG, "Login successful - user_id: " + userId + ", email: " + userEmail);

                            Toast.makeText(LogInActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();

                            // Save user data to SharedPreferences
                            saveUserData(userId, userEmail);

                            // Redirect to UserMainActivity with user_id and email
                            Intent intent = new Intent(LogInActivity.this, UserMainActivity.class);
                            intent.putExtra("user_id", userId);
                            intent.putExtra("email", userEmail);

                            //Log.d(TAG, "Passing to UserMainActivity - user_id: " + userId + ", email: " + userEmail);

                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(LogInActivity.this, message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        //Log.e(TAG, "JSON parsing error: " + e.getMessage());
                        //Log.e(TAG, "Response was: " + response);

                        // Handle old response format or JSON parsing error
                        if (response.trim().equals("Login Success")) {
                            Toast.makeText(LogInActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(LogInActivity.this, UserMainActivity.class);
                            intent.putExtra("email", userEmail);
                            // Note: user_id won't be available in this case
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(LogInActivity.this, "Unexpected response format: " + response, Toast.LENGTH_LONG).show();
                        }
                    }
                },
                error -> {
                    progressBar.setVisibility(View.GONE);
                    //Log.e(TAG, "Network error: " + error.getMessage());
                    Toast.makeText(LogInActivity.this, "Network Error", Toast.LENGTH_SHORT).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("email", userEmail);
                params.put("password", userPassword);
                return params;
            }
        };

        Volley.newRequestQueue(this).add(stringRequest);
    }

    private void saveUserData(int userId, String email) {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("user_id", userId);
        editor.putString("email", email);
        editor.putBoolean("isLoggedIn", true);
        editor.apply();

        //Log.d(TAG, "User data saved to SharedPreferences - user_id: " + userId);
    }
}