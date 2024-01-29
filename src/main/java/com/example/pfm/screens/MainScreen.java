package com.example.pfm.screens;

import com.example.pfm.PFMApp;
import com.example.pfm.dao.BudgetDAO;
import com.example.pfm.dao.ExpenseDAO;
import com.example.pfm.dao.IncomeDAO;
import com.example.pfm.model.User;
import javafx.geometry.Side;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * MainScreen class serves as the central hub of the Personal Finance Manager (PFM) application,
 * hosting various functional tabs like Dashboard, Incomes, Expenses, Budgets, and Report.
 * It also provides a user context menu for actions such as logging out.
 */

public class MainScreen{
    private VBox view;
    private BorderPane borderPane;
    private VBox userIconVBox;
    private ImageView userIcon;
    private ContextMenu contextMenu;
    private PFMApp app;
    private IncomeDAO incomeDAO;
    private ExpenseDAO expenseDAO;
    private BudgetDAO budgetDAO;
    private IncomeScreen incomeScreen;
    private ExpenseScreen expenseScreen;
    private BudgetScreen budgetScreen;
    private DashboardScreen dashboardScreen;
    private ReportScreen reportScreen;
    private Stage primaryStage;
    private int userId;

    /**
     * Constructs the MainScreen with necessary dependencies and initializes the UI components.
     *
     * @param app               The main application instance for navigation and data handling.
     * @param incomeDAO         Data access object for income-related operations.
     * @param expenseDAO        Data access object for expense-related operations.
     * @param budgetDAO         Data access object for budget-related operations.
     * @param userId            The ID of the currently logged-in user.
     * @param incomeScreen      The income screen for income management.
     * @param expenseScreen     The expense screen for expense management.
     * @param budgetScreen      The budget screen for budget management.
     * @param dashboardScreen   The dashboard screen for displaying summary information.
     * @param reportScreen      The report screen for generating financial reports.
     * @param primaryStage      The primary stage of the application.
     */

    public MainScreen(PFMApp app,
                      IncomeDAO incomeDAO,
                      ExpenseDAO expenseDAO,
                      BudgetDAO budgetDAO,
                      int userId,
                      IncomeScreen incomeScreen,
                      ExpenseScreen expenseScreen,
                      BudgetScreen budgetScreen,
                      DashboardScreen dashboardScreen,
                      ReportScreen reportScreen,
                      Stage primaryStage) {
        this.app = app;
        this.incomeDAO = incomeDAO;
        this.expenseDAO = expenseDAO;
        this.budgetDAO = budgetDAO;
        this.userId = userId;
        this.incomeScreen = incomeScreen;
        this.expenseScreen = expenseScreen;
        this.budgetScreen = budgetScreen;
        this.dashboardScreen = dashboardScreen;
        this.reportScreen = reportScreen;
        this.primaryStage = primaryStage;
        createView();
    }


    /**
     * Initializes the UI components of the main screen, including the tab pane for different application
     * sections and the user profile icon with its context menu for logging out.
     */
    private void createView() {
        view = new VBox();
        borderPane = new BorderPane();
        borderPane.getStylesheets().add(getClass().getResource("/com/example/pfm/stylesheets/mainscreen.css").toExternalForm());
        TabPane tabPane = new TabPane();

        // Setup for each application tab (Dashboard, Incomes, Expenses, etc.)

        Tab dashboardTab = new Tab("Dashboard");
        dashboardTab.setContent(new DashboardScreen(app, incomeDAO, expenseDAO, budgetDAO, userId).getView());
        dashboardTab.setClosable(false);

        Tab incomesTab = new Tab("Incomes");
        incomesTab.setContent(new IncomeScreen(app, new IncomeDAO(), app.getUserService().getCurrentUserId()).getView());
        incomesTab.setClosable(false);


        Tab expensesTab = new Tab("Expenses");
        expensesTab.setContent(new ExpenseScreen(app, new ExpenseDAO(), app.getUserService().getCurrentUserId()).getView());
        expensesTab.setClosable(false);

        Tab budgetTab = new Tab("Budgets");
        budgetTab.setContent(new BudgetScreen(app, budgetDAO, expenseDAO, userId).getView());
        budgetTab.setClosable(false);

        Tab reportTab = new Tab("Report");
        reportTab.setContent(new ReportScreen(app, incomeDAO, expenseDAO, primaryStage).getView());
        reportTab.setClosable(false);

        tabPane.getTabs().addAll(dashboardTab, incomesTab, expensesTab, budgetTab, reportTab);
        view.getChildren().add(tabPane);

        // Setup for user icon and its click action to open the context menu

        userIconVBox = new VBox();
        userIconVBox.getStyleClass().add("right-panel");

        userIcon = new ImageView(new Image(getClass().getResourceAsStream("/images/icons/profile.png")));
        userIcon.setFitHeight(50);
        userIcon.setFitWidth(50);
        userIcon.setPickOnBounds(true);
        userIcon.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                openContextMenu(e);
            }
        });
        userIconVBox.getChildren().add(userIcon);
        borderPane.setCenter(view);
        borderPane.setRight(userIconVBox);
    }

    /**
     * Handles the opening and closing of the context menu upon clicking the user icon. This menu provides
     * user-specific actions such as logging out.
     *
     * @param e MouseEvent that triggers the context menu action.
     */

    private void openContextMenu(MouseEvent e) {
        if(contextMenu == null) {
            contextMenu = new ContextMenu();
            MenuItem logoutItem = new MenuItem("Logout");
            logoutItem.setOnAction(event -> logout());
            contextMenu.getItems().add(logoutItem);
        }
        if (contextMenu.isShowing()) {
            contextMenu.hide(); // to hide the menu when its already showing
        } else {
            contextMenu.show(userIcon, Side.LEFT, 0, 0);
        }

    }

    /**
     * Logs out the current user and navigates back to the login screen.
     */
    private void logout() {
        app.getUserService().logoutUser();
        app.showLoginScreen();
    }

    /**
     * Provides access to the main screen's view component, which includes all UI elements.
     *
     * @return The BorderPane that contains the main screen's layout.
     */
    public BorderPane getView() {
        return borderPane;
    }

}
