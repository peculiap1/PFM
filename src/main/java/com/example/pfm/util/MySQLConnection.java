package com.example.pfm.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Utility class for creating connections to the MySQL database.
 */
public class MySQLConnection {
    private static final String DATABASE_URL = "jdbc:mysql://adainforma.tk:3306/bp2_pfm";
    private static final String DATABASE_USER = "pfm";
    private static final String DATABASE_PASSWORD = "8t9&Zf1t9";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DATABASE_URL, DATABASE_USER, DATABASE_PASSWORD);
    }
}
