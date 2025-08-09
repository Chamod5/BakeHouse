package com.example.bakehouse.Sellers;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.bakehouse.HomePageFragment;
import com.example.bakehouse.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class SellerMainActivity extends AppCompatActivity {

    private String sellerEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_main);

        sellerEmail = getIntent().getStringExtra("sellerEmail");
        if (sellerEmail == null || sellerEmail.isEmpty()) {
            finish();
            return;
        }

        BottomNavigationView bottomNavigation = findViewById(R.id.bottomNavigation);
        bottomNavigation.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();

            if (itemId == R.id.bottom_home_seller) {
                selectedFragment = new SellerHomeFragment();
            } else if (itemId == R.id.bottom_add_item_seller) {
                selectedFragment = new SellerUploadCakeFragment();
            } else if (itemId == R.id.bottom_profile_seller) {
                selectedFragment = new SellerProfileFragment();
            }

            if (selectedFragment != null) {
                loadFragment(selectedFragment, sellerEmail);
            }
            return true;
        });

        // Load default fragment
        bottomNavigation.setSelectedItemId(R.id.bottom_home_seller);
    }

    private void loadFragment(Fragment fragment, String sellerEmail) {
        Bundle bundle = new Bundle();
        bundle.putString("sellerEmail", sellerEmail);
        fragment.setArguments(bundle);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container_seller, fragment)
                .commit();
    }
}
