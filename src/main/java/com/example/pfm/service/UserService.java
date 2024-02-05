package com.example.pfm.service;

import com.example.pfm.dao.ExpenseDAO;
import com.example.pfm.dao.IncomeDAO;
import com.example.pfm.dao.UserDAO;
import com.example.pfm.model.Expense;
import com.example.pfm.model.Income;
import com.example.pfm.model.User;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import org.mindrot.jbcrypt.BCrypt;

/**
 * Service class for user-related operations, including registration, authentication,
 * and managing user sessions.
 */
public class UserService {
    private UserDAO userDAO;
    private IncomeDAO incomeDAO;
    private ExpenseDAO expenseDAO;
    private User loggedInUser;

    // Maximum login attempts before locking the account temporarily.
    private static final int MAX_ATTEMPTS = 3;
    // Duration in minutes for which the account is locked after exceeding max login attempts.
    private static final int LOCKOUT_DURATION_MINUTES = 5;
    // Tracks the number of failed login attempts for usernames.
    private Map<String, Integer> loginAttempts = new HashMap<>();
    // Tracks lockout expiry time for usernames.
    private Map<String, LocalDateTime> lockoutExpiry = new HashMap<>();

    /**
     * Constructor initializing DAOs for user, income, and expense entities.
     */
    public UserService() {
        this.userDAO = new UserDAO();
        incomeDAO = new IncomeDAO();
        expenseDAO = new ExpenseDAO();
    }

    public UserService(UserDAO userDAO, IncomeDAO incomeDAO, ExpenseDAO expenseDAO) {
        this.userDAO = userDAO;
        this.incomeDAO = incomeDAO;
        this.expenseDAO = expenseDAO;
    }

    /**
     * Registers a new user with the provided username and password.
     * The password is hashed before storage for (extra) security purposes.
     *
     * @param username The desired username for the new account.
     * @param password The desired password for the new account.
     * @return A message indicating the outcome of the registration attempt.
     */
    public String registerUser(String username, String password) {
        if (username.trim().isEmpty() || password.trim().isEmpty()) {
            return "Username and password cannot be empty.";
        }

        if (userDAO.getUserByUsername(username) != null) {
            return "Username already exists.";
        }

        if (password.length() < 8) {
            return "Password must be at least 8 characters long.";
        }

        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

        boolean success = userDAO.insertUser(new User(username, hashedPassword));
        if (!success) {
            return "Registration failed. Please try again.";
        }
        return null;
    }

    /**
     * Attempts to authenticate a user with the provided username and password.
     * Accounts are temporarily locked after a specified number of failed attempts.
     *
     * @param username The username of the account attempting to log in.
     * @param password The password of the account attempting to log in.
     * @return A message indicating the outcome of the login attempt.
     */

    public String authenticateUser(String username, String password) {
        if (username.trim().isEmpty() || password.trim().isEmpty()) {
            return "Username and password cannot be empty.";
        }
        if (isAccountLocked(username)) {
            return "Account is temporarily locked due to multiple failed login attempts. Please try again later.";
        }
        User user = userDAO.getUserByUsername(username);
        if (user == null || !BCrypt.checkpw(password, user.getPassword())) {
            incrementLoginAttempts(username);
            return "Incorrect username and/or password.";
        }

        resetLoginAttempts(username);

        loggedInUser = user;
        return null;
    }


    // Helper methods
    private void incrementLoginAttempts(String username) {
        int attempts = loginAttempts.getOrDefault(username, 0);
        attempts++;
        loginAttempts.put(username, attempts);

        if (attempts >= MAX_ATTEMPTS) {
            lockoutExpiry.put(username, LocalDateTime.now().plusMinutes(LOCKOUT_DURATION_MINUTES));
        }
    }

    private boolean isAccountLocked(String username) {
        if (lockoutExpiry.containsKey(username)) {
            LocalDateTime expiryTime = lockoutExpiry.get(username);
            if (expiryTime.isAfter(LocalDateTime.now())) {
                return true;
            } else {
                // Lockout period has expired
                lockoutExpiry.remove(username);
                resetLoginAttempts(username);
                return false;
            }
        }
        return false;
    }

    private void resetLoginAttempts(String username) {
        loginAttempts.remove(username);
        lockoutExpiry.remove(username);
    }

    public int getCurrentUserId() {
        return loggedInUser != null ? loggedInUser.getId() : -1;
    }

    public boolean addIncome(Income income) {
        return incomeDAO.insertIncome(income);
    }
    public boolean addExpense(Expense expense) {
        return expenseDAO.insertExpense(expense);
    }
    public String getCurrentUsername() {
        return loggedInUser != null ? loggedInUser.getUsername() : null;
    }

    /**
     * Method for logging out a user.
     */
    public void logoutUser() {
        loggedInUser = null;
        loginAttempts.clear();
        lockoutExpiry.clear();
    }
}
