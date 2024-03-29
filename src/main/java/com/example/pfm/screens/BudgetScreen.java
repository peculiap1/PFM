package com.example.pfm.screens;

import com.example.pfm.PFMApp;
import com.example.pfm.config.CategoryConfig;
import com.example.pfm.dao.BudgetDAO;
import com.example.pfm.dao.ExpenseDAO;
import com.example.pfm.model.Budget;
import javafx.application.Platform;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Represents the budget screen in the Personal Finance Management (PFM) application.
 * This screen allows the user to add budgets for all their expense categories.
 * It then displays their set limit, and how much of the budget has been spent.
 * It also displays if they went over their set budget.
 */
public class BudgetScreen implements DataRefresh {
    private PFMApp app;
    private BudgetDAO budgetDAO;
    private ExpenseDAO expenseDAO;
    private VBox view;
    private TableView<Budget> budgetTableView;
    private StackedBarChart<String, Number> budgetBarChart;
    private int userId;

    @Override
    public void refreshData() {
        refreshBudgetData();
        refreshBudgetBarChart();
    }

    /**
     * Constructs the BudgetScreen which displays the user's budget information, including a bar chart of budgeted vs. spent amounts for various categories.
     * This screen allows the user to visualize their budget allocations and spending, add new budgets, edit existing ones, and delete budgets as needed.
     *
     * @param app The main application instance, providing access to shared resources and functionality.
     * @param budgetDAO The data access object for budget-related operations, allowing interaction with the database for budget data.
     * @param expenseDAO The data access object for expense-related operations, used to calculate the total spent amount in each budget category.
     * @param userId The unique identifier of the currently logged-in user, used to fetch and manage budgets specific to the user.
     */
    public BudgetScreen(PFMApp app, BudgetDAO budgetDAO, ExpenseDAO expenseDAO, int userId) {
        this.app = app;
        app.registerListener(this); // Registering this screen to listen for data changes
        this.budgetDAO = budgetDAO;
        this.expenseDAO = expenseDAO;
        this.userId = userId;

        // Initializes and sets up the bar chart to display budgeted vs. spent amounts.
        createBudgetBarChart();
        // Sets up the overall layout and UI components of the Budget screen.
        createview();
        // Adds the budget bar chart to the screen's layout.
        addBudgetBarChart();
        // Fetches the latest budget and expense data from the database and refreshes the bar chart.
        refreshBudgetBarChart();

        // Applies the CSS stylesheet to the screen for consistent styling.
        view.getStylesheets().add(getClass().getResource("/com/example/pfm/stylesheets/budget.css").toExternalForm());
    }
    /**
     * Sets up the overall layout for the budget screen, arranging UI components.
     */
    private void createview() {
        view = new VBox();
        budgetTableView = new TableView<>();
        createBudgetTable();
        Node customLegend = createCustomLegend();

        Button addButton = new Button("Add Budget");
        VBox.setMargin(addButton, new Insets(10));
        addButton.setOnAction(e -> showAddEditBudgetForm(null));

        view.getChildren().addAll(budgetTableView, addButton, customLegend);
    }
    /**
     * Initializes and sets up the budget table to display all the user's budgets.
     */
    private void createBudgetTable() {
        TableColumn<Budget, String> categoryColumn = new TableColumn<>("Category");
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));

        TableColumn<Budget, Double> limitColumn = new TableColumn<>("Budget Limit");
        limitColumn.setCellValueFactory(new PropertyValueFactory<>("budgetLimit"));

        TableColumn<Budget, Number> overBudgetColumn = getBudgetNumberTableColumn();

        TableColumn<Budget, Void> actionsColumn = new TableColumn<>("Actions");
        actionsColumn.setCellFactory(col -> new TableCell<Budget, Void>() {
                    private final Button addButton = new Button("Add");
                    private final Button editButton = new Button("Edit");
                    private final Button deleteButton = new Button("Delete");

                    {
                        addButton.setOnAction(e -> showAddEditBudgetForm(null));
                        editButton.setOnAction(e -> {
                            Budget selectedBudget = getTableView().getItems().get(getIndex());
                            showAddEditBudgetForm(selectedBudget);
                        });
                        deleteButton.setOnAction(e -> {
                            Budget selectedBudget = getTableView().getItems().get(getIndex());
                            deleteBudget(selectedBudget);
                        });
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                                setGraphic(new HBox(10, editButton, deleteButton));
                            }
                        }
                });


        budgetTableView.getColumns().addAll(categoryColumn, limitColumn, overBudgetColumn, actionsColumn);

        refreshBudgetTable();
        refreshBudgetBarChart();
    }

    /**
     * Returns an over budget column.
     * This column displays how much the user went over their set budget.
     */
    private static TableColumn<Budget, Number> getBudgetNumberTableColumn() {
        TableColumn<Budget, Number> overBudgetColumn = new TableColumn<>("Over Budget");
        overBudgetColumn.setCellValueFactory(cellData -> {
            Budget budget = cellData.getValue();
            double overAmount = budget.getSpentAmount() - budget.getBudgetLimit();
            return overAmount > 0 ? new SimpleDoubleProperty(overAmount) : new SimpleDoubleProperty(0);
        });

        overBudgetColumn.setCellFactory(column -> new TableCell<Budget, Number>() {
            @Override
            protected void updateItem(Number item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(String.format("€%.2f", item.doubleValue()));
                    if (item.doubleValue() > 0) {
                        setTextFill((Color.RED));
                        setStyle("-fx-font-weight: bold;");
                    } else {
                        setTextFill(Color.BLACK);
                        setStyle("");
                    }
                }
            }
        });
        return overBudgetColumn;
    }

    /**
     * Sets up the bar chart to display budgeted vs. spent amounts.
     */
    private void createBudgetBarChart() {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        budgetBarChart = new StackedBarChart<>(xAxis, yAxis);

        XYChart.Series<String, Number> spentSeries = new XYChart.Series<>();
        spentSeries.setName("Spent");

        XYChart.Series<String, Number> remainingSeries = new XYChart.Series<>();
        remainingSeries.setName("Remaining");

        List<Budget> budgets = budgetDAO.getAllBudgetsByUserId(userId);
        for (Budget budget : budgets) {
            double spent = expenseDAO.getTotalSpentForCategory(userId, budget.getCategory());
            double remaining = budget.getBudgetLimit() - spent;
            spentSeries.getData().add(new XYChart.Data<>(budget.getCategory(), spent));
            remainingSeries.getData().add(new XYChart.Data<>(budget.getCategory(), remaining));
        }

        budgetBarChart.getData().addAll(spentSeries, remainingSeries);
    }

    /**
     * Fetches the latest budget and expense data from the database and refreshes the bar chart.
     */
    private void refreshBudgetTable() {
        List<Budget> budgets = budgetDAO.getAllBudgetsByUserId(userId);
        budgetTableView.setItems(FXCollections.observableArrayList(budgets));
        refreshBudgetData();
    }

    /**
     * Sets up the dialog that displays the form to add a new budget to the user.
     */
    private void showAddEditBudgetForm(Budget budget) {
        //Dialog setup
        Dialog<Budget> dialog = new Dialog<>();
        dialog.setTitle((budget == null) ? "Add New Budget" : "Edit Budget");
        dialog.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        dialog.getDialogPane().setPrefHeight(225);

        dialog.getDialogPane().getStylesheets().add(getClass().getResource("/com/example/pfm/stylesheets/budget.css").toExternalForm());

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);


        // Fields for the form
        ComboBox<String> categoryDropdown= new ComboBox<>();
        categoryDropdown.setPromptText("Category");
        categoryDropdown.getItems().addAll(CategoryConfig.CATEGORIES);

        TextField limitField = new TextField();
        limitField.setPromptText("Budget Limit");

        // To automatically set budget date to the first day of the current month
        LocalDate currentDate = LocalDate.now();
        LocalDate firstDayOfMonth = currentDate.withDayOfMonth(1);


        if (budget != null) {
            categoryDropdown.setValue(budget.getCategory());
            limitField.setText(String.valueOf((budget.getBudgetLimit())));
        }

        GridPane grid = new GridPane();
        grid.add(new Label("Category:"), 0, 0);
        grid.add(categoryDropdown, 1, 0);
        grid.add(new Label("Budget Limit:"), 0, 1);
        grid.add(limitField, 1, 1);

        grid.setVgap(10);

        dialog.getDialogPane().setContent(grid);

        // Convert result to budget when save button is clicked
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                try {
                    String budgetCategory = categoryDropdown.getValue();
                    double budgetLimit = Double.parseDouble(limitField.getText());

                    if (budgetCategory == null) {
                        showAlert("Invalid Input", "Please select a category");
                        return null;
                    }

                    return new Budget(budget == null ? 0 : budget.getId(), userId, categoryDropdown.getValue(), budgetLimit, firstDayOfMonth);

                } catch (NumberFormatException e) {
                    showAlert("Invalid Input", "Please enter a valid budget limit");
                    return null;
                }
            }
            return null;
        });

        Optional<Budget> result = dialog.showAndWait();

        result.ifPresent(newBudget -> {
            if (budget == null) {
                budgetDAO.insertBudget(newBudget);
            } else {
                budgetDAO.updateBudget(newBudget);
            }
            refreshBudgetTable();
            app.onDataChanged();
        });
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        alert.getDialogPane().getStylesheets().add(getClass().getResource("/com/example/pfm/stylesheets/budget.css").toExternalForm());

        alert.showAndWait();
    }

    /**
     * Displays budget deletion confirmation.
     * Deletes user budget on confirmation.
     */
    private void deleteBudget(Budget budget) {
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete this budget?");
        confirmationAlert.setTitle("Confirm Delete");
        confirmationAlert.setHeaderText(null);
        confirmationAlert.setContentText("This action cannot be undone.");

        confirmationAlert.getDialogPane().getStylesheets().add(getClass().getResource("/com/example/pfm/stylesheets/budget.css").toExternalForm());

        Optional<ButtonType> result = confirmationAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            boolean deleteSuccess = budgetDAO.deleteBudget(budget.getId(), budget.getUserId());
            if (deleteSuccess) {
                refreshBudgetTable();
            } else {
                showAlert("Deletion Error", "Could not delete the budget record.");
            }
        }
        refreshBudgetData();
        refreshBudgetBarChart();
    }


    //Refresh methods..
    private void refreshBudgetData() {
        Map<String, Double> spentTotals = expenseDAO.getTotalSpentPerCategory(userId);

        List<Budget> budgets = budgetDAO.getAllBudgetsByUserId(userId);
        budgets.forEach(budget -> {
            Double spentAmount = spentTotals.getOrDefault(budget.getCategory(), 0.0);
            budget.setSpentAmount(spentAmount);
        });

        budgetTableView.setItems(FXCollections.observableArrayList(budgets));
        refreshBudgetBarChart();
    }

    private void refreshBudgetBarChart() {
        budgetBarChart.getData().clear();
        CategoryAxis xAxis = (CategoryAxis) budgetBarChart.getXAxis();
        xAxis.getCategories().clear();

        XYChart.Series<String, Number> spentSeries = new XYChart.Series<>();
        spentSeries.setName("Spent");
        XYChart.Series<String, Number> limitSeries = new XYChart.Series<>();
        limitSeries.setName("Limit");

        List<Budget> budgets = budgetDAO.getAllBudgetsByUserId(userId);
        for (Budget budget : budgets) {
            double spent = expenseDAO.getTotalSpentForCategory(userId, budget.getCategory());
            double limit = budget.getBudgetLimit() - spent;
            spentSeries.getData().add(new XYChart.Data<>(budget.getCategory(), spent));
            limitSeries.getData().add(new XYChart.Data<>(budget.getCategory(), limit));
            xAxis.getCategories().add(budget.getCategory());
        }

        budgetBarChart.getData().addAll(spentSeries, limitSeries);
        budgetBarChart.setLegendVisible(false);

        //Adjusting the Y-axis upper bound
        NumberAxis yAxis = (NumberAxis) budgetBarChart.getYAxis();
        double maxLimit = getMaxBudgetLimit();
        yAxis.setAutoRanging(false);
        yAxis.setUpperBound(maxLimit + 50); // +50 for padding
        yAxis.setTickUnit(50);

        Platform.runLater(() -> applyBarChartStyles(spentSeries, limitSeries));
        budgetBarChart.setAnimated(false); //this is set to false because the animation misaligned the xAis labels
    }

    /**
     * Sets the upperbound of the Y-axis to the highest budget limit
     */
    private double getMaxBudgetLimit() {
        double maxLimit = 0;
        for (Budget budget : budgetDAO.getAllBudgetsByUserId(userId)) {
            if (budget.getBudgetLimit() > maxLimit) {
                maxLimit = budget.getBudgetLimit();
            }
        }
        return maxLimit;
    }

    // Legend for the Barchart
    private Node createCustomLegend() {
        VBox legendbox = new VBox(5);
        legendbox.setAlignment(Pos.CENTER_LEFT);

        //Legend items
        HBox spentLegendItem = createLegendItem("Spent", "budget-spent-bar-square");
        HBox limitLegendItem = createLegendItem("Limit", "budget-limit-bar-square");

        legendbox.getChildren().addAll(spentLegendItem, limitLegendItem);

        return legendbox;
    }

    private HBox createLegendItem(String text, String styleClass) {
        Rectangle colorIndicator = new Rectangle(15, 15);
        colorIndicator.getStyleClass().add(styleClass);

        Label label = new Label(text);
        label.getStyleClass().add("legend-label");

        HBox legendItem = new HBox(5, colorIndicator, label);
        legendItem.setAlignment(Pos.CENTER_LEFT);

        return legendItem;
    }

    // Styling for the Bar Chart

    private void applyBarChartStyles(XYChart.Series<String, Number> spentSeries, XYChart.Series<String, Number> limitSeries) {
        for (XYChart.Data<String, Number> data : spentSeries.getData()) {
            Node node = data.getNode();
            if (node != null) {
                node.getStyleClass().add("budget-spent-bar");
            }
        }
        for (XYChart.Data<String, Number> data : limitSeries.getData()) {
            Node node = data.getNode();
            if (node != null) {
                node.getStyleClass().add("budget-limit-bar");
            }
        }
        for (int i = 0; i < spentSeries.getData().size(); i++) {
            XYChart.Data<String, Number> spentData = spentSeries.getData().get(i);
            XYChart.Data<String, Number> limitData = limitSeries.getData().get(i);
            Node spentNode = spentData.getNode();
            double spentAmount = spentData.getYValue().doubleValue();
            double limitAmount = limitData.getYValue().doubleValue() + spentAmount;

            if (spentNode != null) {
                if (spentAmount > limitAmount) {
                    spentNode.getStyleClass().add("budget-over-spent-bar");
                } else {
                    spentNode.getStyleClass().add("budget-spent-bar");
                }
            }
        }
    }

    private void addBudgetBarChart() {
        Platform.runLater(() -> {
            view.getChildren().add(budgetBarChart);
        });
    }

    /**
     * Returns the view for the budget screen.
     * @return A VBox containing the screen's layout and components.
     */
    public VBox getView() {
        return view;
    }
}
