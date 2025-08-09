package com.example.bakehouse.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface CartDao {

    @Insert
    void insertCartItem(CartItem item);

    @Query("SELECT * FROM cart_items")
    List<CartItem> getAllCartItems();

    @Query("DELETE FROM cart_items")
    void clearCart(); // Optional
}
