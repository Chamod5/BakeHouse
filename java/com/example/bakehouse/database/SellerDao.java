package com.example.bakehouse.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface SellerDao {

    @Insert
    void insertSeller(Seller seller);

    @Update
    void updateSeller(Seller seller);

    @Query("SELECT * FROM sellers")
    List<Seller> getAllSellers();

    @Query("DELETE FROM sellers")
    void deleteAllSellers();

    @Query("SELECT * FROM sellers WHERE email = :email LIMIT 1")
    Seller getSellerByEmail(String email);

    @Query("SELECT * FROM sellers WHERE email = :email AND password = :password LIMIT 1")
    Seller loginSeller(String email, String password);

    @Query("SELECT COUNT(*) FROM sellers WHERE email = :email")
    int getSellerCountByEmail(String email);

    @Query("UPDATE sellers SET name = :name, phone = :phone, businessName = :businessName, address = :address WHERE email = :email")
    void updateSellerProfile(String email, String name, String phone, String businessName, String address);
}