package com.example.pfm.screens;

import com.example.pfm.PFMApp;
import com.example.pfm.dao.BudgetDAO;
import com.example.pfm.dao.ExpenseDAO;
import com.example.pfm.dao.IncomeDAO;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.chart.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

/**
 * Represents the dashboard screen in the Personal Finance Management (PFM) application.
 * This is the first screen the user sees after logging in.
 * It shows a high-level summary of the user's incomes and expenses.
 */

public class DashboardScreen implements DataRefresh{
    private VBox view;
    private PFMApp app;
    private Label welcomeLabel;
    private StackedBarChart<Number, String> financeChart;
    private IncomeDAO incomeDAO;
    private ExpenseDAO expenseDAO;
    private BudgetDAO budgetDAO;
    private int userId;


    /**
     * Constructs the BudgetScreen which displays the user's budget information, including a bar chart of budgeted vs. spent amounts for various categories.
     * This screen allows the user to visualize their budget allocations and spending, add new budgets, edit existing ones, and delete budgets as needed.
     *
     * @param app The main application instance, providing access to shared resources and functionality.
     * @param incomeDAO The data access object for income-related operations, used to track and calculate the user's total income for the month.
     * @param expenseDAO The data access object for expense-related operations, used to calculate the total spent amount in each budget category.
     * @param budgetDAO The data access object for budget-related operations, allowing interaction with the database for budget data.
     * @param userId The unique identifier of the currently logged-in user, used to fetch and manage budgets specific to the user.
     */
    public DashboardScreen(PFMApp app, IncomeDAO incomeDAO, ExpenseDAO expenseDAO, BudgetDAO budgetDAO, int userId) {
        this.app = app;
        app.registerListener(this); // Registering this screen to listen for data changes
        this.incomeDAO = incomeDAO;
        this.expenseDAO = expenseDAO;
        this.budgetDAO = budgetDAO;
        this.userId = userId;

        // Sets up the overall layout and UI components of the Budget screen.
        createView();
        // Sets up the finance chart that shows the total income and expenses of the current month.
        createFinanceChart();
        // Displays a pie chart that displays the expense totals for the categories of set budgets.
        addBudgetPieChartToDashBoard();
        // Updates/Refreshes the totals of the finance chart.
        updateTotals();
        // Applies the CSS stylesheet to the screen for consistent styling.
        view.getStylesheets().add(getClass().getResource("/com/example/pfm/stylesheets/dashboard.css").toExternalForm());

    }

    @Override
    public void refreshData() {
        updateTotals();
        updatePieChart();
    }


    private void createView() {
        view = new VBox(10);

        //Welcome Label
        welcomeLabel = new Label();
        String username = app.getUserService().getCurrentUsername();
        if (username != null) {
            welcomeLabel.setText("Welcome, " + username + "!");
        } else {
            welcomeLabel.setText("Welcome!");
        }
        welcomeLabel.getStyleClass().add("welcome-label");
        VBox.setMargin(welcomeLabel, new Insets(10));

        //Income Button
        Button addIncomeButton = new Button("Add Income");
        VBox.setMargin(addIncomeButton, new Insets(10));
        addIncomeButton.setOnAction(e -> app.showIncomeEntryScreen());

        //Expense Button
        Button addExpenseButton = new Button("Add Expense");
        VBox.setMargin(addExpenseButton, new Insets(10));
        addExpenseButton.setOnAction(e -> app.showExpenseEntryScreen());


        view.getChildren().addAll(welcomeLabel, addIncomeButton, addExpenseButton);
    }

    /**
     * Creates the financial summary chart for the current month, displaying income, expenses, and net profit/loss.
     * The chart is set up with a NumberAxis (x-axis) for the amounts and a CategoryAxis (y-axis) for the categories.
     * Grid lines are disabled for a cleaner look, and the chart is added to the main view layout.
     */

    private void createFinanceChart() {
        NumberAxis xAxis = new NumberAxis();
        CategoryAxis yAxis = new CategoryAxis();


        financeChart = new StackedBarChart<>(xAxis, yAxis);
        financeChart.setTitle("Financial Summary - Current Month");
        financeChart.setLegendVisible(false);
        financeChart.setCategoryGap(0);

        xAxis.setLabel("Amount");
        yAxis.setTickLabelGap(10);
        yAxis.setLabel("");

        xAxis.setTickMarkVisible(false);
        xAxis.setTickLabelsVisible(true);
        yAxis.setTickMarkVisible(false);
        yAxis.setTickLabelsVisible(true);

        financeChart.setHorizontalGridLinesVisible(false);
        financeChart.setVerticalGridLinesVisible(false);

        view.getChildren().add(financeChart);
    }

