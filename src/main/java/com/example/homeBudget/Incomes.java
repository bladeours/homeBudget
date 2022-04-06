package com.example.homeBudget;

import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.CheckBox;

public class Incomes {
    private SimpleIntegerProperty id = new SimpleIntegerProperty(0);
    private SimpleStringProperty date = new SimpleStringProperty("");
    private SimpleStringProperty category = new SimpleStringProperty("");
    private SimpleStringProperty amount = new SimpleStringProperty("");
    private CheckBox select = new CheckBox();

    public Incomes(int id, String date, String category, String amount, CheckBox select) {
        setId(id);
        setDate(date);
        setCategory(category);
        setAmount(amount);
        setSelect(select);
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

    public String getCategory() {
        return category.get();
    }

    public void setCategory(String category) {
        this.category.set(category);
    }

    public String getAmount() {
        return amount.get();
    }

    public void setAmount(String amount) {
        this.amount.set(amount);
    }

    public CheckBox getSelect(){
        return select;
    }

    public void setSelect(CheckBox select){
        this.select = select;
    }

    @Override
    public String toString() {
        return getId() + "\t" + getDate()  + "\t" + String.format("%15s",getCategory()) +  "\t " +
                    getAmount();
    }
}
