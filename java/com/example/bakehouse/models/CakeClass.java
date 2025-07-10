package com.example.bakehouse.models;

import java.util.ArrayList;

public class CakeClass {
    private String title;
    private String price;
    private String description;
    private String sellerId;
    private ArrayList<String> imageUrls;

    public CakeClass() {
        // Default constructor required for calls to DataSnapshot.getValue(CakeClass.class)
    }

    public CakeClass(String title, String price, String description, String sellerId, ArrayList<String> imageUrls) {
        this.title = title;
        this.price = price;
        this.description = description;
        this.sellerId = sellerId;
        this.imageUrls = imageUrls;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    public ArrayList<String> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(ArrayList<String> imageUrls) {
        this.imageUrls = imageUrls;
    }
}
