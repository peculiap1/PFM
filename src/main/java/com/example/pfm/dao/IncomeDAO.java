package com.example.pfm.dao;

import com.example.pfm.model.Income;
import com.example.pfm.util.MySQLConnection;

import javax.xml.transform.Result;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class IncomeDAO {

    public boolean insertIncome(Income income) {
        String sql = "INSERT INTO income (user_id, amount, source, date) VALUES (?, ?, ?, ?)";
        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, income.getUserId());
            stmt.setDouble(2, income.getAmount());
            stmt.setString(3, income.getSource());
            stmt.setDate(4, Date.valueOf(income.getDate()));

            int affectedRows = stmt.executeUpdate();
            return affectedRows == 1;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Income getIncomeById(int id) {
        String sql = "SELECT * FROM Income WHERE id = ?";
        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Income income = new Income();
                income.setId(rs.getInt("id"));
                income.setUserId(rs.getInt("userId"));
                income.setAmount(rs.getDouble("amount"));
                income.setSource(rs.getString("source"));
                income.setDate(rs.getDate("date").toLocalDate());
                return income;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle exception
        }
        return null;
    }


    public List<Income> getAllIncomesByUserId(int userId) {
        List<Income> incomes = new ArrayList<>();
        String sql = "SELECT * FROM income WHERE user_id = ?";
        try (Connection conn = MySQLConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Income income = new Income();
                income.setId(rs.getInt("id"));
                income.setUserId(rs.getInt("user_id"));
                income.setAmount(rs.getDouble("amount"));
                income.setSource(rs.getString("source"));
                income.setDate(rs.getDate("date").toLocalDate());
                incomes.add(income);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return incomes;
    }

    public boolean updateIncome(Income income) {
        String sql = "UPDATE income SET amount = ?, source = ?, date = ?, WHERE id = ? AND user_id = ?";
        try (Connection conn = MySQLConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDouble(1, income.getAmount());
            stmt.setString(2, income.getSource());
            stmt.setDate(3, Date.valueOf(income.getDate()));
            stmt.setInt(4, income.getId());
            stmt.setInt(5, income.getUserId());

            int affectedRows = stmt.executeUpdate();
            return affectedRows == 1;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteIncome(int id, int userId) {
        String sql = "DELETE FROM income WHERE id = ? AND user_id = ?";
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
