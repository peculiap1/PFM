package com.example.pfm.dao;

import com.example.pfm.model.Income;
import com.example.pfm.util.MySQLConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for managing income related database operations.
 */
public class IncomeDAO {
    /**
     * Inserts a new income record into the database.
     *
     * @param income The Income object containing details to be added.
     * @return true if the operation was successful, false if not.
     */
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

    /**
     * Retrieves all income records for a specific user.
     *
     * @param userId The ID of the user whose income records are to be retrieved.
     * @return A list of Income objects.
     */

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

    /**
     * Updates an existing income record in the database.
     *
     * @param income The updated Income object.
     * @return true if the operation was successful, false if not.
     */
    public boolean updateIncome(Income income) {
        String sql = "UPDATE income SET amount = ?, source = ?, date = ? WHERE id = ? AND user_id = ?";

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

    /**
     * Deletes an income record from the database.
     *
     * @param id The ID of the income record to be deleted.
     * @param userId The ID of the user that the income record belongs to.
     * @return true if the operation was successful, false if not.
     */
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

    /**
     * Calculates the total income for the current month for a specific user.
     *
     * @param userId The ID of the user whose total income is calculated.
     * @return The total income amount for the current month.
     */
    public double getTotalIncomeForCurrentMonth(int userId) {
        String sql = "SELECT SUM(amount) AS total FROM income WHERE user_id = ? AND MONTH(date) = ? AND YEAR(date) = ?";

        try (Connection conn = MySQLConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            LocalDate now = LocalDate.now();
            stmt.setInt(1, userId);
            stmt.setInt(2, now.getMonthValue()); // Current month
            stmt.setInt(3, now.getYear()); // Current year

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0; //if error or no incomes found
    }

    /**
     * Calculates the total income for a specific month and year for a specific user.
     *
     * @param userId The ID of the user whose total income is calculated.
     * @param month  The month for which the total income is calculated.
     * @param year   The year for which the total income is calculated.
     * @return The total income amount for the specified month and year.
     */
    public double getTotalIncomeForMonth(int userId, int month, int year) {
        String sql = "SELECT SUM(amount) AS total FROM income WHERE user_id = ? AND MONTH(date) = ? AND YEAR(date) = ?";

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
}
