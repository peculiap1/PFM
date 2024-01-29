package com.example.pfm.dao;

import com.example.pfm.model.Budget;
import com.example.pfm.util.MySQLConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Data Access Object for managing budget records in the database.
 * This class provides methods to insert, update, delete, and retrieve budget records.
 */

public class BudgetDAO {
    private ExpenseDAO expenseDAO;

    /**
     * Constructs a BudgetDAO with a reference to an ExpenseDAO.
     * The ExpenseDAO is used for calculating spent amounts in budget categories.
     *
     * @param expenseDAO An instance of ExpenseDAO for expense-related operations.
     */
    public BudgetDAO(ExpenseDAO expenseDAO) {
        this.expenseDAO = expenseDAO;
    }

    /**
     * Inserts a new budget record into the database.
     *
     * @param budget The Budget object containing the budget details.
     * @return true if the budget was successfully inserted, false if failed.
     */
    public boolean insertBudget(Budget budget) {
        String sql = "INSERT INTO budget (user_id, category, budget_limit, date) VALUES (?, ?, ?, ?)";

        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

                 stmt.setInt(1, budget.getUserId());
                 stmt.setString(2, budget.getCategory());
                 stmt.setDouble(3, budget.getBudgetLimit());
                 stmt.setDate(4, Date.valueOf(budget.getDate()));

                 int affectedRows = stmt.executeUpdate();

                 if (affectedRows == 0) {
                     throw new SQLException("Creating budget failed, no rows affected.");
                 }

                 try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                     if (generatedKeys.next()) {
                         budget.setId(generatedKeys.getInt(1));
                     } else {
                         throw new SQLException("Creating budget failed, no ID obtained");
                     }
                 }
                 return true;
        } catch (SQLException e) {
                 e.printStackTrace();
                 return false;
        }
    }

    /**
     * Retrieves all budget records for a specific user.
     *
     * @param userId The ID of the user whose budgets are to be retrieved.
     * @return A list of Budget objects for the specified user.
     */
    public List<Budget> getAllBudgetsByUserId(int userId) {
        List<Budget> budgets = new ArrayList<>();

        Map<String, Double> spentTotals = expenseDAO.getTotalSpentPerCategory(userId);

        String sql = "SELECT * FROM budget WHERE user_id = ?";

        try (Connection conn = MySQLConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Budget budget = new Budget();
                budget.setId(rs.getInt("id"));
                budget.setUserId(rs.getInt("user_id"));
                budget.setCategory(rs.getString("category"));
                budget.setBudgetLimit(rs.getDouble("budget_limit"));
                budget.setDate(rs.getDate("date").toLocalDate());

                String category = budget.getCategory();
                double spentAmount = spentTotals.getOrDefault(category, 0.0);
                budget.setSpentAmount(spentAmount);

                budgets.add(budget);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return budgets;
    }

    /**
     * Updates an existing budget record in the database.
     *
     * @param budget The Budget object containing updated budget details.
     * @return true if the budget was successfully updated, false if failed.
     */

    public boolean updateBudget(Budget budget) {
        String sql = "UPDATE budget SET category = ?, budget_limit = ?, date = ? WHERE id = ? AND user_id = ?";

        try (Connection conn = MySQLConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, budget.getCategory());
            stmt.setDouble(2, budget.getBudgetLimit());
            stmt.setDate(3, Date.valueOf(budget.getDate()));
            stmt.setInt(4, budget.getId());
            stmt.setInt(5, budget.getUserId());

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Deletes a budget from the database.
     *
     * @param id The ID of the budget to be deleted.
     * @param userId The ID of the user who owns the budget.
     * @return true if the budget was successfully deleted, false if failed.
     */

    public boolean deleteBudget(int id, int userId) {
        String sql = "DELETE FROM budget WHERE id = ? AnD user_id = ?";

        try (Connection conn = MySQLConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.setInt(2, userId);

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
