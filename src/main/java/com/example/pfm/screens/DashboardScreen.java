package com.example.pfm.screens;

import com.example.pfm.PFMApp;
import com.example.pfm.dao.BudgetDAO;
import com.example.pfm.dao.ExpenseDAO;
import com.example.pfm.dao.IncomeDAO;
import com.example.pfm.model.Budget;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.chart.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class DashboardScreen implements DataRefresh{
    private VBox view;
    private PFMApp app;

    private Label totalIncomeLabel;
    private Label totalExpenseLabel;

    private StackedBarChart<Number, String> financeChart;
    private IncomeDAO incomeDAO;
    private ExpenseDAO expenseDAO;
    private BudgetDAO budgetDAO;
    private int userId;

    public DashboardScreen(PFMApp app, IncomeDAO incomeDAO, ExpenseDAO expenseDAO, BudgetDAO budgetDAO, int userId) {
        this.app = app;
        app.registerListener(this);
        this.incomeDAO = incomeDAO;
        this.expenseDAO = expenseDAO;
        this.budgetDAO = budgetDAO;
        this.userId = userId;
        createView();
        createFinanceChart();
        addBudgetPieChartToDashBoard();
        updateTotals();

        view.getStylesheets().add(getClass().getResource("/com/example/pfm/stylesheets/dashboard.css").toExternalForm());

    }

    @Override
    public void refreshData() {
        updateTotals();
        updatePieChart();
    }


    private void createView() {
        view = new VBox(10);
        totalIncomeLabel = new Label("Total Income This Month: Calculating...");
        totalExpenseLabel = new Label("Total Expenses This Month: Calculating... ");

        //Income Button
        Button addIncomeButton = new Button("Add Income");
        addIncomeButton.setOnAction(e -> app.showIncomeEntryScreen());

        //Expense Button
        Button addExpenseButton = new Button("Add Expense");
        addExpenseButton.setOnAction(e -> app.showExpenseEntryScreen());

        /** other UI components **/

        view.getChildren().addAll(addIncomeButton, addExpenseButton, totalIncomeLabel, totalExpenseLabel);
    }

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

    // Pie chart (budgets)
    private PieChart createBudgetPieChart() {
       PieChart pieChart = new PieChart();

        budgetDAO.getAllBudgetsByUserId(userId).forEach(budget -> {
            PieChart.Data slice = new PieChart.Data(
                    budget.getCategory() + ": €" + budget.getSpentAmount(),
                    budget.getSpentAmount()
            );
            pieChart.getData().add(slice);
        });

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


        totalIncomeLabel.setText("Total Income For This Month: " + totalIncome);
        totalExpenseLabel.setText("Total Expenses For This Month: " + totalExpense);

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


    public VBox getView() {
        return view;
    }
}
