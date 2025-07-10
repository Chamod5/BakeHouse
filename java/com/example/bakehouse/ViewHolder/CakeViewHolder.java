package com.example.bakehouse.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bakehouse.Interface.ItemClickListner;
import com.example.bakehouse.R;

public class CakeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView txtCakeName, txtCakePrice, txtCakeDescription;
    public ImageView imageCake;
    public ItemClickListner listner;


    public CakeViewHolder(@NonNull View itemView) {
        super(itemView);

        imageCake = itemView.findViewById(R.id.CakeImage);
        txtCakeName = itemView.findViewById(R.id.CakeName);
        txtCakePrice = itemView.findViewById(R.id.CakePrice);
        txtCakeDescription = itemView.findViewById(R.id.CakeDescription);

    }


    public void setItemClickListner(ItemClickListner listner){

        this.listner = listner;
    }

    @Override
    public void onClick(View view) {
        listner.onClick(view, getAdapterPosition(),false);

    }
}
