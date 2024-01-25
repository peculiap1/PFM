package com.example.pfm.screens;

import com.example.pfm.PFMApp;
import com.example.pfm.dao.BudgetDAO;
import com.example.pfm.dao.ExpenseDAO;
import com.example.pfm.dao.IncomeDAO;
import com.example.pfm.model.Budget;
import com.example.pfm.model.Income;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.VBox;

public class MainScreen{
    private VBox view;
    private PFMApp app;
    private IncomeDAO incomeDAO;
    private ExpenseDAO expenseDAO;
    private BudgetDAO budgetDAO;
    private IncomeScreen incomeScreen;
    private ExpenseScreen expenseScreen;
    private BudgetScreen budgetScreen;
    private DashboardScreen dashboardScreen;
    private int userId;


    public MainScreen(PFMApp app,
                      IncomeDAO incomeDAO,
                      ExpenseDAO expenseDAO,
                      BudgetDAO budgetDAO,
                      int userId,
                      IncomeScreen incomeScreen,
                      ExpenseScreen expenseScreen,
                      BudgetScreen budgetScreen,
                      DashboardScreen dashboardScreen) {
        this.app = app;
        this.incomeDAO = incomeDAO;
        this.expenseDAO = expenseDAO;
        this.budgetDAO = budgetDAO;
        this.userId = userId;
        this.incomeScreen = incomeScreen;
        this.expenseScreen = expenseScreen;
        this.budgetScreen = budgetScreen;
        this.dashboardScreen = dashboardScreen;
        createView();
    }



    private void createView() {
        view= new VBox();
        TabPane tabPane = new TabPane();

        Tab dashboardTab = new Tab("Dashboard");
        dashboardTab.setContent(new DashboardScreen(app, incomeDAO, expenseDAO, budgetDAO, userId).getView());
        dashboardTab.setClosable(false);

        dashboardTab.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
            if (isSelected) {
                dashboardScreen.refreshData();
            }
        });

        Tab incomesTab = new Tab("Incomes");
        incomesTab.setContent(new IncomeScreen(app, new IncomeDAO(), app.getUserService().getCurrentUserId()).getView());
        incomesTab.setClosable(false);


        Tab expensesTab = new Tab("Expenses");
        expensesTab.setContent(new ExpenseScreen(app, new ExpenseDAO(), app.getUserService().getCurrentUserId()).getView());
        expensesTab.setClosable(false);

        Tab budgetTab = new Tab("Budgets");
        budgetTab.setContent(new BudgetScreen(app, budgetDAO, expenseDAO, userId).getView());
        budgetTab.setClosable(false);

        budgetTab.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
            if (isSelected) {
                dashboardScreen.refreshData();
            }
        });

        tabPane.getTabs().addAll(dashboardTab, incomesTab, expensesTab, budgetTab);
        view.getChildren().add(tabPane);
    }

    public void refreshAllTabs() {
        dashboardScreen.refreshData();
        incomeScreen.refreshData();
        expenseScreen.refreshData();
        budgetScreen.refreshData();
    }

    public VBox getView() {
        return view;
    }

}
