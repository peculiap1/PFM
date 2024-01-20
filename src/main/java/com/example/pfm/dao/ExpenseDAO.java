package com.example.pfm.dao;

import com.example.pfm.model.Expense;
import com.example.pfm.util.MySQLConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ExpenseDAO {

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
            // Handle exception
        }
        return null;
    }


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

}
