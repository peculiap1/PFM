package com.example.pfm.model;

import java.time.LocalDate;

public class Income {
    private int id;
    private int userId;
    private double amount;
    private String source;
    private LocalDate date;

    //Constructors
    public Income() {

    }

    public Income(int userId, double amount, String source, LocalDate date) {
        this.userId = userId;
        this.amount = amount;
        this.source = source;
        this.date = date;
    }

    //Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id){
        this.id = id;
    }

    public int getUserId() {
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

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}
