package com.example.bakehouse.Sellers;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.bakehouse.R;

public class SellerItemDetailActivity extends AppCompatActivity {

    TextView detailTitle, detailDescription, detailPrice;
    ImageView detailImage;
    String sellerId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_item_detail);

        detailTitle = findViewById(R.id.detailTitle);
        detailDescription = findViewById(R.id.detailDescription);
        detailImage = findViewById(R.id.detailImage);
        detailPrice = findViewById(R.id.detailPrice);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            detailDescription.setText(bundle.getString("Description"));
            detailTitle.setText(bundle.getString("Title"));
            detailPrice.setText(bundle.getString("Price"));
            sellerId = bundle.getString("SellerId");
            //imageUrls = bundle.getString("Image");
            Glide.with(this).load(bundle.getString("Image")).into(detailImage);
        }
    }
}