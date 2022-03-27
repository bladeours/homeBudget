package com.example.homeBudget;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class Purchases {
    private SimpleIntegerProperty id = new SimpleIntegerProperty(0);
    private SimpleStringProperty date = new SimpleStringProperty("");
    private SimpleStringProperty shop = new SimpleStringProperty("");
    private  SimpleStringProperty category = new SimpleStringProperty("");
    private SimpleStringProperty price = new SimpleStringProperty("");

    public Purchases(){
    }

    public Purchases(int id, String date, String shop, String category, String price) {
        setId(id);
        setDate(date);
        setShop(shop);
        setCategory(category);
        setPrice(price);
    }

    public int getId() {
        return id.get();
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public String getDate() {
        return date.get();
    }

    public void setDate(String date) {
        this.date.set(date);
    }

    public String getShop() {
        return shop.get();
    }

    public void setShop(String shop) {
        this.shop.set(shop);
    }

    public String getCategory() {
        return category.get();
    }

    public void setCategory(String category) {
        this.category.set(category);
    }

    public String getPrice() {
        return price.get();
    }

    public void setPrice(String price) {
        this.price.set(price);
    }
}
