package com.example.bakehouse.Categories;

import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.bakehouse.DbLink;
import com.example.bakehouse.R;
import com.example.bakehouse.adapters.BirthdayCakeAdapter;
import com.example.bakehouse.adapters.WeddingCakeAdapter;
import com.example.bakehouse.models.WeddingCakeModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class WeddingCakeActivity extends AppCompatActivity {

    private static final String TAG = "WeddingCakeActivity";
    private RecyclerView recyclerView;
    private WeddingCakeAdapter cakeAdapter;
    private ArrayList<WeddingCakeModel> weddingCakeList;
    private ProgressBar progressBar;

    private static final String URL = DbLink.BASE_URL + "get_wedding_cakes.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wedding_cake);

        initViews();
        setupRecyclerView();
        loadBirthdayCakes();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressBar); // Make sure this exists in your layout

        // If progressBar doesn't exist in layout, create it programmatically
        if (progressBar == null) {
            Log.w(TAG, "ProgressBar not found in layout");
        }
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        weddingCakeList = new ArrayList<>();
        cakeAdapter = new WeddingCakeAdapter(this, weddingCakeList);
        recyclerView.setAdapter(cakeAdapter);
    }

    private void loadBirthdayCakes() {
        showProgressBar(true);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, URL, null,
                response -> {
                    showProgressBar(false);
                    try {
                        Log.d(TAG, "Response: " + response.toString());

                        if (response.getString("status").equals("success")) {
                            JSONArray cakesArray = response.getJSONArray("cakes");

                            if (cakesArray.length() == 0) {
                                Toast.makeText(this, "No wedding cakes available", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            weddingCakeList.clear(); // Clear existing data

                            for (int i = 0; i < cakesArray.length(); i++) {
                                JSONObject cakeObj = cakesArray.getJSONObject(i);
                                WeddingCakeModel cake = new WeddingCakeModel();

                                cake.setId(cakeObj.getInt("cake_id"));
                                cake.setTitle(cakeObj.getString("cake_title"));
                                cake.setPrice(cakeObj.getDouble("cake_price"));
                                cake.setDescription(cakeObj.getString("cake_description"));

                                // Handle images (Base64 Strings)
                                ArrayList<byte[]> decodedImages = new ArrayList<>();
                                JSONArray images = cakeObj.getJSONArray("cake_images");

                                for (int j = 0; j < images.length(); j++) {
                                    try {
                                        String base64Image = images.getString(j);
                                        if (!base64Image.isEmpty()) {
                                            byte[] decodedImage = Base64.decode(base64Image, Base64.DEFAULT);
                                            decodedImages.add(decodedImage);
                                        }
                                    } catch (Exception e) {
                                        Log.e(TAG, "Error decoding image " + j + " for cake " + cake.getId(), e);
                                    }
                                }

                                cake.setImages(decodedImages);
                                weddingCakeList.add(cake);
                            }

                            cakeAdapter.notifyDataSetChanged();
                            //Toast.makeText(this, "Loaded " + weddingCakeList.size() + " wedding cakes", Toast.LENGTH_SHORT).show();

                        } else {
                            String message = response.optString("message", "No wedding cakes found");
                            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                            Log.w(TAG, "Server response: " + message);
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Parsing error", e);
                        Toast.makeText(this, "Error loading cakes: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    showProgressBar(false);
                    Log.e(TAG, "Network error", error);
                    String errorMessage = "Failed to load cakes";
                    if (error.networkResponse != null) {
                        errorMessage += " (Code: " + error.networkResponse.statusCode + ")";
                    }
                    Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
                }
        );

        // Add timeout settings
        request.setRetryPolicy(new com.android.volley.DefaultRetryPolicy(
                10000, // 10 seconds timeout
                1, // no retries
                1.0f
        ));

        Volley.newRequestQueue(this).add(request);
    }

    private void showProgressBar(boolean show) {
        if (progressBar != null) {
            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }
}