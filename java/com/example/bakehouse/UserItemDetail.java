package com.example.bakehouse;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bakehouse.adapters.CakeImageAdapter;
import com.example.bakehouse.database.AppDatabase;
import com.example.bakehouse.database.CartItem;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class UserItemDetail extends AppCompatActivity {

    private static final String TAG = "UserItemDetail";

    private TextView titleText, priceText, descriptionText, categoryText;
    private ImageView mainImage;
    private RecyclerView imagesRecyclerView;
    private CakeImageAdapter imageAdapter;
    private List<byte[]> imageList = new ArrayList<>();
    private Button addToCartButton, orderButton;

    private int cakeId;
    private int sellerId;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_item_detail);

        // Get cake_id and user_id from Intent
        cakeId = getIntent().getIntExtra("cake_id", -1);
        userId = getIntent().getIntExtra("user_id", -1);

        Log.d(TAG, "Received cake_id: " + cakeId + ", user_id: " + userId);

        // Validate that we have the required data
        if (cakeId == -1) {
            Toast.makeText(this, "Invalid cake ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        if (userId == -1) {
            Toast.makeText(this, "User session invalid. Please login again.", Toast.LENGTH_SHORT).show();
            // Redirect to login
            Intent loginIntent = new Intent(this, LogInActivity.class);
            loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(loginIntent);
            finish();
            return;
        }

        initViews();
        setupRecyclerView();
        loadCakeDetails();

        orderButton.setOnClickListener(v -> {
            // Check if sellerId is valid before proceeding
            if (sellerId == -1) {
                Toast.makeText(this, "Seller information not loaded. Please try again.", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(UserItemDetail.this, OrderingActivity.class);
            intent.putExtra("cake_id", cakeId);
            intent.putExtra("seller_id", sellerId);
            intent.putExtra("user_id", userId);
            startActivity(intent);
        });
    }

    private void initViews() {
        titleText = findViewById(R.id.cakeTitle);
        priceText = findViewById(R.id.cakePrice);
        descriptionText = findViewById(R.id.cakeDescription);
        categoryText = findViewById(R.id.cakeCategory);
        mainImage = findViewById(R.id.mainCakeImage);
        imagesRecyclerView = findViewById(R.id.imagesRecyclerView);
        addToCartButton = findViewById(R.id.addToCartButton);
        orderButton = findViewById(R.id.orderButton);
    }

    private void setupRecyclerView() {
        imageAdapter = new CakeImageAdapter(this, imageList, this::onImageClick);
        imagesRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        imagesRecyclerView.setAdapter(imageAdapter);
    }

    private void onImageClick(int position) {
        if (position < imageList.size()) {
            byte[] imageData = imageList.get(position);
            Bitmap bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
            mainImage.setImageBitmap(bitmap);
        }
    }

    private void loadCakeDetails() {
        new Thread(() -> {
            try {
                String urlString = DbLink.BASE_URL + "fetch_cake_details.php?cake_id=" + cakeId;
                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(10000);
                connection.setReadTimeout(10000);

                InputStream input;
                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    input = new BufferedInputStream(connection.getInputStream());
                } else {
                    input = new BufferedInputStream(connection.getErrorStream());
                }

                BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                StringBuilder result = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }

                JSONObject response = new JSONObject(result.toString());

                runOnUiThread(() -> {
                    try {
                        if (response.getString("status").equals("success")) {
                            JSONObject cake = response.getJSONObject("cake");

                            sellerId = cake.optInt("seller_id", -1);
                            Log.d(TAG, "Loaded seller_id: " + sellerId);

                            // Set cake details
                            titleText.setText(cake.getString("cake_title"));
                            priceText.setText("Rs. " + cake.getString("cake_price"));
                            descriptionText.setText(cake.getString("cake_description"));
                            categoryText.setText(cake.getString("cake_category"));

                            // Load images
                            JSONArray imagesArray = cake.getJSONArray("cake_images");
                            imageList.clear();

                            for (int i = 0; i < imagesArray.length(); i++) {
                                String imageString = imagesArray.getString(i);
                                byte[] imageData = Base64.decode(imageString, Base64.DEFAULT);
                                imageList.add(imageData);
                            }

                            imageAdapter.notifyDataSetChanged();

                            // Set first image as main image if available
                            if (!imageList.isEmpty()) {
                                Bitmap bitmap = BitmapFactory.decodeByteArray(imageList.get(0), 0, imageList.get(0).length);
                                mainImage.setImageBitmap(bitmap);
                            } else {
                                mainImage.setImageResource(R.drawable.ic_cake_placeholder);
                            }

                        } else {
                            Toast.makeText(this, "Error loading cake details", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Parse error: " + e.getMessage());
                        Toast.makeText(this, "Parse error", Toast.LENGTH_SHORT).show();
                    }
                });

                connection.disconnect();

            } catch (Exception e) {
                Log.e(TAG, "Network error: " + e.getMessage());
                runOnUiThread(() -> {
                    Toast.makeText(this, "Network error", Toast.LENGTH_SHORT).show();
                });
            }
        }).start();

        // Add to cart functionality
        addToCartButton.setOnClickListener(v -> {
            // Validate user before adding to cart
            if (userId == -1) {
                Toast.makeText(this, "Please login to add items to cart", Toast.LENGTH_SHORT).show();
                return;
            }

            new Thread(() -> {
                try {
                    CartItem item = new CartItem();
                    item.cakeId = cakeId;
                    item.title = titleText.getText().toString();
                    item.price = priceText.getText().toString();
                    item.description = descriptionText.getText().toString();
                    item.category = categoryText.getText().toString();
                    if (!imageList.isEmpty()) {
                        item.image = imageList.get(0); // Save first image
                    }

                    AppDatabase db = AppDatabase.getInstance(getApplicationContext());
                    db.cartDao().insertCartItem(item);

                    runOnUiThread(() -> Toast.makeText(this, "Item added to cart!", Toast.LENGTH_SHORT).show());

                } catch (Exception e) {
                    Log.e(TAG, "Error saving to cart: " + e.getMessage());
                    runOnUiThread(() -> Toast.makeText(this, "Error adding to cart", Toast.LENGTH_SHORT).show());
                }
            }).start();
        });
    }
}