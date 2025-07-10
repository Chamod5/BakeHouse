package com.example.bakehouse.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bakehouse.PaymentActivity;
import com.example.bakehouse.R;
import com.example.bakehouse.models.WeddingCakeModel;
import com.squareup.picasso.Picasso;

import java.util.List;

public class WeddingCakeAdapter extends RecyclerView.Adapter<WeddingCakeAdapter.CakeViewHolder> {

    private Context context;
    private List<WeddingCakeModel> cakeList;

    public WeddingCakeAdapter(Context context, List<WeddingCakeModel> cakeList) {
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
        WeddingCakeModel cake = cakeList.get(position);
        holder.titleTextView.setText(cake.getTitle());
        holder.priceTextView.setText("Rs" + cake.getPrice());

        // Load the first image in the imageUrls list
        if (cake.getImageUrls() != null && !cake.getImageUrls().isEmpty()) {
            Picasso.get().load(cake.getImageUrls().get(0)).into(holder.cakeImageView);
        }
        /////////////////////
        holder.orderButton.setOnClickListener(v -> {
            Intent intent = new Intent(context, PaymentActivity.class);
            context.startActivity(intent);
        });
    }

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
}

