package com.example.pfm.model;

import java.time.LocalDate;

/**
 * Represents an expense record in the PFM application.
 * Each expense is associated with a user and categorized.
 */

public class Expense {
    private int id; // Unique identifier for the expense.
    private int userId;  // Identifier of the user to whom the expense belongs.
    private double amount; // The value of the expense.
    private String category; // The category this expense is allocated to.
    private LocalDate date; // The date of the expense.

    /**
     * Default constructor for creating an Expense instance without setting properties initially.
     */
    public Expense(){
    }

    /**
     * Constructs an Expense with specified details.
     *
     * @param userId The identifier of the user this expense is associated with.
     * @param amount The value of the expense.
     * @param category The category this expense is allocated to.
     * @param date The date of the expense.
     */

    public Expense(int userId, double amount, String category, LocalDate date){
        this.userId = userId;
        this.amount = amount;
        this.category = category;
        this.date = date;
    }

    //Getters and setters..
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
