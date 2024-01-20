package com.example.pfm.dao;

import com.example.pfm.model.Expense;
import com.example.pfm.util.MySQLConnection;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ExpenseDAOTest {

    private static final int TEST_USER_ID = 7;
    private static ExpenseDAO expenseDAO;

    @BeforeAll
    static void setup() {
        expenseDAO = new ExpenseDAO();
    }

    @Test
    void testGetAllExpensesByUserId() {
        List<Expense> expenses = expenseDAO.getAllExpensesByUserId(TEST_USER_ID);
        assertFalse(expenses.isEmpty(), "Expenses should not be empty");
    }
    @Test
    void testInsertExpense() {
        Expense newExpense = new Expense(TEST_USER_ID, 100.00, "Groceries", LocalDate.now());
        assertTrue(expenseDAO.insertExpense(newExpense), "Insert expense should return true");
        List<Expense> expenses = expenseDAO.getAllExpensesByUserId(TEST_USER_ID);
        assertFalse(expenses.isEmpty(), "Expenses should not be empty");
        Expense lastExpense = expenses.get(expenses.size() - 1);
        assertEquals(100.00, lastExpense.getAmount(), "The amount should match the inserted value");
    }

    @Test
    void testUpdateExpense() {
        Expense expenseToUpdate = new Expense(TEST_USER_ID, 500.00, "TestSource", LocalDate.now());
        expenseDAO.insertExpense(expenseToUpdate);
        int expenseId = 5;

        expenseToUpdate.setId(expenseId);
        expenseToUpdate.setAmount(600.00);

        assertTrue(expenseDAO.updateExpense(expenseToUpdate));

        Expense updatedExpense = expenseDAO.getExpenseById(expenseId);

        assertNotNull(updatedExpense, "Updated expense should not be null");
        assertEquals(600.00, updatedExpense.getAmount(), "The amount should be updated");
    }

    @Test
    void testDeleteExpense() {
        int expenseId = 4;
        assertTrue(expenseDAO.deleteExpense(expenseId, TEST_USER_ID), "Delete expense should return true");

        Expense deletedExpense = expenseDAO.getExpenseById(expenseId);
            assertNull(deletedExpense, "Deleted expense should no longer exist");
        }

    /** @AfterAll
    static void tearDown() {
        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM expense WHERE user_id = ?")) {

            stmt.setInt(1, TEST_USER_ID);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    **/
}
