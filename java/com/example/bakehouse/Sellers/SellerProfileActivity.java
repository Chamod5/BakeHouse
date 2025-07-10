package com.example.bakehouse.Sellers;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.bakehouse.HomePageActivity;
import com.example.bakehouse.MainActivity;
import com.example.bakehouse.ProfileActivity;
import com.example.bakehouse.R;
import com.example.bakehouse.SearchActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class SellerProfileActivity extends AppCompatActivity {

    Button logoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_profile);

        logoutButton = findViewById(R.id.profile_LogOut);

        Intent intent2 = getIntent();

        String sellerId = intent2.getStringExtra("sellerId");
        String name = intent2.getStringExtra("name");
        String email = intent2.getStringExtra("email");
        String phoneNo = intent2.getStringExtra("phone");
        String businessName = intent2.getStringExtra("business_name");
        String address = intent2.getStringExtra("address");
        String imageUrl = intent2.getStringExtra("imageUrl");


        TextView sellerNameView = findViewById(R.id.seller_profile_name);
        TextView sellerEmailView = findViewById(R.id.seller_profile_email);
        TextView sellerPhoneView = findViewById(R.id.seller_profile_phone);
        TextView sellerBusinessNameView = findViewById(R.id.seller_profile_business_name);
        TextView sellerAddressView = findViewById(R.id.seller_profile_Address);
        CircleImageView sellerImageView = findViewById(R.id.Seller_profile_image);

        sellerNameView.setText(name);
        sellerEmailView.setText(email);
        sellerPhoneView.setText(phoneNo);
        sellerBusinessNameView.setText(businessName);
        sellerAddressView.setText(address);

        // Load the profile image using Picasso
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Picasso.get().load(imageUrl).placeholder(R.drawable.profile_icon).into(sellerImageView);
        } else {
            sellerImageView.setImageResource(R.drawable.profile_icon); // Default image
        }

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                // Redirect to the home screen
                Intent intent = new Intent(SellerProfileActivity.this, MainActivity.class);
                //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish(); // Close the current activity
            }
        });



        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setSelectedItemId(R.id.bottom_profile_seller);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.bottom_profile_seller) {
                return true;
            } else if (itemId == R.id.bottom_home_seller) {
                //Intent intent = new Intent(SellerProfileActivity.this, SellerHomeActivity.class);
                Intent intent = new Intent(SellerProfileActivity.this, SellerHomeActivity.class);
                intent.putExtra("sellerId", sellerId);
                intent.putExtra("name", name);
                intent.putExtra("email", email);
                intent.putExtra("phone", phoneNo);
                intent.putExtra("business_name", businessName);
                intent.putExtra("address", address);
                intent.putExtra("imageUrl", imageUrl);
                startActivity(intent);
                finish();
                return true;
            } else if (itemId == R.id.bottom_add_item_seller) {
                //Intent intent = new Intent(SellerProfileActivity.this, SellerUploadCakeActivity.class);
                Intent intent = new Intent(SellerProfileActivity.this, SellerUploadCakeActivity.class);
                intent.putExtra("sellerId", sellerId);
                intent.putExtra("name", name);
                intent.putExtra("email", email);
                intent.putExtra("phone", phoneNo);
                intent.putExtra("business_name", businessName);
                intent.putExtra("address", address);
                intent.putExtra("imageUrl", imageUrl);
                startActivity(intent);
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
                startActivity(new Intent(getApplicationContext(), SellerHomeActivity.class));
                finish();
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);
    }
}