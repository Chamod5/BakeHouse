package com.example.bakehouse.Sellers;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bakehouse.AppUtils;
import com.example.bakehouse.DbLink;
import com.example.bakehouse.R;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.example.bakehouse.Sellers.SellerLoginActivity;
import com.example.bakehouse.VolleyMultipartRequest;
import com.example.bakehouse.database.AppDatabase;
import com.example.bakehouse.database.Seller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class SellerRegistrationActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri selectedImageUri;

    private EditText nameSeller, phoneSeller, emailSeller, businessNameSeller, addressSeller, passwordSeller, confirmPasswordSeller;
    private ImageView profileImageView;
    private Button registerBtn;
    private ProgressBar progressBar;
    private TextView goToLogin;
    private AppDatabase localDb;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_registration);

        // Linking XML UI components
        nameSeller = findViewById(R.id.seller_rg_name);
        phoneSeller = findViewById(R.id.seller_rg_phone_no);
        emailSeller = findViewById(R.id.seller_rg_email);
        businessNameSeller = findViewById(R.id.seller_rg_business_name);
        addressSeller = findViewById(R.id.seller_rg_business_address);
        passwordSeller = findViewById(R.id.seller_rg_password);
        confirmPasswordSeller = findViewById(R.id.seller_rg_repassword);
        profileImageView = findViewById(R.id.seller_rg_profilePic);
        registerBtn = findViewById(R.id.seller_btn_register);
        progressBar = findViewById(R.id.progressbar);
        goToLogin = findViewById(R.id.seller_login_ask);
        profileImageView.setImageResource(R.drawable.profile_icon);

        localDb = AppDatabase.getInstance(this);


        progressBar.setVisibility(ProgressBar.INVISIBLE);

        // Open image picker on image click
        profileImageView.setOnClickListener(v -> openGallery());

        // Navigate to login
        goToLogin.setOnClickListener(v -> {
            startActivity(new Intent(SellerRegistrationActivity.this, SellerLoginActivity.class));
            finish();
        });

        // Submit form
        registerBtn.setOnClickListener(v -> {
            if (validateInputs()) {
                saveSellerData();
            }
        });
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
            profileImageView.setImageURI(selectedImageUri);
        }
    }

    private boolean validateInputs() {
        String password = passwordSeller.getText().toString().trim();
        String confirmPassword = confirmPasswordSeller.getText().toString().trim();

        // Remove the image selection validation - allow dummy image upload
        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void saveSellerData() {
        progressBar.setVisibility(ProgressBar.VISIBLE);

        try {
            byte[] imageData;

            // Check if user selected an image or use dummy image
            if (selectedImageUri != null) {
                // User selected an image - use it
                InputStream inputStream = getContentResolver().openInputStream(selectedImageUri);
                imageData = getBytes(inputStream);
            } else {
                // No image selected - use dummy image from drawable
                imageData = getDummyImageBytes();
            }

            Toast.makeText(this, "Uploading...", Toast.LENGTH_SHORT).show();
            AppUtils.trustEveryone();

            String url = DbLink.BASE_URL + "register_seller.php";   //database link

            VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(
                    Request.Method.POST,
                    url,
                    response -> {
                        progressBar.setVisibility(ProgressBar.INVISIBLE);
                        String res = new String(response.data);
                        Toast.makeText(SellerRegistrationActivity.this, "Registered successfully!" , Toast.LENGTH_SHORT).show();

                        // On success, move to login
                        if (res.contains("Sign Up Success")) {

                            // Save to local Room database in background thread
                            new Thread(() -> {
                                try {
                                    Seller seller = new Seller(
                                            nameSeller.getText().toString().trim(),
                                            phoneSeller.getText().toString().trim(),
                                            emailSeller.getText().toString().trim(),
                                            businessNameSeller.getText().toString().trim(),
                                            addressSeller.getText().toString().trim(),
                                            passwordSeller.getText().toString().trim(),
                                            imageData
                                    );

                                    localDb.sellerDao().insertSeller(seller);

                                    // Return to main thread for UI operations
                                    runOnUiThread(() -> {
                                        Toast.makeText(SellerRegistrationActivity.this,
                                                "Registered successfully!", Toast.LENGTH_SHORT).show();

                                        // Navigate to login
                                        startActivity(new Intent(SellerRegistrationActivity.this, SellerLoginActivity.class));
                                        finish();
                                    });

                                } catch (Exception e) {
                                    e.printStackTrace();
                                    runOnUiThread(() -> {
                                        Toast.makeText(SellerRegistrationActivity.this,
                                                "Error saving to local database: " + e.getMessage(),
                                                Toast.LENGTH_LONG).show();
                                    });
                                }
                            }).start();
                        }
                    },
                    error -> {
                        progressBar.setVisibility(ProgressBar.INVISIBLE);
                        Toast.makeText(SellerRegistrationActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
                    }
            ) {
                @Override
                public Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("name", nameSeller.getText().toString().trim());
                    params.put("phone_number", phoneSeller.getText().toString().trim());
                    params.put("email", emailSeller.getText().toString().trim());
                    params.put("business_name", businessNameSeller.getText().toString().trim());
                    params.put("address", addressSeller.getText().toString().trim());
                    params.put("password", passwordSeller.getText().toString().trim());
                    return params;
                }

                @Override
                public Map<String, DataPart> getByteData() {
                    Map<String, DataPart> data = new HashMap<>();
                    data.put("profile_image", new DataPart("profile.jpg", imageData, "image/jpeg"));
                    return data;
                }
            };

            Volley.newRequestQueue(this).add(multipartRequest);

        } catch (IOException e) {
            progressBar.setVisibility(ProgressBar.INVISIBLE);
            e.printStackTrace();
            Toast.makeText(this, "Failed to read image", Toast.LENGTH_SHORT).show();
        }
    }

    // New method to get dummy image bytes from drawable
    private byte[] getDummyImageBytes() throws IOException {
        InputStream inputStream = getResources().openRawResource(R.drawable.profile_icon);
        return getBytes(inputStream);
    }

    private byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];
        int len;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }
}