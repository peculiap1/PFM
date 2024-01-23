package com.example.pfm.screens;

import com.example.pfm.PFMApp;
import com.example.pfm.dao.ExpenseDAO;
import com.example.pfm.dao.IncomeDAO;
import com.example.pfm.model.Income;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.VBox;

public class MainScreen {
    private VBox view;
    private PFMApp app;
    private IncomeDAO incomeDAO;
    private ExpenseDAO expenseDAO;

    public MainScreen(PFMApp app, IncomeDAO incomeDAO, ExpenseDAO expenseDAO) {
        this.app = app;
        this.incomeDAO = incomeDAO;
        this.expenseDAO = expenseDAO;
        createView();
    }

    private void createView() {
        view= new VBox();
        TabPane tabPane = new TabPane();

        Tab dashboardTab = new Tab("Dashboard");
        dashboardTab.setContent(new DashboardScreen(app, incomeDAO, expenseDAO).getView());
        dashboardTab.setClosable(false);

        Tab incomesTab = new Tab("Incomes");
        incomesTab.setContent(new IncomeScreen(app, new IncomeDAO(), app.getUserService().getCurrentUserId()).getView());
        incomesTab.setClosable(false);

        Tab expensesTab = new Tab("Expenses");
        expensesTab.setContent(new ExpenseScreen(app, new ExpenseDAO(), app.getUserService().getCurrentUserId()).getView());
        expensesTab.setClosable(false);

        tabPane.getTabs().addAll(dashboardTab, incomesTab,expensesTab);
        view.getChildren().add(tabPane);
    }

    public VBox getView() {
        return view;
    }

}
