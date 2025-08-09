package com.example.bakehouse.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.bakehouse.R;
import com.example.bakehouse.Sellers.SellerItemDetailActivity;
import com.example.bakehouse.models.CakeClass;

import java.util.List;

public class SellerShowAdapter extends RecyclerView.Adapter<SellerViewHolder> {

    private Context context;
    private List<CakeClass> cakeList;

    public SellerShowAdapter(Context context, List<CakeClass> cakeList) {
        this.context = context;
        this.cakeList = cakeList;
    }

    @NonNull
    @Override
    public SellerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.seller_show_item,parent,false);
        return new SellerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SellerViewHolder holder, int position) {
        Glide.with(context).load(cakeList.get(position).getImageUrls()).into(holder.cake_image);
        holder.cake_title.setText(cakeList.get(position).getTitle());
        holder.cake_description.setText(cakeList.get(position).getDescription());
        holder.cake_price.setText(cakeList.get(position).getPrice());

        holder.cake_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = holder.getAdapterPosition();
                Intent intent = new Intent(context, SellerItemDetailActivity.class);
                intent.putExtra("Images", cakeList.get(position).getImageUrls());
                intent.putExtra("Title", cakeList.get(position).getTitle());
                intent.putExtra("Description", cakeList.get(position).getDescription());
                intent.putExtra("Price", cakeList.get(position).getPrice());
                intent.putExtra("SellerId", cakeList.get(position).getSellerId());

                context.startActivity(intent);
            }
        });



    }

    @Override
    public int getItemCount() {

        return cakeList.size();
    }
}


class SellerViewHolder extends RecyclerView.ViewHolder{

    ImageView cake_image;
    TextView cake_title, cake_description, cake_price;
    CardView cake_card;
    public SellerViewHolder(@NonNull View itemView) {
        super(itemView);

        cake_image = itemView.findViewById(R.id.CakeImage);
        cake_title = itemView.findViewById(R.id.CakeName);
        cake_description = itemView.findViewById(R.id.CakeDescription);
        cake_price = itemView.findViewById(R.id.CakePrice);
        cake_card = itemView.findViewById(R.id.CakeCard);
    }
}

