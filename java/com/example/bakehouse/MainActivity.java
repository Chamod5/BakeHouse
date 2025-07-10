package com.example.bakehouse;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.bakehouse.Sellers.SellerLogInActivity;
import com.example.bakehouse.Sellers.SellerRegistrationActivity;


public class MainActivity extends AppCompatActivity {

    Button joinNowButton, logInButton;
    TextView seller_registration_ask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        joinNowButton = findViewById(R.id.main_register_button);
        logInButton = findViewById(R.id.main_login_button);
        seller_registration_ask = findViewById(R.id.seller_reg_ask);

        logInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,LogInActivity.class);
                startActivity(intent);
            }
        });

        joinNowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,RegistrationActivity.class);
                startActivity(intent);
            }
        });

        seller_registration_ask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SellerLogInActivity.class);
                startActivity(intent);
            }
        });

    }
}

