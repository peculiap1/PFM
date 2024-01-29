package com.example.pfm.model;

import java.time.LocalDate;

/**
 * Represents an income record for the PFM application.
 * This class captures the details of income sources for users.
 */

public class Income {
    private int id; // Unique identifier for the income record.
    private int userId; // Identifier of the user to whom the income belongs.
    private double amount; // The value of the income.
    private String source; // Description of the income source.
    private LocalDate date; // The date on which the income was received.

    /**
     * Default constructor for creating an Income instance without initial property values.
     */
    public Income() {
    }

    /**
     * Constructs an Income with specified details.
     *
     * @param userId The identifier of the user this income is associated with.
     * @param amount The value of the income.
     * @param source The description of the income source.
     * @param date The date on which the income was received.
     */
    public Income(int userId, double amount, String source, LocalDate date) {
        this.userId = userId;
        this.amount = amount;
        this.source = source;
        this.date = date;
    }

    //Getters and setters..
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
