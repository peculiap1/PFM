package com.example.pfm.model;

/**
 * Represents a user of the PFM application.
 * This class encapsulates user-specific details such as username and password, user ID and authentication.
 */
public class User {
    private int id; // Unique identifier for the user.
    private String username; // The user's chosen username for login.
    private String password; // The user's password for authentication.

    /**
     * Constructs a new User with specified username and password.
     *
     * @param username The username chosen by the user, used for login.
     * @param password The user's password, used for authentication.
     */
    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    /**
     * Default constructor for creating a User instance without initial property values.
     */
    public User() {
    }

    // getters and setters..

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
