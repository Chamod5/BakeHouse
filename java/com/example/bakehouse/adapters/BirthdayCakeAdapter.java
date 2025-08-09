package com.example.bakehouse.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.bakehouse.R;
import com.example.bakehouse.UserItemDetail;
import com.example.bakehouse.models.BirthdayCakeModel;

import java.util.ArrayList;

public class BirthdayCakeAdapter extends RecyclerView.Adapter<BirthdayCakeAdapter.ViewHolder> {

    private static final String TAG = "BirthdayCakeAdapter";
    private Context context;
    private ArrayList<BirthdayCakeModel> birthdayCakeList;

    private int userId;

    /*
    public BirthdayCakeAdapter(Context context, ArrayList<BirthdayCakeModel> cakeList) {
        this.context = context;
        this.birthdayCakeList = cakeList;
    }
    /*
     */
    public BirthdayCakeAdapter(Context context, ArrayList<BirthdayCakeModel> cakeList, int userId) {
        this.context = context;
        this.birthdayCakeList = cakeList;
        this.userId = userId;
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, price;
        ImageView image;
        Button viewButton;

        public ViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.Cake_name_view);
            price = view.findViewById(R.id.Cake_price_view);
            image = view.findViewById(R.id.Cake_image_view);
            viewButton = view.findViewById(R.id.Cake_Order_Button_view);
        }
    }

    @Override
    public BirthdayCakeAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        View view = LayoutInflater.from(context).inflate(R.layout.user_show_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        BirthdayCakeModel cake = birthdayCakeList.get(position);

        // Set title and price
        holder.title.setText(cake.getTitle());
        holder.price.setText("Rs " + String.format("%.2f", cake.getPrice()));

        // Set image
        setImageFromByteArray(holder.image, cake);

        // Set click listener for view button
        // In onBindViewHolder method, both click listeners should have:
        holder.viewButton.setOnClickListener(v -> {
            Intent intent = new Intent(context, UserItemDetail.class);
            intent.putExtra("cake_id", cake.getId());
            intent.putExtra("cake_title", cake.getTitle());
            intent.putExtra("seller_id", cake.getSeller_id());
            intent.putExtra("user_id", userId); // This line is already there
            context.startActivity(intent);
        });

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, UserItemDetail.class);
            intent.putExtra("cake_id", cake.getId());
            intent.putExtra("cake_title", cake.getTitle());
            intent.putExtra("seller_id", cake.getSeller_id());
            intent.putExtra("user_id", userId); // This line is already there
            context.startActivity(intent);
        });
    }

    private void setImageFromByteArray(ImageView imageView, BirthdayCakeModel cake) {
        try {
            if (cake.getImages() != null && !cake.getImages().isEmpty()) {
                byte[] imageBytes = cake.getImages().get(0); // Get first image
                if (imageBytes != null && imageBytes.length > 0) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                    if (bitmap != null) {
                        imageView.setImageBitmap(bitmap);
                    } else {
                        // Set placeholder image if bitmap creation fails
                        imageView.setImageResource(R.drawable.ic_cake_placeholder); // Make sure you have this drawable
                        Log.w(TAG, "Failed to create bitmap for cake: " + cake.getTitle());
                    }
                } else {
                    imageView.setImageResource(R.drawable.ic_cake_placeholder);
                    Log.w(TAG, "Empty image bytes for cake: " + cake.getTitle());
                }
            } else {
                imageView.setImageResource(R.drawable.ic_cake_placeholder);
                Log.w(TAG, "No images available for cake: " + cake.getTitle());
            }
        } catch (Exception e) {
            imageView.setImageResource(R.drawable.ic_cake_placeholder);
            Log.e(TAG, "Error setting image for cake: " + cake.getTitle(), e);
        }
    }

    @Override
    public int getItemCount() {
        return birthdayCakeList != null ? birthdayCakeList.size() : 0;
    }

    // Method to update the data
    public void updateData(ArrayList<BirthdayCakeModel> newCakeList) {
        this.birthdayCakeList = newCakeList;
        notifyDataSetChanged();
    }
}