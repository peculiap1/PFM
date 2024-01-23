package com.example.pfm.screens;

import com.example.pfm.PFMApp;
import com.example.pfm.model.Expense;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import org.w3c.dom.Text;

import java.time.LocalDate;

public class ExpenseEntryScreen {
    private GridPane view;
    private PFMApp app;
    private int userId;
    private TextField amountField;
    private ComboBox<String> categoryDropdown;
    private DatePicker datePicker;

    public ExpenseEntryScreen(PFMApp app, int userId) {
        this.app = app;
        this.userId = userId;
        createView();
    }

    private void createView() {
        view = new GridPane();
        view.setPadding(new Insets(10, 10, 10, 10));
        view.setVgap(8);
        view.setHgap(10);

        // Amount Label
        Label amountLabel = new Label("Enter an expense amount:");
        GridPane.setConstraints(amountLabel, 0, 0);

        // Amount Field
        amountField = new TextField();
        amountField.setPromptText("Expense Amount");
        GridPane.setConstraints(amountField, 1, 0);

        // Category Label
        Label categoryLabel = new Label("Select the category:");
        GridPane.setConstraints(categoryLabel, 0, 1);

        // Category Select
        categoryDropdown = new ComboBox<>();
        categoryDropdown.getItems().addAll("Groceries", "Shopping", "Utilities", "Entertainment", "Insurance", "Hobbies", "Travel", "Other");
        categoryDropdown.setPromptText("Select Category");
        GridPane.setConstraints(categoryDropdown, 1,1);

        // Date Label
        Label dateLabel = new Label("Expense Date");
        GridPane.setConstraints(dateLabel, 0, 2);

        // Date Picker
        datePicker = new DatePicker();
        GridPane.setConstraints(datePicker, 1, 2);

        // Save Button
        Button saveButton = new Button("Save");
        GridPane.setConstraints(saveButton, 1, 3);
        saveButton.setOnAction(e -> {
            String amountText = amountField.getText();
            String category = categoryDropdown.getValue();
            LocalDate date = datePicker.getValue();

            if (validateExpenseData(amountText, category, date)) {
                double amount = Double.parseDouble(amountText);
                Expense expense = new Expense(userId, amount, category, date);
                boolean success = app.getUserService().addExpense(expense);

                if (success) {
                    clearForm();
                    showAlert(Alert.AlertType.INFORMATION, "Expense Added", "Expense has been successfully added.", true);
                } else {
                    showAlert(Alert.AlertType.ERROR, "Save Failed", "Failed to add expense. Please try again.", false);
                }
            } else {
                showAlert(Alert.AlertType.ERROR, "Invalid Input", "Please check your input and try again.", false);
            }
        });
        Button backButton = new Button("Back");
        backButton.setOnAction(e -> app.showMainScreen());
        GridPane.setConstraints(backButton, 1,4);

        view.getChildren().addAll(amountLabel, amountField, categoryLabel, categoryDropdown, datePicker, saveButton, backButton);
    }

    private void clearForm() {
        amountField.clear();
        categoryDropdown.getSelectionModel().clearSelection();
        datePicker.setValue(null);
    }

    private void showAlert(Alert.AlertType alertType, String title, String message, boolean navigateBack) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        if (navigateBack) {
            alert.setOnHidden(evt -> app.showDashboard());
        }

        alert.show();
    }

    private boolean validateExpenseData(String amountText, String category, LocalDate date) {
        try {
            double amount = Double.parseDouble(amountText);
            return amount > 0 && category != null && date != null;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    public GridPane getView() {
        return view;
    }
}
