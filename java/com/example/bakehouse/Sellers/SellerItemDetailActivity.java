package com.example.bakehouse.Sellers;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bakehouse.AppUtils;
import com.example.bakehouse.DbLink;
import com.example.bakehouse.R;
import com.example.bakehouse.adapters.CakeImageAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class SellerItemDetailActivity extends AppCompatActivity {

    private static final String TAG = "SellerItemDetail";

    private TextView titleText, priceText, descriptionText, categoryText;
    private ImageView mainImage;
    private RecyclerView imagesRecyclerView;
    private CakeImageAdapter imageAdapter;
    private List<byte[]> imageList = new ArrayList<>();

    private int cakeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_item_detail);

        // Get cake ID from intent
        cakeId = getIntent().getIntExtra("cake_id", -1);

        if (cakeId == -1) {
            Toast.makeText(this, "Invalid cake ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        setupRecyclerView();
        loadCakeDetails();
    }

    private void initViews() {
        titleText = findViewById(R.id.cakeTitle);
        priceText = findViewById(R.id.cakePrice);
        descriptionText = findViewById(R.id.cakeDescription);
        categoryText = findViewById(R.id.cakeCategory);
        mainImage = findViewById(R.id.mainCakeImage);
        imagesRecyclerView = findViewById(R.id.imagesRecyclerView);
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
    }
}