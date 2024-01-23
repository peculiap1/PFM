package com.example.pfm.screens;

import com.example.pfm.PFMApp;
import com.example.pfm.dao.ExpenseDAO;
import com.example.pfm.model.Expense;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class ExpenseScreen {
    private VBox view;
    private PFMApp app;
    private ExpenseDAO expenseDAO;
    private int userId;

    private BarChart<String, Number> expenseChart;

    public ExpenseScreen(PFMApp app, ExpenseDAO expenseDAO, int userId) {
        this.app = app;
        this.expenseDAO = expenseDAO;
        this.userId = userId;
        createView();
        setupExpenseChart();

        view.getStylesheets().add(getClass().getResource("/com/example/pfm/stylesheets/Expense.css").toExternalForm());

    }

    private void createView() {
        view = new VBox();
        TableView<Expense> ExpenseTableView = new TableView<>();
        TableColumn<Expense, Double> amountColumn = new TableColumn<>("Amount");
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));

        TableColumn<Expense, String> categoryColumn = new TableColumn<>("Category");
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));

        TableColumn<Expense, LocalDate> dateColumn = new TableColumn<>("Date");
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));

        ExpenseTableView.getColumns().addAll(amountColumn, categoryColumn, dateColumn);

        List<Expense> Expenses = expenseDAO.getAllExpensesByUserId(userId);
        ExpenseTableView.setItems(FXCollections.observableArrayList(Expenses));


        TableColumn<Expense, Void> actionsColumn = new TableColumn<>("Actions");
        actionsColumn.setCellFactory(col -> new TableCell<Expense, Void>() {
            private final Button editButton = new Button("Edit");
            private final Button deleteButton = new Button("Delete");

            {
                editButton.setOnAction(event -> {
                    Expense selectedExpense = getTableView().getItems().get(getIndex());
                    openExpenseEditForm(selectedExpense);

                });
                deleteButton.setOnAction(event -> {
                    Expense selectedExpense = getTableView().getItems().get(getIndex());
                    deleteExpense(selectedExpense);
                });
            }

            private void openExpenseEditForm(Expense selectedExpense) {
                Dialog<Expense> dialog = new Dialog<>();
                dialog.setTitle("Edit Expense");

                ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
                dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

                TextField amountField = new TextField(String.valueOf(selectedExpense.getAmount()));
                ComboBox<String> categoryField = new ComboBox<>();
                categoryField.getItems().addAll("Groceries", "Shopping", "Utilities", "Entertainment", "Insurance", "Hobbies", "Travel", "Other");
                categoryField.setValue(selectedExpense.getCategory());
                DatePicker datePicker = new DatePicker(selectedExpense.getDate());

                GridPane grid = new GridPane();
                grid.add(new Label("Amount:"), 0, 0);
                grid.add(amountField, 1, 0);
                grid.add(new Label("Category:"), 0, 1);
                grid.add(categoryField, 1, 1);
                grid.add(new Label("Date:"), 0, 2);
                grid.add(datePicker, 1, 2);

                dialog.getDialogPane().setContent(grid);

                dialog.setResultConverter(dialogButton -> {
                    if (dialogButton == saveButtonType) {
                        try {
                            selectedExpense.setAmount(Double.parseDouble((amountField.getText())));
                            selectedExpense.setCategory(categoryField.getValue());
                            selectedExpense.setDate(datePicker.getValue());
                            return selectedExpense;
                        } catch (NumberFormatException e) {
                            showAlert("Invalid Input", "Please enter a valid amount.");
                            return null;
                        }
                    }
                    return null;
                });

                Optional<Expense> result = dialog.showAndWait();
                result.ifPresent(newExpense -> {
                    boolean updateSuccess = expenseDAO.updateExpense(newExpense);
                    if (updateSuccess) {
                        refreshExpenseTable();
                    } else {
                        showAlert("Update Error", "Could not update the Expense information.");
                    }
                });
            }

            private void refreshExpenseTable() {
                ExpenseTableView.setItems(FXCollections.observableArrayList(expenseDAO.getAllExpensesByUserId(userId)));
                updateExpenseChart();
            }

            private void showAlert(String title, String message) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle(title);
                alert.setHeaderText(null);
                alert.setContentText(message);
                alert.showAndWait();
            }

            private void deleteExpense(Expense selectedExpense) {
                Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete this expense?");
                confirmationAlert.setTitle("Confirm Delete");
                confirmationAlert.setHeaderText(null);
                confirmationAlert.setContentText("This action cannot be undone.");

                Optional<ButtonType> result = confirmationAlert.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    // When the user confirms the expense deletion
                    boolean deleteSuccess = expenseDAO.deleteExpense(selectedExpense.getId(), selectedExpense.getUserId());
                    if (deleteSuccess) {
                        // When the deletion was successful, the table updates
                        refreshExpenseTable();
                    } else {
                        // When the deletion fails, it shows an error message
                        showAlert("Deletion Error", "Could not delete the expense record.");
                    }
                }
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
        ExpenseTableView.getColumns().add(actionsColumn);

        view.getChildren().add(ExpenseTableView);
    }

    //Monthly Expenses total chart
    private void setupExpenseChart() {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        expenseChart = new BarChart<>(xAxis, yAxis);

        xAxis.setLabel("Month");
        yAxis.setLabel("Total Expense");

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Monthly Expense");

        for (int month = 1; month <= 12; month++) {
            double total = expenseDAO.getTotalExpenseForMonth(userId, month, LocalDate.now().getYear());
            String monthName = Month.of(month).getDisplayName(TextStyle.FULL, Locale.getDefault());
            series.getData().add(new XYChart.Data<>(monthName, total));
        }

        expenseChart.getData().add(series);
        view.getChildren().add(expenseChart);
    }

    private void updateExpenseChart() {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Monthly Expense");

        for (int month = 1; month <= 12; month++) {
            double total = expenseDAO.getTotalExpenseForMonth(userId, month, LocalDate.now().getYear());
            String monthName = Month.of(month).getDisplayName(TextStyle.FULL, Locale.getDefault());
            series.getData().add((new XYChart.Data<>(monthName, total)));
        }

        expenseChart.getData().clear();
        expenseChart.getData().add(series);
    }

    public VBox getView() {
        return view;
    }
}
