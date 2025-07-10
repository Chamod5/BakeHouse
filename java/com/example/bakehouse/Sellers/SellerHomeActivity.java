package com.example.bakehouse.Sellers;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bakehouse.Categories.BirthdayCakeActivity;
import com.example.bakehouse.R;
import com.example.bakehouse.SearchActivity;
import com.example.bakehouse.Sellers.SellerProfileActivity;
import com.example.bakehouse.Sellers.SellerUploadCakeActivity;
import com.example.bakehouse.adapters.SellerShowAdapter;
import com.example.bakehouse.models.CakeClass;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SellerHomeActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private DatabaseReference productRef;
    private List<CakeClass> cakeList;
    private RecyclerView.LayoutManager layoutManager;
    private ValueEventListener eventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_home);

        // Retrieve sellerId from Intent
        Intent intent2 = getIntent();

        String sellerId = intent2.getStringExtra("sellerId");
        String name = intent2.getStringExtra("name");
        String email = intent2.getStringExtra("email");
        String phoneNo = intent2.getStringExtra("phone");
        String businessName = intent2.getStringExtra("business_name");
        String address = intent2.getStringExtra("address");
        String imageUrl = intent2.getStringExtra("imageUrl");





        recyclerView = findViewById(R.id.recycler_menu);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(SellerHomeActivity.this, 1);
        recyclerView.setLayoutManager(gridLayoutManager);

        cakeList = new ArrayList<>();
        SellerShowAdapter adapter = new SellerShowAdapter(SellerHomeActivity.this, cakeList);
        recyclerView.setAdapter(adapter);

        // Reference to the cakes in Firebase
        productRef = FirebaseDatabase.getInstance().getReference().child("Cakes");

        // Query Firebase for cakes that match the sellerId
        // List of cake categories
        String[] cakeCategories = {"Birthday cakes", "Wedding cakes", "Others"};

        for (String category : cakeCategories) {
            DatabaseReference categoryRef = productRef.child(category);

            categoryRef.orderByChild("sellerId").equalTo(sellerId)
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            cakeList.clear();
                            for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                                CakeClass cakeClass = itemSnapshot.getValue(CakeClass.class);
                                cakeList.add(cakeClass);
                            }
                            adapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            // Handle possible errors.
                        }
                    });
        }



        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setSelectedItemId(R.id.bottom_home_seller);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.bottom_home_seller) {
                return true;
            } else if (itemId == R.id.bottom_add_item_seller) {
                if (sellerId != null) {
                    //Intent intent = new Intent(SellerHomeActivity.this, SellerUploadCakeActivity.class);
                    Intent intent = new Intent(getApplicationContext(), SellerUploadCakeActivity.class);
                    intent.putExtra("sellerId", sellerId);
                    intent.putExtra("name", name);
                    intent.putExtra("email", email);
                    intent.putExtra("phone", phoneNo);
                    intent.putExtra("business_name", businessName);
                    intent.putExtra("address", address);
                    intent.putExtra("imageUrl", imageUrl);
                    startActivity(intent);
                    finish();
                }
                return true;
            } else if (itemId == R.id.bottom_profile_seller) {
                //Intent intent = new Intent(SellerHomeActivity.this, SellerProfileActivity.class);
                //startActivity(new Intent(getApplicationContext(), SellerProfileActivity.class));
                Intent intent = new Intent(getApplicationContext(), SellerProfileActivity.class);
                intent.putExtra("sellerId", sellerId);
                intent.putExtra("name", name);
                intent.putExtra("email", email);
                intent.putExtra("phone", phoneNo);
                intent.putExtra("business_name", businessName);
                intent.putExtra("address", address);
                intent.putExtra("imageUrl", imageUrl);
                startActivity(intent);
                startActivity(intent);
                finish();
                return true;
            }
            return false;
        });

    }
}
