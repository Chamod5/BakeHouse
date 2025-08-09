package com.example.bakehouse.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bakehouse.R;
import com.example.bakehouse.models.Cake;

import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.CakeViewHolder> {

    private Context context;
    private List<Cake> cakeList;
    private OnCakeClickListener onCakeClickListener;

    public interface OnCakeClickListener {
        void onCakeClick(Cake cake);
    }

    public SearchAdapter(Context context, List<Cake> cakeList) {
        this.context = context;
        this.cakeList = cakeList;
    }

    public void setOnCakeClickListener(OnCakeClickListener listener) {
        this.onCakeClickListener = listener;
    }

    @NonNull
    @Override
    public CakeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.search_item, parent, false);
        return new CakeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CakeViewHolder holder, int position) {
        Cake cake = cakeList.get(position);

        holder.cakeTitle.setText(cake.getTitle());
        holder.cakeCategory.setText(cake.getDescription()); // Using description as category display
        holder.cakePrice.setText("Rs. " + cake.getPrice());

        // Load image from byte array
        if (cake.getImage() != null && cake.getImage().length > 0) {
            try {
                Bitmap bitmap = BitmapFactory.decodeByteArray(cake.getImage(), 0, cake.getImage().length);
                holder.cakeImage.setImageBitmap(bitmap);
            } catch (Exception e) {
                holder.cakeImage.setImageResource(R.drawable.ic_cake_placeholder);
            }
        } else {
            holder.cakeImage.setImageResource(R.drawable.ic_cake_placeholder);
        }

        // Set click listener
        holder.itemView.setOnClickListener(v -> {
            if (onCakeClickListener != null) {
                onCakeClickListener.onCakeClick(cake);
            }
        });
    }

    @Override
    public int getItemCount() {
        return cakeList.size();
    }

    public void updateCakeList(List<Cake> newCakeList) {
        this.cakeList = newCakeList;
        notifyDataSetChanged();
    }

    public class CakeViewHolder extends RecyclerView.ViewHolder {
        ImageView cakeImage;
        TextView cakeTitle, cakeCategory, cakePrice;

        public CakeViewHolder(@NonNull View itemView) {
            super(itemView);
            cakeImage = itemView.findViewById(R.id.cartItemImage);
            cakeTitle = itemView.findViewById(R.id.cartItemTitle);
            cakeCategory = itemView.findViewById(R.id.cartItemCategory);
            cakePrice = itemView.findViewById(R.id.cartItemPrice);
        }
    }
}