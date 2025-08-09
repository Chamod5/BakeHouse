package com.example.bakehouse;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.Volley;
import com.example.bakehouse.Sellers.SellerLoginActivity;
import com.example.bakehouse.Sellers.SellerRegistrationActivity;
import com.example.bakehouse.database.AppDatabase;
import com.example.bakehouse.database.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;

public class RegistrationActivity extends AppCompatActivity {

    EditText name, username, email, password, repassword;

    TextView goToLogin;
    ImageView profilePic;
    Button registerBtn;
    ProgressBar progressBar;
    Bitmap bitmap;

    AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        initializeViews();

        goToLogin.setOnClickListener(v -> {
            startActivity(new Intent(RegistrationActivity.this, LogInActivity.class));
            finish();
        });



        setupDatabase();
        setupClickListeners();
    }

    private void initializeViews() {
        name = findViewById(R.id.rg_name);
        username = findViewById(R.id.rg_Username);
        email = findViewById(R.id.rg_email);
        password = findViewById(R.id.rg_password);
        repassword = findViewById(R.id.rg_repassword);
        profilePic = findViewById(R.id.rg_profilePic);
        registerBtn = findViewById(R.id.btn_register);
        progressBar = findViewById(R.id.progressbar);
        profilePic.setImageResource(R.drawable.profile_icon);
        goToLogin = findViewById(R.id.goToLogin);
    }

    private void setupDatabase() {
        db = AppDatabase.getInstance(getApplicationContext());
    }

    private void setupClickListeners() {
        profilePic.setOnClickListener(v -> {
            Intent pickImage = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(pickImage, 1);
        });

        registerBtn.setOnClickListener(v -> {
            if (validateInput()) {
                progressBar.setVisibility(View.VISIBLE);
                checkUserExistsAndRegister();

            }
        });
    }

    private boolean validateInput() {
        String nameText = name.getText().toString().trim();
        String usernameText = username.getText().toString().trim();
        String emailText = email.getText().toString().trim();
        String passwordText = password.getText().toString().trim();
        String repasswordText = repassword.getText().toString().trim();


        if (TextUtils.isEmpty(nameText)) {
            Toast.makeText(this, "Name is required", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (TextUtils.isEmpty(usernameText)) {
            Toast.makeText(this, "Username is required", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (TextUtils.isEmpty(emailText)) {
            Toast.makeText(this, "Email is required", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailText).matches()) {
            Toast.makeText(this, "Please enter a valid email", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (TextUtils.isEmpty(passwordText)) {
            Toast.makeText(this, "Password is required", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (passwordText.length() < 6) {
            Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!passwordText.equals(repasswordText)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void checkUserExistsAndRegister() {

        Executors.newSingleThreadExecutor().execute(() -> {
            String emailText = email.getText().toString().trim();
            String usernameText = username.getText().toString().trim();

            // Check if user already exists
            int emailExists = db.userDao().checkEmailExists(emailText);
            int usernameExists = db.userDao().checkUsernameExists(usernameText);

            runOnUiThread(() -> {
                if (emailExists > 0) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Email already exists", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (usernameExists > 0) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Username already exists", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Proceed with registration
                registerUser();
            });
        });
    }

    private void registerUser() {
        Toast.makeText(this, "Registering user...", Toast.LENGTH_SHORT).show();

        //Save server
        sendToServer();

        //Save locally
        saveToLocalDatabase();
        Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show();

    }

    private void saveToLocalDatabase() {
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                User user = new User();
                user.name = name.getText().toString().trim();
                user.username = username.getText().toString().trim();
                user.email = email.getText().toString().trim();
                user.password = password.getText().toString().trim();
                user.profileImage = (bitmap != null) ? getFileDataFromDrawable(bitmap) : null;

                long userId = db.userDao().insertUser(user);

                runOnUiThread(() -> {
                //    Toast.makeText(this, "User saved locally", Toast.LENGTH_SHORT).show();
                    Log.d("Registration", "User saved locally with ID: " + userId);
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                //    Toast.makeText(this, "Error saving locally: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("Registration", "Error saving locally", e);
                });
            }
        });
    }

    /*
    private void sendToServer() {
        AppUtils.trustEveryone();

        String url = DbLink.BASE_URL + "register_user.php";
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, url,
                response -> {
                    progressBar.setVisibility(View.GONE);
                    try {
                        JSONObject jsonObject = new JSONObject(new String(response.data));
                        String status = jsonObject.getString("status");
                        String message = jsonObject.getString("message");

                        if (status.equals("success")) {
                            Toast.makeText(this, "Registration successful!", Toast.LENGTH_LONG).show();

                            // Navigate to login
                            Intent intent = new Intent(RegistrationActivity.this, LogInActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        Toast.makeText(this, "Registration completed locally", Toast.LENGTH_LONG).show();
                        Log.e("Registration", "JSON parsing error", e);

                        // Still navigate to login even if server response is unclear
                        Intent intent = new Intent(RegistrationActivity.this, LogInActivity.class);
                        startActivity(intent);
                        finish();
                    }
                },
                error -> {
                    progressBar.setVisibility(View.GONE);
                    String errorMsg = "Server registration failed, but user saved locally";

                    if (error.networkResponse != null) {
                        errorMsg += "\nStatus Code: " + error.networkResponse.statusCode;
                        Log.e("Registration", "Network error: " + error.networkResponse.statusCode);
                    } else {
                        errorMsg += ": " + error.getMessage();
                        Log.e("Registration", "Network error: " + error.getMessage());
                    }

                    Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show();

                    // Still allow user to proceed to login
                    Intent intent = new Intent(RegistrationActivity.this, LogInActivity.class);
                    startActivity(intent);
                    finish();
                }
        ) {
            @Override
            public Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("name", name.getText().toString().trim());
                params.put("username", username.getText().toString().trim());
                params.put("email", email.getText().toString().trim());
                params.put("password", password.getText().toString().trim());
                return params;
            }

            @Override
            public Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                if (bitmap != null) {
                    params.put("profile_image", new DataPart("profile.jpg", getFileDataFromDrawable(bitmap)));
                }
                return params;
            }
        };

        Volley.newRequestQueue(this).add(volleyMultipartRequest);
    }
    */

    private void sendToServer() {
        AppUtils.trustEveryone();

        String url = DbLink.BASE_URL + "register_user.php";
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, url,
                response -> {
                    progressBar.setVisibility(View.GONE);
                    try {
                        JSONObject jsonObject = new JSONObject(new String(response.data));
                        String status = jsonObject.getString("status");
                        String message = jsonObject.getString("message");

                        if (status.equals("success")) {
                            Toast.makeText(this, "Registration successful!", Toast.LENGTH_LONG).show();
                            saveToLocalDatabase();  // Now saving only if server returns success

                            // Navigate to login
                            Intent intent = new Intent(RegistrationActivity.this, LogInActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                      //  Toast.makeText(this, "Unexpected server response", Toast.LENGTH_LONG).show();
                     //   Log.e("Registration", "JSON parsing error", e);
                    }
                },
                error -> {
                    progressBar.setVisibility(View.GONE);
                    String errorMsg = "Server registration failed.";

                    if (error.networkResponse != null) {
                        errorMsg += "\nStatus Code: " + error.networkResponse.statusCode;
                        Log.e("Registration", "Network error: " + error.networkResponse.statusCode);
                    } else {
                        errorMsg += ": " + error.getMessage();
                        Log.e("Registration", "Network error: " + error.getMessage());
                    }

                    Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show();
                }
        ) {
            @Override
            public Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("name", name.getText().toString().trim());
                params.put("username", username.getText().toString().trim());
                params.put("email", email.getText().toString().trim());
                params.put("password", password.getText().toString().trim());
                return params;
            }

            @Override
            public Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                byte[] imageBytes;

                if (bitmap != null) {
                    imageBytes = getFileDataFromDrawable(bitmap);
                } else {
                    // Load dummy image from resources
                    Bitmap defaultBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.profile_icon);
                    imageBytes = getFileDataFromDrawable(defaultBitmap);
                }

                params.put("profile_image", new DataPart("profile.jpg", imageBytes));
                return params;
            }
        };

        Volley.newRequestQueue(this).add(volleyMultipartRequest);
    }


    public byte[] getFileDataFromDrawable(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                profilePic.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Error loading image", Toast.LENGTH_SHORT).show();
            }
        }
    }
}