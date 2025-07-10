package com.example.bakehouse.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bakehouse.Categories.BirthdayCakeDetailActivity;
import com.example.bakehouse.PaymentActivity;
import com.example.bakehouse.R;
import com.example.bakehouse.models.BirthdayCakeModel;
import com.squareup.picasso.Picasso;

import java.util.List;

public class BirthdayCakeAdapter extends RecyclerView.Adapter<BirthdayCakeAdapter.CakeViewHolder> {

    Context context;
    List<BirthdayCakeModel> cakeList;

    public BirthdayCakeAdapter(Context context, List<BirthdayCakeModel> cakeList) {
        this.context = context;
        this.cakeList = cakeList;
    }

    @NonNull
    @Override
    public CakeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_show_item, parent, false);
        return new CakeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CakeViewHolder holder, int position) {
        BirthdayCakeModel cake = cakeList.get(position);
        holder.titleTextView.setText(cake.getTitle());
        holder.priceTextView.setText("Rs. " + cake.getPrice()+".00");

        // Load the first image in the imageUrls list
        if (cake.getImageUrls() != null && !cake.getImageUrls().isEmpty()) {
            Picasso.get().load(cake.getImageUrls().get(0)).into(holder.cakeImageView);

        }
        holder.orderButton.setOnClickListener(v -> {
            Intent intent = new Intent(context, PaymentActivity.class);
            context.startActivity(intent);
        });

    }



    /*

    @Override
    public void onBindViewHolder(@NonNull CakeViewHolder holder, int position) {
        BirthdayCakeModel cake = cakeList.get(position);
        holder.cakeNameTextView.setText(cake.getTitle());
        holder.cakePriceTextView.setText(cake.getPrice());
        Picasso.get().load((Uri) cake.getImageUrls()).into(holder.cakeImageView);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, BirthdayCakeDetailActivity.class);
            intent.putExtra("cakeName", cake.getTitle());
            intent.putExtra("cakeDescription", cake.getDescription());
            intent.putExtra("cakePrice", cake.getPrice());
            intent.putExtra("cakeImageUrls", cake.getImageUrls().toArray(new String[0]));
            context.startActivity(intent);
        });
    }

     */



    @Override
    public int getItemCount() {
        return cakeList.size();
    }

    public static class CakeViewHolder extends RecyclerView.ViewHolder {

        ImageView cakeImageView;
        TextView titleTextView, priceTextView;
        Button orderButton;


        public CakeViewHolder(@NonNull View itemView) {
            super(itemView);
            cakeImageView = itemView.findViewById(R.id.Cake_image_view);
            titleTextView = itemView.findViewById(R.id.Cake_name_view);
            priceTextView = itemView.findViewById(R.id.Cake_price_view);
            orderButton = itemView.findViewById(R.id.Cake_Order_Button_view);

        }
    }


    /*
    public static class CakeViewHolder extends RecyclerView.ViewHolder {
        TextView cakeNameTextView, cakePriceTextView;
        ImageView cakeImageView;

        public CakeViewHolder(@NonNull View itemView) {
            super(itemView);
            cakeNameTextView = itemView.findViewById(R.id.cakeNameTextView);
            cakePriceTextView = itemView.findViewById(R.id.cakePriceTextView);
            cakeImageView = itemView.findViewById(R.id.cakeImageView);
        }
    }

     */


}

