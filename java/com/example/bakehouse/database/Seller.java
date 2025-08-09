package com.example.bakehouse.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "sellers") // This maps it to the "sellers" table
public class Seller {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String name;
    public String phone;
    public String email;
    public String businessName;
    public String address;
    public String password;
    public byte[] profileImage;

    public Seller(String name, String phone, String email, String businessName,
                  String address, String password, byte[] profileImage) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.businessName = businessName;
        this.address = address;
        this.password = password;
        this.profileImage = profileImage;
    }
}
