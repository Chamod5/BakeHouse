package com.example.bakehouse.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bakehouse.R;
import com.example.bakehouse.models.Cake;
import com.example.bakehouse.Sellers.SellerItemDetailActivity;

import java.util.List;

public class CakeAdapter extends RecyclerView.Adapter<CakeAdapter.CakeViewHolder> {

    private static final String TAG = "CakeAdapter";
    private Context context;
    private List<Cake> cakeList;

    public CakeAdapter(Context context, List<Cake> cakeList) {
        this.context = context;
        this.cakeList = cakeList;
    }

    @NonNull
    @Override
    public CakeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.seller_show_item, parent, false);
        return new CakeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CakeViewHolder holder, int position) {
        Cake cake = cakeList.get(position);

        holder.cakeName.setText(cake.getTitle());
        holder.cakePrice.setText("Rs. " + cake.getPrice());
        holder.cakeDescription.setText(cake.getDescription());

        // Set the image - Use first image from the list if available
        if (cake.getImage() != null && cake.getImage().length > 0) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(cake.getImage(), 0, cake.getImage().length);
            holder.cakeImage.setImageBitmap(bitmap);
        } else {
            // Set a default image if no image is available
            holder.cakeImage.setImageResource(R.drawable.ic_cake_placeholder); // Add a placeholder image
        }

        // Add click listener to open cake detail
        holder.itemView.setOnClickListener(v -> {
            int cakeId = cake.getId();
            Log.d(TAG, "Cake clicked - ID: " + cakeId + ", Title: " + cake.getTitle());

            Intent intent = new Intent(context, SellerItemDetailActivity.class);
            intent.putExtra("cake_id", cakeId);
            context.startActivity(intent);

        });
    }

    @Override
    public int getItemCount() {
        return cakeList != null ? cakeList.size() : 0;
    }

    public void updateCakeList(List<Cake> newCakeList) {
        this.cakeList = newCakeList;
        notifyDataSetChanged();
    }

    public static class CakeViewHolder extends RecyclerView.ViewHolder {
        TextView cakeName, cakePrice, cakeDescription;
        ImageView cakeImage;

        public CakeViewHolder(@NonNull View itemView) {
            super(itemView);
            cakeName = itemView.findViewById(R.id.CakeName);
            cakePrice = itemView.findViewById(R.id.CakePrice);
            cakeDescription = itemView.findViewById(R.id.CakeDescription);
            cakeImage = itemView.findViewById(R.id.CakeImage);
        }
    }
}