    /**
     * Creates and configures a PieChart to display the breakdown of expenses by category.
     * Each slice represents a category's total spent amount, styled with specific CSS classes based on the category.
     * The pie chart is configured without a legend and with a fixed title.
     *
     * @return The configured PieChart instance with data slices for each budget category.
     */
    private PieChart createBudgetPieChart() {
       PieChart pieChart = new PieChart();

        // Populates the pie chart with slices representing each budget category and its total spent amount.
        budgetDAO.getAllBudgetsByUserId(userId).forEach(budget -> {
            PieChart.Data slice = new PieChart.Data(
                    budget.getCategory() + ": €" + budget.getSpentAmount(),
                    budget.getSpentAmount()
            );
            pieChart.getData().add(slice);
        });

        // Applies CSS styling to each slice based on its category after the chart is rendered.
        Platform.runLater(() -> {
            for (PieChart.Data data : pieChart.getData()) {
                Node sliceNode = data.getNode();
                String category = data.getName().split(":")[0];
                String styleClass = getCategoryStyleClass(category);
                if (sliceNode != null && styleClass != null) {
                    sliceNode.getStyleClass().add(styleClass);
                }
            }
        });

        pieChart.setLegendVisible(false);
        pieChart.setTitle("Expenses Budgets");

        return pieChart;
    }


    private String getCategoryStyleClass(String category) {
        switch (category) {
            case "Shopping":
                return "shopping-slice";
            case "Travel":
                return "travel-slice";
            case "Groceries":
                return "groceries-slice";
            case "Insurance":
                return "insurance-slice";
            case "Utilities":
                return "utilities-slice";
            case "Entertainment":
                return "entertainment-slice";
            case "Hobbies":
                return "hobbies-slice";
            case "Other":
                return "other-slice";
            default:
                return null;
        }
    }

    private void addBudgetPieChartToDashBoard() {
        PieChart budgetPieChart = createBudgetPieChart();
        view.getChildren().add(budgetPieChart);
    }

    private void updatePieChart() {
        PieChart pieChart = createBudgetPieChart();
        for (Node node : view.getChildren()) {
            if (node instanceof PieChart) {
                view.getChildren().remove(node);
                break;
            }
        }
        view.getChildren().add(pieChart);
    }

    private void updateTotals() {
        int userId = app.getUserService().getCurrentUserId();
        double totalIncome = incomeDAO.getTotalIncomeForCurrentMonth(userId);
        double totalExpense = expenseDAO.getTotalExpenseForCurrentMonth(userId);

        double profitOrLoss = Math.abs(totalIncome - totalExpense);

        XYChart.Series<Number, String> incomeSeries = new XYChart.Series<>();
        incomeSeries.setName("Income");
        incomeSeries.getData().add(new XYChart.Data<>(totalIncome, "Income"));


        XYChart.Series<Number, String> expenseSeries = new XYChart.Series<>();
        expenseSeries.setName("Expenses");
        expenseSeries.getData().add(new XYChart.Data<>(totalExpense, "Expenses"));

        XYChart.Series<Number, String> profitSeries = new XYChart.Series<>();
        profitSeries.setName("Profit/Loss");
        profitSeries.getData().add(new XYChart.Data<>(profitOrLoss, "Profit/Loss"));

        financeChart.getData().clear();
        financeChart.getData().addAll(profitSeries, expenseSeries, incomeSeries);

        Platform.runLater(() -> {
            applyCssStyles();
        });
    }

    private void applyCssStyles() {
        for (XYChart.Series<Number, String> series : financeChart.getData()) {
            for (XYChart.Data<Number, String> data : series.getData()) {
                Node node = data.getNode();
                if (node != null) {
                    String color = getColorForSeries(series.getName());
                    node.setStyle("-fx-bar-fill: " + color + ";");
                } else {
                    data.nodeProperty().addListener((obs, oldNode, newNode) -> {
                        if (newNode != null) {
                            String color = getColorForSeries(series.getName());
                            newNode.setStyle("-fx-bar-fill: " + color + ";");
                        }
                    });
                }
            }
        }
    }

    private String getColorForSeries(String seriesName) {
        switch (seriesName) {
            case "Income":
                return "#5D3587";
            case "Expenses":
                return "#2A5694";
            case "Profit/Loss":
                return "#00B4D8";
            default:
                return "gray";
        }
    }

    /**
     * Returns the view for the dashboard screen.
     * @return A VBox containing the screen's layout and components.
     */
    public VBox getView() {
        return view;
    }
}
