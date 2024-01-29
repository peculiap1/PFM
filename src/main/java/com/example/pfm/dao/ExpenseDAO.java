package com.example.pfm.dao;

import com.example.pfm.model.Expense;
import com.example.pfm.util.MySQLConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Data Access Object for managing expenses in the database.
 * Provides methods to insert, update, delete, and query expense records.
 */

public class ExpenseDAO {
    /**
     * Inserts a new expense record into the database.
     *
     * @param expense The Expense object containing expense details.
     * @return true if the expense is successfully inserted, false if not.
     */
    public boolean insertExpense(Expense expense) {
        String sql = "INSERT INTO expense (user_id, amount, date, category) VALUES (?, ?, ?, ?)";

        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, expense.getUserId());
            stmt.setDouble(2, expense.getAmount());
            stmt.setDate(3, Date.valueOf(expense.getDate()));
            stmt.setString(4, expense.getCategory());

            int affectedRows = stmt.executeUpdate();
            return affectedRows == 1;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Retrieves all expenses for a specific user.
     *
     * @param userId The ID of the user whose expenses are to be retrieved.
     * @return A list of Expense objects for the specified user.
     */
    public List<Expense> getAllExpensesByUserId(int userId) {
        List<Expense> expenses = new ArrayList<>();

        String sql = "SELECT * FROM expense WHERE user_id = ?";

        try (Connection conn = MySQLConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Expense expense = new Expense();
                expense.setId(rs.getInt("id"));
                expense.setUserId(rs.getInt("user_id"));
                expense.setAmount(rs.getDouble("amount"));
                expense.setCategory(rs.getString("category"));
                expense.setDate(rs.getDate("date").toLocalDate());
                expenses.add(expense);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return expenses;
    }

    /**
     * Retrieves an expense by its ID.
     *
     * @param id The ID of the expense to be retrieved.
     * @return An Expense object if found, null if not found.
     */
    public Expense getExpenseById(int id) {
        String sql = "SELECT * FROM Expense WHERE id = ?";

        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Expense expense = new Expense();
                expense.setId(rs.getInt("id"));
                expense.setUserId(rs.getInt("user_id"));
                expense.setAmount(rs.getDouble("amount"));
                expense.setCategory(rs.getString("category"));
                expense.setDate(rs.getDate("date").toLocalDate());
                return expense;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Updates an existing expense record in the database.
     *
     * @param expense The Expense object containing updated details.
     * @return true if the expense is successfully updated, false if not.
     */
    public boolean updateExpense(Expense expense) {
        String sql = "UPDATE expense SET amount = ?, category = ?, date = ? WHERE id = ? AND user_id = ?";

        try (Connection conn = MySQLConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDouble(1, expense.getAmount());
            stmt.setString(2, expense.getCategory());
            stmt.setDate(3, Date.valueOf(expense.getDate()));
            stmt.setInt(4, expense.getId());
            stmt.setInt(5, expense.getUserId());

            int affectedRows = stmt.executeUpdate();
            return affectedRows == 1;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Deletes an expense from the database.
     *
     * @param id The ID of the expense to be deleted.
     * @param userId The ID of the user who owns the expense.
     * @return true if the expense is successfully deleted, false if failed.
     */
    public boolean deleteExpense(int id, int userId) {
        String sql = "DELETE FROM expense WHERE id = ? AND user_id = ?";

        try (Connection conn = MySQLConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.setInt(2, userId);

            int affectedRows = stmt.executeUpdate();
            return affectedRows == 1;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    /**
     * Calculates the total expenses for the current month for a specific user.
     *
     * @param userId The ID of the user.
     * @return The total amount of expenses for the current month.
     */
    public double getTotalExpenseForCurrentMonth(int userId) {
        String sql = "SELECT SUM(amount) AS total FROM expense WHERE user_id = ? AND MONTH(date) = ? AND YEAR(date) = ?";

        try (Connection conn = MySQLConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            LocalDate now = LocalDate.now();
            stmt.setInt(1, userId);
            stmt.setInt(2, now.getMonthValue());
            stmt.setInt(3, now.getYear());

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    /**
     * Calculates the total expenses for a specific month and year for a specific user.
     *
     * @param userId The ID of the user.
     * @param month The month for which to calculate expenses.
     * @param year The year for which to calculate expenses.
     * @return The total amount of expenses for the specified month and year.
     */
    public double getTotalExpenseForMonth(int userId, int month, int year) {
        String sql = "SELECT SUM(amount) AS total FROM expense WHERE user_id = ? AND MONTH(date) = ? AND YEAR(date) = ?";

        try(Connection conn = MySQLConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.setInt(2, month);
            stmt.setInt(3, year);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    /**
     * Retrieves the total amount spent per category for the current month for a specific user.
     *
     * @param userId The ID of the user.
     * @return A Map with categories as keys and the total spent as values.
     */
    public Map<String, Double> getTotalSpentPerCategory(int userId) {
        Map<String, Double> categoryTotals = new HashMap<>();

        String sql = "SELECT category, SUM(amount) AS total FROM expense WHERE user_id = ? AND MONTH(date) = ? AND YEAR(date) = ? GROUP BY category";

        try (Connection conn = MySQLConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            LocalDate now = LocalDate.now();
            stmt.setInt(1, userId);
            stmt.setInt(2, now.getMonthValue());
            stmt.setInt(3, now.getYear());

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String category = rs.getString("category");
                double total = rs.getDouble("total");
                categoryTotals.put(category, total);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return categoryTotals; //returns map of category totals
    }

    /**
     * Calculates the total amount spent in a specific category for the current month for a specific user.
     *
     * @param userId The ID of the user.
     * @param category The category for which to calculate the total spent.
     * @return The total amount spent in the specified category for the current month.
     */

    public double getTotalSpentForCategory(int userId, String category) {
        double totalSpent = 0.0;

        String sql = "SELECT SUM(amount) AS total FROM expense WHERE user_id = ? AND category = ? AND MONTH(date) = MONTH(CURRENT_DATE) AND YEAR(date) = YEAR(CURRENT_DATE)";

        try (Connection conn = MySQLConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.setString(2, category);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                totalSpent = rs.getDouble("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return totalSpent;
    }
}
