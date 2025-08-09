package com.example.bakehouse.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "cart_items")
public class CartItem {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public int cakeId;
    public String title;
    public String category;
    public String price;
    public String description;
    public byte[] image; // First image only
}
