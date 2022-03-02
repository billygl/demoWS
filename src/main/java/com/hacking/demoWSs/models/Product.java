package com.hacking.demows.models;

public class Product {
    protected int id;
    protected String title;
    protected String description;
    protected float price;
    protected String imageURL;
 
    public Product() {
    }
 
    public Product(int id) {
        this.id = id;
    }
 
    public Product(int id, String title, String description, float price, String imageURL) {
        this(title, description, price, imageURL);
        this.id = id;
    }
    public Product(int id, String title, String description, float price) {
        this(id, title, description, price, "");
    }

    public Product(String title, String description, float price) {
        this(title, description, price, "");
    }
     
    public Product(String title, String description, float price, String imageURL) {
        this.title = title;
        this.description = description;
        this.price = price;
        this.imageURL = imageURL;
    }
 
    public int getId() {
        return id;
    }
 
    public void setId(int id) {
        this.id = id;
    }
 
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
 
    public float getPrice() {
        return price;
    }
 
    public void setPrice(float price) {
        this.price = price;
    }
    
    public String getImageURL() {
        return imageURL;
    }
 
    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String toString() {
        return "Producto: " + title + "\n" +
            description;
    }
 
}