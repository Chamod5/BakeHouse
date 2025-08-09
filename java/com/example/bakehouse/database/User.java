package com.example.bakehouse.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "users")
public class User {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String name;
    public String username;
    public String email;
    public String password;
    public byte[] profileImage;

    // Server-side user ID (if needed for sync)
    public int serverId;

    // Constructors
    public User() {}

    public User(String name, String username, String email, String password, byte[] profileImage) {
        this.name = name;
        this.username = username;
        this.email = email;
        this.password = password;
        this.profileImage = profileImage;
    }
}