package com.example.pfm.dao;

import com.example.pfm.util.MySQLConnection;
import com.example.pfm.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Data Access Object for the User entity. Provides methods for interacting
 * with the database regarding user-related operations.
 */

public class UserDAO {

    /**
     * Retrieves a User object by their username.
     *
     * @param username The username of the user to be retrieved.
     * @return A User object if found, or null if not found.
     */
    public User getUserByUsername(String username) {
        final String query = "SELECT * FROM users WHERE username = ?";

        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                return user;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Inserts a new user into the database.
     *
     * @param user The User object containing the information to be inserted.
     * @return true if the user was successfully inserted, false if not.
     */

    public boolean insertUser(User user) {
        final String query = "INSERT INTO users (username, password) VALUES (?, ?)";

        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());
            int affectedRows = stmt.executeUpdate();
            return affectedRows == 1;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Deletes a user from the database by their username.
     *
     * @param username The username of the user to be deleted.
     * @return true if the user was successfully deleted, false if failed.
     */

    public boolean deleteUser(String username) {
        final String query = "DELETE FROM users WHERE username = ?";
        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);
            int affectedRows = stmt.executeUpdate();

            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
