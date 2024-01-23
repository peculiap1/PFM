package com.example.pfm.model;

import java.time.LocalDate;

public class Budget {
    private int id;
    private int userId;
    private String category;
    private double budgetLimit;
    private LocalDate date;

    public Budget() {
    }

    // Budget constructor
    public Budget(int id, int userId, String category, double budgetLimit, LocalDate date) {
        this.id = id;
        this.userId = userId;
        this.category = category;
        this.budgetLimit = budgetLimit;
        this.date = date;
    }

    // Budget getters
    public int getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }

    public String getCategory() {
        return category;
    }

    public double getBudgetLimit() {
        return budgetLimit;
    }

    public LocalDate getDate() {
        return date;
    }

    //Budget setters

    public void setId(int id) {
        this.id = id;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setBudgetLimit(double budgetLimit) {
        this.budgetLimit = budgetLimit;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}
