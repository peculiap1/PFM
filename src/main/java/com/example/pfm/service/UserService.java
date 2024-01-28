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

public class UserService {
    private UserDAO userDAO;
    private IncomeDAO incomeDAO;

    private ExpenseDAO expenseDAO;
    private User loggedInUser;
    private static final int MAX_ATTEMPTS = 3;
    private static final int LOCKOUT_DURATION_MINUTES = 1;

    private Map<String, Integer> loginAttempts = new HashMap<>();
    private Map<String, LocalDateTime> lockoutExpiry = new HashMap<>();

    public UserService() {
        userDAO = new UserDAO();
        incomeDAO = new IncomeDAO();
        expenseDAO = new ExpenseDAO();
    }

    public String registerUser(String username, String password) {

        // Checks if the username and/or password are empty
        if (username.trim().isEmpty() || password.trim().isEmpty()) {
            return "Username and password cannot be empty.";
        }

        // Checks if the username already exists
        if (userDAO.getUserByUsername(username) != null) {
            return "Username already exists.";
        }

        // Checks for minimum password length (for extra security)
        if (password.length() < 8) {
            return "Password must be at least 8 characters long.";
        }


        boolean success = userDAO.insertUser(new User(username, password));
        if (!success) {
            return "Registration failed. Please try again.";
        }
        return null;
    }

    public String authenticateUser(String username, String password) {
        if (username.trim().isEmpty() || password.trim().isEmpty()) {
            return "Username and password cannot be empty.";
        }
        if (isAccountLocked(username)) {
            return "Account is temporarily locked due to multiple failed login attempts. Please try again later.";
        }
        User user = userDAO.getUserByUsername(username);
        if (user == null) {
            return "Incorrect username and/or password.";
        }
        if (!user.getPassword().equals(password)){
            incrementLoginAttempts(username);
            return "Incorrect username and/or password.";
        }
        loggedInUser = user;
        return null;
    }

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
    public void logoutUser() {
        loggedInUser = null;
        loginAttempts.clear();
        lockoutExpiry.clear();
    }
}
