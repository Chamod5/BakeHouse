package com.example.bakehouse.models;

import java.util.ArrayList;

public class OtherCakeModel {
    private int id;
    private int seller_id;
    private String title;
    private double price;
    private String description;
    private ArrayList<byte[]> images;

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getSeller_id() { return seller_id; }
    public void setSeller_id(int seller_id) { this.seller_id = seller_id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public ArrayList<byte[]> getImages() { return images; }
    public void setImages(ArrayList<byte[]> images) { this.images = images; }
}
