package com.example.pfm.model;

import java.time.LocalDate;

/**
 * Represents a budget entity within the PFM application.
 * A budget is defined for a specific category and is associated with a user.
 */
public class Budget {
    private int id; // Unique identifier for the budget.
    private int userId; // Identifier of the user to whom the budget belongs.
    private String category; // The category the budget is set for.
    private double budgetLimit; // The limit set for this budget
    private double spentAmount; // The amount already spent within this budget's category.
    private LocalDate date; // The date representing the month and year the budget is set for.

    /**
     * Default constructor for creating a Budget instance without setting properties initially.
     */
    public Budget() {
    }

    /**
     * Constructs a Budget with specified details.
     *
     * @param id The budget's unique identifier.
     * @param userId The identifier of the user this budget is associated with.
     * @param category The category this budget is allocated to.
     * @param budgetLimit The limit set for the budget.
     * @param date The date the budget is set for, representing a month and year.
     */
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

    public double getSpentAmount() {
        return spentAmount;
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

    public void setSpentAmount(double spentAmount) {
        this.spentAmount = spentAmount;
    }
}
