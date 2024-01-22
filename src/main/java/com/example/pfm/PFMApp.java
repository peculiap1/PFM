package com.example.pfm;

import com.example.pfm.dao.ExpenseDAO;
import com.example.pfm.dao.IncomeDAO;
import com.example.pfm.model.Income;
import com.example.pfm.screens.*;
import com.example.pfm.service.UserService;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class PFMApp extends Application {

    private Stage primaryStage;
    private UserService userService;

    private IncomeDAO incomeDAO;
    private ExpenseDAO expenseDAO;

    @Override
    public void start(Stage primaryStage) {
        userService = new UserService();
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("PFM - Login");
        this.incomeDAO = new IncomeDAO();
        this.expenseDAO = new ExpenseDAO();

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

    public void showDashboard() {
        DashboardScreen dashboardScreen = new DashboardScreen(this, incomeDAO, expenseDAO);
        Scene scene = new Scene(dashboardScreen.getView(), 1024, 768);
        primaryStage.setScene(scene);
        primaryStage.setTitle("PFM - Dashboard");
    }

    public void showincomeScreen() {
        int currentUserId = userService.getCurrentUserId();
        IncomeScreen incomeScreen = new IncomeScreen(this, incomeDAO, currentUserId);
        Scene scene = new Scene(incomeScreen.getView(), 1024, 768);
        primaryStage.setScene(scene);
        primaryStage.setTitle("PFM - Income");
    }

    public static void main(String[] args) {
        launch(args);
    }
}


