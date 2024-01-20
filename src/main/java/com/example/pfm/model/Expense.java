package com.example.pfm.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Expense {
    private int id;
    private int userId;
    private double amount;
    private String category;
    private LocalDate date;

    //Constructors
    public Expense(){
    }

    public Expense(int userId, double amount, String category, LocalDate date){
        this.userId = userId;
        this.amount = amount;
        this.category = category;
        this.date = date;
    }

    //Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId(){
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}
