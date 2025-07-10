package com.example.bakehouse;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toolbar;

import com.example.bakehouse.Categories.BirthdayCakeActivity;
import com.example.bakehouse.Categories.OtherCakeActivity;
import com.example.bakehouse.Categories.WeddingCakeActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomePageActivity extends AppCompatActivity {

    LinearLayout birthdayCakesCategory;
    LinearLayout weddingCakesCategory;
    LinearLayout othersCategory;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        // Initialize LinearLayouts
        birthdayCakesCategory = findViewById(R.id.Birthday_cake_category);
        weddingCakesCategory = findViewById(R.id.Wedding_cake_category);
        othersCategory = findViewById(R.id.Others_category);


        Intent intent = getIntent();

        String name = intent.getStringExtra("name");
        String username = intent.getStringExtra("username");
        String email = intent.getStringExtra("email");
        String imageURL = intent.getStringExtra("imageUrl");



        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setSelectedItemId(R.id.bottom_home);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.bottom_home) {
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
            } else if (itemId == R.id.bottom_cart) {
                //startActivity(new Intent(getApplicationContext(), CartActivity.class));
                Intent intent2 = new Intent(getApplicationContext(), CartActivity.class);
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

        // Set click listeners
        birthdayCakesCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateTo(BirthdayCakeActivity.class);
            }
        });

        weddingCakesCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateTo(WeddingCakeActivity.class);
            }
        });

        othersCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateTo(OtherCakeActivity.class);
            }
        });


    }

    private void navigateTo(Class<?> activityClass) {
        Intent intent = new Intent(HomePageActivity.this, activityClass);
        startActivity(intent);
    }
}