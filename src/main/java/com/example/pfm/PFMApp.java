package com.example.pfm;

import com.example.pfm.dao.BudgetDAO;
import com.example.pfm.dao.ExpenseDAO;
import com.example.pfm.dao.IncomeDAO;
import com.example.pfm.model.Income;
import com.example.pfm.model.User;
import com.example.pfm.screens.*;
import com.example.pfm.service.UserService;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.XYChart;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class PFMApp extends Application {

    private Stage primaryStage;
    private UserService userService;
    private User user;
    private IncomeDAO incomeDAO;
    private ExpenseDAO expenseDAO;
    private BudgetDAO budgetDAO;
    private MainScreen mainScreen;
    private IncomeScreen incomeScreen;
    private ExpenseScreen expenseScreen;
    private BudgetScreen budgetScreen;
    private DashboardScreen dashboardScreen;
    private ReportScreen reportScreen;
    private List<DataRefresh> refreshListeners = new ArrayList<>();


    @Override
    public void start(Stage primaryStage) {
        Font.loadFont(getClass().getResourceAsStream("/fonts/OpenSans-VariableFont_wdth,wght.ttf"), 14);

        user = new User();
        userService = new UserService();
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("PFM - Login");
        this.incomeDAO = new IncomeDAO();
        this.expenseDAO = new ExpenseDAO();
        this.budgetDAO = new BudgetDAO(expenseDAO);
        this.incomeScreen = new IncomeScreen(this, incomeDAO, userService.getCurrentUserId());
        this.expenseScreen = new ExpenseScreen(this, expenseDAO, userService.getCurrentUserId());
        this.budgetScreen = new BudgetScreen(this, budgetDAO, expenseDAO, userService.getCurrentUserId());
        this.dashboardScreen = new DashboardScreen(this, incomeDAO, expenseDAO, budgetDAO, userService.getCurrentUserId());
        this.reportScreen = new ReportScreen(this, incomeDAO, expenseDAO, primaryStage);

        this.mainScreen = new MainScreen( this, incomeDAO, expenseDAO, budgetDAO, userService.getCurrentUserId(), incomeScreen, expenseScreen, budgetScreen, dashboardScreen, reportScreen, primaryStage);
        showLoginScreen();
    }

    public UserService getUserService() {
        return userService;
    }

    public void showLoginScreen() {
        LoginScreen loginScreen = new LoginScreen(this, userService);
        Scene scene = new Scene(LoginScreen.getView(), 807, 500);
        primaryStage.setScene(scene);
        primaryStage.setTitle("PFM - Login");
        primaryStage.show();
        primaryStage.setResizable(false);
    }

    public void showRegistrationScreen() {
        RegistrationScreen registrationScreen = new RegistrationScreen(this, userService);
        Scene scene = new Scene(registrationScreen.getView(),807, 500);
        primaryStage.setScene(scene);
        primaryStage.setTitle("PFM - Register");
        primaryStage.setResizable(false);
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

    public void showMainScreen() {
        int currentUserId = userService.getCurrentUserId();
        MainScreen mainScreen = new MainScreen(this, incomeDAO, expenseDAO, budgetDAO, currentUserId, incomeScreen, expenseScreen, budgetScreen, dashboardScreen, reportScreen, primaryStage);
        Scene scene = new Scene(mainScreen.getView(), 1204, 768);
        primaryStage.setScene(scene);
        scene.getStylesheets().add(getClass().getResource("/com/example/pfm/stylesheets/mainscreen.css").toExternalForm());
        primaryStage.setTitle("PFM");
    }

    public void registerListener(DataRefresh listener) {
        refreshListeners.add(listener);
    }

    public void onDataChanged() {
        for (DataRefresh listener : refreshListeners) {
            listener.refreshData();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}


