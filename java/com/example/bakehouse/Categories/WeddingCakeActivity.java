package com.example.bakehouse.Categories;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import com.example.bakehouse.HomePageActivity;
import com.example.bakehouse.R;
import com.example.bakehouse.adapters.WeddingCakeAdapter;
import com.example.bakehouse.models.WeddingCakeModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class WeddingCakeActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private WeddingCakeAdapter cakeAdapter;
    private List<WeddingCakeModel> cakeList;

    private DatabaseReference cakesRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_birthday_cake);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        cakeList = new ArrayList<>();
        cakeAdapter = new WeddingCakeAdapter(this, cakeList);
        recyclerView.setAdapter(cakeAdapter);

        cakesRef = FirebaseDatabase.getInstance().getReference("Cakes").child("Wedding cakes");

        cakesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                cakeList.clear();
                for (DataSnapshot cakeSnapshot : snapshot.getChildren()) {
                    WeddingCakeModel cake = cakeSnapshot.getValue(WeddingCakeModel.class);
                    cakeList.add(cake);
                }
                cakeAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle possible errors.
            }
        });

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Navigate to the home page when the back button is pressed
                Intent intent = new Intent(WeddingCakeActivity.this, HomePageActivity.class);
                startActivity(intent);
                finish(); // Optional: Call finish() to close the current activity
            }
        });
    }
}