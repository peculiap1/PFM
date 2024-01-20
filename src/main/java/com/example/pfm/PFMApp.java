package com.example.pfm;

import com.example.pfm.model.Income;
import com.example.pfm.screens.ExpenseEntryScreen;
import com.example.pfm.screens.IncomeEntryScreen;
import com.example.pfm.screens.LoginScreen;
import com.example.pfm.screens.RegistrationScreen;
import com.example.pfm.service.UserService;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class PFMApp extends Application {

    private Stage primaryStage;
    private UserService userService;

    @Override
    public void start(Stage primaryStage) {
        userService = new UserService();
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("PFM - Login");

        showLoginScreen();
    }

    public UserService getUserService() {
        return userService;
    }

    public void showLoginScreen() {
        LoginScreen loginScreen = new LoginScreen(this, userService);
        Scene scene = new Scene(LoginScreen.getView(), 400, 275);
        primaryStage.setScene(scene);
        primaryStage.setTitle("PFM - Login");
        primaryStage.show();
    }

    public void showRegistrationScreen() {
        RegistrationScreen registrationScreen = new RegistrationScreen(this, userService);
        Scene scene = new Scene(registrationScreen.getView(),400, 275);
        primaryStage.setScene(scene);
        primaryStage.setTitle("PFM - Register");
    }

    public void showIncomeEntryScreen() {
        int currentUserId = userService.getCurrentUserId();
        IncomeEntryScreen incomeEntryScreen = new IncomeEntryScreen(this, currentUserId);
        Scene scene = new Scene(incomeEntryScreen.getView(), 400, 275);
        primaryStage.setScene(scene);
        primaryStage.setTitle("PFM - Add Income");
    }

    public void showExpenseEntryScreen() {
        int currentUserId = userService.getCurrentUserId();
        ExpenseEntryScreen expenseEntryScreen = new ExpenseEntryScreen(this, currentUserId);
        Scene scene = new Scene(expenseEntryScreen.getView(), 400, 275);
        primaryStage.setScene(scene);
        primaryStage.setTitle("PFM - Add Expense");
    }

    public static void main(String[] args) {
        launch(args);
    }
}


