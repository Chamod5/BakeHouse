package com.example.bakehouse;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.bakehouse.database.AppDatabase;
import com.example.bakehouse.database.User;

import java.util.concurrent.Executors;

public class ProfileFragment extends Fragment {

    private TextView nameTextView, nameTextView2, usernameTextView, emailTextView;
    private ImageView profileImageView;
    private Button logoutButton, editProfileButton;

    private String email;
    private User currentUser;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            email = getArguments().getString("email");

        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        initializeViews(view);
        setupClickListeners();

        if (email != null) {
            fetchUserDetailsFromLocal(email);
        } else {
            Toast.makeText(getContext(), "No email provided", Toast.LENGTH_SHORT).show();
        }

        return view;
    }

    private void initializeViews(View view) {
        nameTextView = view.findViewById(R.id.profile_name);
        nameTextView2 = view.findViewById(R.id.profile_name2);
        usernameTextView = view.findViewById(R.id.profile_username);
        emailTextView = view.findViewById(R.id.profile_email);
        profileImageView = view.findViewById(R.id.profile_image);
        logoutButton = view.findViewById(R.id.profile_LogOut);

        // Add edit profile button if it exists in your layout
        // editProfileButton = view.findViewById(R.id.profile_edit_btn);
    }

    private void setupClickListeners() {
        logoutButton.setOnClickListener(v -> logout());

        // If you have an edit profile button
        // editProfileButton.setOnClickListener(v -> editProfile());
    }

    private void fetchUserDetailsFromLocal(String email) {
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                AppDatabase db = AppDatabase.getInstance(requireContext());
                User user = db.userDao().getUserByEmail(email);

                if (user != null) {
                    currentUser = user;
                    requireActivity().runOnUiThread(() -> displayUserData(user));
                } else {
                    requireActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), "User not found in local database", Toast.LENGTH_SHORT).show();
                        Log.w("ProfileFragment", "User not found for email: " + email);
                    });
                }
            } catch (Exception e) {
                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(getContext(), "Error loading user data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("ProfileFragment", "Error fetching user data", e);
                });
            }
        });
    }

    private void displayUserData(User user) {
        nameTextView.setText(user.name != null ? user.name : "No name");
        nameTextView2.setText("Name: "+(user.name != null ? user.name : "No name"));
        usernameTextView.setText("User name: "+(user.username != null ? user.username : "No username"));
        emailTextView.setText("Email: "+(user.email != null ? user.email : "No email"));

        if (user.profileImage != null && user.profileImage.length > 0) {
            try {
                Bitmap bitmap = BitmapFactory.decodeByteArray(user.profileImage, 0, user.profileImage.length);
                if (bitmap != null) {
                    profileImageView.setImageBitmap(bitmap);
                } else {
                    profileImageView.setImageResource(R.drawable.profile_icon); // Set default image
                }
            } catch (Exception e) {
                Log.e("ProfileFragment", "Error decoding profile image", e);
                profileImageView.setImageResource(R.drawable.profile_icon);
            }
        } else {
            profileImageView.setImageResource(R.drawable.profile_icon);
        }
    }

    private void logout() {
        // Clear SharedPreferences
        requireActivity().getSharedPreferences("UserPrefs", requireContext().MODE_PRIVATE)
                .edit()
                .clear()
                .apply();

        // Navigate to MainActivity
        Intent intent = new Intent(getActivity(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        requireActivity().finish();
    }

    // Method to refresh user data (can be called from parent activity)
    public void refreshUserData() {
        if (email != null) {
            fetchUserDetailsFromLocal(email);
        }
    }

    // Method to find user by email (utility method)
    public static void findUserByEmail(String email, UserCallback callback) {
        Executors.newSingleThreadExecutor().execute(() -> {
            AppDatabase db = AppDatabase.getInstance(null); // You'll need to pass context
            User user = db.userDao().getUserByEmail(email);
            callback.onUserFound(user);
        });
    }

    // Callback interface for user search
    public interface UserCallback {
        void onUserFound(User user);
    }
}