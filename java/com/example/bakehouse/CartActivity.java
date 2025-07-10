package com.example.bakehouse;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class CartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        Intent intent = getIntent();

        String name = intent.getStringExtra("name");
        String username = intent.getStringExtra("username");
        String email = intent.getStringExtra("email");
        String imageURL = intent.getStringExtra("imageUrl");


        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setSelectedItemId(R.id.bottom_cart);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.bottom_cart) {
                return true;
            } else if (itemId == R.id.bottom_search) {
                //startActivity(new Intent(getApplicationContext(), SearchActivity.class));
                Intent intent2 = new Intent(getApplicationContext(), SearchActivity.class);
                intent2.putExtra("name", name);
                intent2.putExtra("username", username);
                intent2.putExtra("email", email);
                intent2.putExtra("imageUrl", imageURL);
                startActivity(intent2);
                finish();
                return true;
            } else if (itemId == R.id.bottom_home) {
                //startActivity(new Intent(getApplicationContext(), HomePageActivity.class));
                Intent intent2 = new Intent(getApplicationContext(), HomePageActivity.class);
                intent2.putExtra("name", name);
                intent2.putExtra("username", username);
                intent2.putExtra("email", email);
                intent2.putExtra("imageUrl", imageURL);
                startActivity(intent2);
                finish();
                return true;
            } else if (itemId == R.id.bottom_profile) {
               //startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                Intent intent2 = new Intent(getApplicationContext(), ProfileActivity.class);
                intent2.putExtra("name", name);
                intent2.putExtra("username", username);
                intent2.putExtra("email", email);
                intent2.putExtra("imageUrl", imageURL);
                startActivity(intent2);
                finish();
                return true;
            }
            return false;
        });


        // Handle the back button press
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Navigate to HomePageActivity
                startActivity(new Intent(getApplicationContext(), HomePageActivity.class));
                finish();
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);
    }


}