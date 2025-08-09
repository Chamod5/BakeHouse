package com.example.bakehouse.Sellers;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.bakehouse.R;
import com.example.bakehouse.database.AppDatabase;
import com.example.bakehouse.database.Seller;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class SellerProfileFragment extends Fragment {

    private static final String ARG_EMAIL = "seller_email";
    private String sellerEmail;
    private AppDatabase localDb;

    // UI components
    private TextView sellerNameView;
    private TextView sellerEmailView;
    private TextView sellerPhoneView;
    private TextView sellerBusinessNameView;
    private TextView sellerAddressView;
    private CircleImageView sellerImageView;

    // ✅ Required empty public constructor
    public SellerProfileFragment() {
    }

    // ✅ Use this method to create a new instance of the fragment
    public static SellerProfileFragment newInstance(String email) {
        SellerProfileFragment fragment = new SellerProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_EMAIL, email);
        fragment.setArguments(args);
        return fragment;
    }

    // ✅ Get arguments from Bundle
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize database
        localDb = AppDatabase.getInstance(requireContext());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_seller_profile, container, false);



        // Initialize UI components
        sellerNameView = view.findViewById(R.id.seller_profile_name);
        sellerEmailView = view.findViewById(R.id.seller_profile_email);
        sellerPhoneView = view.findViewById(R.id.seller_profile_phone);
        sellerBusinessNameView = view.findViewById(R.id.seller_profile_business_name);
        sellerAddressView = view.findViewById(R.id.seller_profile_Address);
        sellerImageView = view.findViewById(R.id.Seller_profile_image);

        // Load seller data from database
        loadSellerProfile();

        view.findViewById(R.id.profile_LogOut).setOnClickListener(v -> {
            // Add actual logout logic
            requireActivity().finish();
        });

        return view;
    }

    private void loadSellerProfile() {
        String sellerEmail = getArguments().getString("sellerEmail");

        if (sellerEmail == null || sellerEmail.isEmpty()) {
            Toast.makeText(getContext(), "No seller email provided", Toast.LENGTH_SHORT).show();
            setDefaultValues();
            return;
        }

        // Load seller data in background thread
        new Thread(() -> {
            try {
                Seller seller = localDb.sellerDao().getSellerByEmail(sellerEmail);

                // Update UI on main thread
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        if (seller != null) {
                            populateSellerData(seller);
                        } else {
                            Toast.makeText(getContext(), "Seller not found in local database", Toast.LENGTH_SHORT).show();
                            setDefaultValues();
                        }
                    });
                }

            } catch (Exception e) {
                Log.e("SellerProfile", "Error loading seller data", e);
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), "Error loading profile: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        setDefaultValues();
                    });
                }
            }
        }).start();
    }

    private void populateSellerData(Seller seller) {
        // Set text data
        sellerNameView.setText(seller.name != null ? seller.name : "N/A");
        sellerEmailView.setText(seller.email != null ? seller.email : "N/A");
        sellerPhoneView.setText(seller.phone != null ? seller.phone : "N/A");
        sellerBusinessNameView.setText(seller.businessName != null ? seller.businessName : "N/A");
        sellerAddressView.setText(seller.address != null ? seller.address : "N/A");

        // Set profile image
        if (seller.profileImage != null && seller.profileImage.length > 0) {
            try {
                Bitmap bitmap = BitmapFactory.decodeByteArray(seller.profileImage, 0, seller.profileImage.length);
                sellerImageView.setImageBitmap(bitmap);
            } catch (Exception e) {
                Log.e("SellerProfile", "Error decoding profile image", e);
                sellerImageView.setImageResource(R.drawable.profile_icon);
            }
        } else {
            sellerImageView.setImageResource(R.drawable.profile_icon);
        }
    }

    private void setDefaultValues() {
        // Set default values if seller not found
        sellerNameView.setText("Seller Name");
        sellerEmailView.setText(sellerEmail != null ? sellerEmail : "No Email");
        sellerPhoneView.setText("No Phone");
        sellerBusinessNameView.setText("No Business Name");
        sellerAddressView.setText("No Address");
        sellerImageView.setImageResource(R.drawable.profile_icon);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Clean up resources if needed
    }
}