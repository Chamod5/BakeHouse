package com.example.bakehouse.Categories;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.bakehouse.R;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.bakehouse.R;
import com.squareup.picasso.Picasso;

public class BirthdayCakeDetailActivity extends AppCompatActivity {

    ImageView cakeImageView;
    TextView cakeNameTextView, cakeDescriptionTextView, cakePriceTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_birthday_cake_detail);

        cakeImageView = findViewById(R.id.cakeImageView);
        cakeNameTextView = findViewById(R.id.cakeNameTextView);
        cakeDescriptionTextView = findViewById(R.id.cakeDescriptionTextView);
        cakePriceTextView = findViewById(R.id.cakePriceTextView);

        // Get data from intent
        String cakeName = getIntent().getStringExtra("cakeName");
        String cakeDescription = getIntent().getStringExtra("cakeDescription");
        String cakePrice = getIntent().getStringExtra("cakePrice");
        String cakeImageUrl = getIntent().getStringExtra("cakeImageUrl");

        // Set data to views
        cakeNameTextView.setText(cakeName);
        cakeDescriptionTextView.setText(cakeDescription);
        cakePriceTextView.setText(cakePrice);
        Picasso.get().load(cakeImageUrl).into(cakeImageView);
    }
}
