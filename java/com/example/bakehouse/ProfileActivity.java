package com.example.bakehouse;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ProfileActivity extends AppCompatActivity {

    TextView nameTextView, usernameTextView, emailTextView;
    ImageView profileImageView;
    Button logoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        nameTextView = findViewById(R.id.profile_name);
        usernameTextView = findViewById(R.id.profile_username);
        emailTextView = findViewById(R.id.profile_email);
        profileImageView = findViewById(R.id.profile_image);

        logoutButton = findViewById(R.id.profile_LogOut);

        // Get the data passed from LogInActivity

        Intent intent = getIntent();

        String name = intent.getStringExtra("name");
        String username = intent.getStringExtra("username");
        String email = intent.getStringExtra("email");
        String imageURL = intent.getStringExtra("imageUrl");

        nameTextView.setText(name);
        usernameTextView.setText(username);
        emailTextView.setText(email);

        // Use Picasso library to load the image from URL
        if (imageURL != null && !imageURL.isEmpty()) {
            Picasso.get().load(imageURL).into(profileImageView);
        } else {
            profileImageView.setImageResource(R.drawable.profile_icon); // Default image if no imageURL is found
        }

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                // Redirect to the home screen
                Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish(); // Close the current activity
            }
        });



        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setSelectedItemId(R.id.bottom_profile);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.bottom_profile) {
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