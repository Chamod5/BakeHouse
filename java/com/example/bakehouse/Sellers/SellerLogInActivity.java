package com.example.bakehouse.Sellers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bakehouse.HomePageActivity;
import com.example.bakehouse.LogInActivity;
import com.example.bakehouse.ProfileActivity;
import com.example.bakehouse.R;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SellerLogInActivity extends AppCompatActivity {


    Button loginSellerButton;
    EditText email, password;
    TextView sellerSignUpText;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_log_in);

        progressBar = findViewById(R.id.progressbar);
        progressBar.setVisibility(View.GONE);

        email = findViewById(R.id.seller_login_Email);
        password = findViewById(R.id.seller_login_Password);
        loginSellerButton = findViewById(R.id.seller_login_Button);
        sellerSignUpText = findViewById(R.id.signuplink);

        sellerSignUpText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SellerLogInActivity.this, SellerRegistrationActivity.class));
            }
        });

        loginSellerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                loginSeller();
            }
        });
    }

    private void loginSeller() {

        String sel_email = email.getText().toString();
        String sel_password = password.getText().toString();

        if (TextUtils.isEmpty(sel_email)) {
            Toast.makeText(this, "Email is empty", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
            return;
        }

        if (TextUtils.isEmpty(sel_password)) {
            Toast.makeText(this, "Password is empty", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
            return;
        }


        String sellerId = getIntent().getStringExtra("sellerId");

        // Reference to the "Users" node in Firebase Realtime Database
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Sellers");

        // Query to find the user by email
        reference.orderByChild("email").equalTo(sel_email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String retrievedPassword = snapshot.child("password").getValue(String.class);
                        String sellerId = snapshot.getKey(); // Assuming the key is the seller ID


                        if (retrievedPassword.equals(sel_password)) {

                            String name = snapshot.child("name").getValue(String.class);
                            String email = snapshot.child("email").getValue(String.class);
                            String imageUrl = snapshot.child("profilepic").getValue(String.class);
                            String phoneNo = snapshot.child("phone").getValue(String.class);
                            String businessName = snapshot.child("business_name").getValue(String.class);
                            String address = snapshot.child("address").getValue(String.class);



                            // Store seller ID in SharedPreferences
                            SharedPreferences sharedPreferences = getSharedPreferences("SellerPrefs", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("sellerId", sellerId);
                            editor.apply();

                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(SellerLogInActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                            //Intent intent = new Intent(SellerLogInActivity.this, SellerHomeActivity.class);

                            Intent intent2 = new Intent(SellerLogInActivity.this, SellerHomeActivity.class);
                            intent2.putExtra("name", name);
                            intent2.putExtra("email", email);
                            intent2.putExtra("phone", phoneNo);
                            intent2.putExtra("business_name", businessName);
                            intent2.putExtra("address", address);
                            intent2.putExtra("imageUrl", imageUrl);

                            intent2.putExtra("sellerId", sellerId);
                            startActivity(intent2);
                            finish(); // Close the login activity
                        } else {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(SellerLogInActivity.this, "Invalid Password", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(SellerLogInActivity.this, "Seller does not exist", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(SellerLogInActivity.this, "Database Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }


}