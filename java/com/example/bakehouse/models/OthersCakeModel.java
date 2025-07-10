package com.example.bakehouse.models;

import java.util.List;

public class OthersCakeModel {
    private String title;
    private String description;
    private String price;
    private List<String> imageUrls;
    private String sellerId;

    // Default constructor required for calls to DataSnapshot.getValue(Cake.class)
    public OthersCakeModel() {
    }

    public OthersCakeModel(String title, String description, String price, List<String> imageUrls, String sellerId) {
        this.title = title;
        this.description = description;
        this.price = price;
        this.imageUrls = imageUrls;
        this.sellerId = sellerId;
    }

    // Getters and setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public List<String> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }
}
