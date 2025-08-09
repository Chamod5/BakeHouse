package com.example.bakehouse.database;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {CartItem.class, User.class, Seller.class}, version = 4, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase instance;

    public abstract CartDao cartDao();
    public abstract UserDao userDao();
    public abstract SellerDao sellerDao();

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "bakehouse_db")
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries() // For simplicity, but use background threads in production
                    .build();
        }
        return instance;
    }
}