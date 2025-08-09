package com.example.bakehouse.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.bakehouse.R;
import com.example.bakehouse.database.CartItem;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

    private final List<CartItem> itemList;
    private final Context context;

    public CartAdapter(Context context, List<CartItem> items) {
        this.context = context;
        this.itemList = items;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView title, price, category;
        public ImageView image;

        public ViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.cartItemTitle);
            price = view.findViewById(R.id.cartItemPrice);
            category = view.findViewById(R.id.cartItemCategory);
            image = view.findViewById(R.id.cartItemImage);
        }
    }

    @Override
    public CartAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.cart_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CartAdapter.ViewHolder holder, int position) {
        CartItem item = itemList.get(position);
        holder.title.setText(item.title);
        holder.price.setText(item.price);
        holder.category.setText(item.category);
        if (item.image != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(item.image, 0, item.image.length);
            holder.image.setImageBitmap(bitmap);
        }
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }
}
