package com.example.pfm.service;

import com.example.pfm.dao.UserDAO;
import com.example.pfm.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class UserServiceTest {

    private UserService userService;
    private UserDAO userDAO;

    @BeforeEach
    void setUp() {
        userDAO = mock(UserDAO.class);
        userService = new UserService();
    }

    @Test
    void testRegisterUserWithShortPassword() {
        String result = userService.registerUser("newuser", "short");
        assertEquals("Password must be at least 8 characters long.", result);
    }

    @Test
    void testRegisterFieldEmpty() {
        String result = userService.registerUser("", "test1234");
        assertEquals("Username and password cannot be empty.", result);
    }

    @Test
    void testPasswordFieldEmpty() {
        String result = userService.registerUser("newuser", "");
        assertEquals("Username and password cannot be empty.", result);
    }


}