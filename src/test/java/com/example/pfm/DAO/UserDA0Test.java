package com.example.pfm;

import com.example.pfm.dao.UserDAO;
import com.example.pfm.model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserDA0Test {

    private UserDAO userDAO;
    private User testUser;

    @BeforeEach
    void setUp() {
        userDAO = new UserDAO();
        testUser = new User("testUser", "testPass");
        userDAO.insertUser(testUser);
    }

    @AfterEach
    void tearDown() {
        userDAO.deleteUser(testUser.getUsername());
    }
    @Test
    void getUserByUsername() {
        User retrievedUser = userDAO.getUserByUsername("testUser");
        assertNotNull(retrievedUser, "The retrieved user should not be null");
        assertEquals(testUser.getUsername(), retrievedUser.getUsername(), "The username should match the test user's username");
        assertEquals(testUser.getPassword(), retrievedUser.getPassword(), "The passwords should match");
    }

    @Test
    void insertUser() {
        User retrievedUser = userDAO.getUserByUsername("testUser");
        assertNotNull(retrievedUser, "The retrieved user should not be null");
        assertEquals(testUser.getUsername(), retrievedUser.getUsername(), "The username should match the test user's username");
        assertEquals(testUser.getPassword(), retrievedUser.getPassword(), "The passwords should match");
    }
}