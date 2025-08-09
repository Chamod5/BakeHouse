package com.example.bakehouse;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class UserMainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    int userId;
    String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_main);

        bottomNavigationView = findViewById(R.id.bottomNavigationUser);

        // ✅ Get user data from intent FIRST
        userId = getIntent().getIntExtra("user_id", -1);
        email = getIntent().getStringExtra("email");

        // ✅ Load default fragment (HomePageFragment) with user data
        loadFragment(new HomePageFragment(), userId, email);

        // ✅ Handle bottom navigation
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int id = item.getItemId();

            if (id == R.id.bottom_home) {
                selectedFragment = new HomePageFragment();
            } else if (id == R.id.bottom_search) {
                selectedFragment = new SearchFragment();
            } else if (id == R.id.bottom_cart) {
                selectedFragment = new CartFragment();
            } else if (id == R.id.bottom_profile) {
                selectedFragment = new ProfileFragment();
            }

            if (selectedFragment != null) {
                loadFragment(selectedFragment, userId, email);
                return true;
            }
            return false;
        });
    }

    // ✅ Method to pass userId and email to fragments
    private void loadFragment(Fragment fragment, int userId, String email) {
        Bundle args = new Bundle();
        args.putString("user_id", String.valueOf(userId)); // Convert to String for HomePageFragment
        args.putString("email", email);
        fragment.setArguments(args);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container_user, fragment)
                .commit();
    }
}