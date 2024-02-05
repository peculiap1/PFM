package com.example.pfm.service;

import com.example.pfm.dao.ExpenseDAO;
import com.example.pfm.dao.IncomeDAO;
import com.example.pfm.dao.UserDAO;
import com.example.pfm.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    private UserService userService;
    private UserDAO userDAO;
    private IncomeDAO incomeDAO;
    private ExpenseDAO expenseDAO;

    @BeforeEach
    void setUp() {

        userDAO = Mockito.mock(UserDAO.class);
        incomeDAO = Mockito.mock(IncomeDAO.class);
        expenseDAO = Mockito.mock(ExpenseDAO.class);

        userService = new UserService(userDAO, incomeDAO, expenseDAO);
    }

    @Test
    void testCorrectUsernameAndPassword() {
        String username = "testgebruiker";
        String password = "correctWachtwoord!123";
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

        // Prepare the fake user object with the hashed password
        User fakeUser = new User();
        fakeUser.setUsername(username);
        fakeUser.setPassword(hashedPassword);

        // When the userDAO is asked for the user by username, return the fake user
        when(userDAO.getUserByUsername(username)).thenReturn(fakeUser);

        // Correct username and password
        String result = userService.authenticateUser(username, password);

        assertNull(result, "Expected null for successful login but got: " + result);
    }

    @Test
    void testIncorrectPassword() {
        String correctPassword = "correctPassword!123";
        String hashedCorrectPassword = BCrypt.hashpw(correctPassword, BCrypt.gensalt());
        User fakeUser = new User("testgebruiker", hashedCorrectPassword);
        when(userDAO.getUserByUsername("testgebruiker")).thenReturn(fakeUser);

        // Correct username, but wrong password
        String result = userService.authenticateUser("testgebruiker", "verkeerdWachtwoord!456");

        assertNotNull(result);
        assertEquals("Incorrect username and/or password.", result);
    }

    @Test
    void testAccountLockedAfterFailedAttempts() {
        String username = "testgebruiker";
        String correctPassword = "correctPassword!123";
        String hashedCorrectPassword = BCrypt.hashpw(correctPassword, BCrypt.gensalt());
        User fakeUser = new User(username, hashedCorrectPassword);
        when(userDAO.getUserByUsername(username)).thenReturn(fakeUser);

        // Simulate MAX_ATTEMPTS failed login attempts
        for (int i = 0; i < 3 - 1; i++) {
            userService.authenticateUser(username, "nogEenVerkeerdWachtwoord!789");
        }

        //This attempt should lock the account
        userService.authenticateUser(username, "nogEenVerkeerdWachtwoord!789");
        //This attempt should find the account locked
        String result = userService.authenticateUser(username, "nogEenVerkeerdWachtwoord!789");

        assertNotNull(result);
        assertEquals("Account is temporarily locked due to multiple failed login attempts. Please try again later.", result);
    }

    @Test
    void testRegisterUser_ExistingUsername() {
        String existingUsername = "bestaandeGebruiker";
        String password = "willekeurigWachtwoord123";
        User existingUser = new User(existingUsername, password);

        // Mock the behavior to simulate the existing user in the database
        when(userDAO.getUserByUsername(existingUsername)).thenReturn(existingUser);

        // Act
        String result = userService.registerUser(existingUsername, password);

        // Assert
        assertNotNull(result);
        assertEquals("Username already exists.", result);
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