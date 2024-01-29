package com.example.pfm.screens;

/**
 * The DataRefresh interface provides a contract for implementing data refresh functionality across various screens in the application.
 * This is particularly useful for ensuring that UI components show the most current data after changes are made elsewhere in the application,
 * such as adding, editing, or deleting incomes/expenses/budgets.
 */
public interface DataRefresh {
    void refreshData();
}
