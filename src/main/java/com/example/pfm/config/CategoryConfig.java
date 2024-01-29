package com.example.pfm.config;

import java.util.Arrays;
import java.util.List;

/**
 * This class provides a configuration for expense and budget categories within the application.
 * It is a list of predefined categories that can be used for categorizing expenses and budgets.
 */
public class CategoryConfig {
    public static final List<String> CATEGORIES = Arrays.asList(
            "Groceries", // Expenses related to food and household supplies.
            "Shopping", // General shopping expenses, including clothes, gadgets, etc.
            "Utilities", // Monthly utility bills such as electricity, water, internet, etc.
            "Entertainment", // Expenses on entertainment such as movies, events, subscriptions like Netflix.
            "Insurance", // Insurance-related expenses, including health, vehicle, property insurance.
            "Hobbies", // Costs associated with personal hobbies and activities.
            "Travel", // Expenses related to travel and vacations.
            "Other" // For any expenses that don't fit into the above categories.
    );
}
