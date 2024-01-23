package com.example.pfm.screens;

import com.example.pfm.PFMApp;
import com.example.pfm.model.Income;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.time.LocalDate;

public class IncomeEntryScreen {

    private GridPane view;
    private PFMApp app;
    private int userId;
    private TextField amountField;
    private ComboBox<String> sourceDropdown;
    private DatePicker datePicker;

    public IncomeEntryScreen(PFMApp app, int userId) {
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
        Label amountLabel = new Label("Enter your income amount:");
        GridPane.setConstraints(amountLabel, 0, 0);

        // Amount field
        amountField = new TextField();
        amountField.setPromptText("Income Amount");
        GridPane.setConstraints(amountField, 1, 0);

        // Source Label
        Label sourceLabel = new Label("Select your income source:");
        GridPane.setConstraints(sourceLabel, 0, 1);

        // Source Select
        sourceDropdown = new ComboBox<>();
        sourceDropdown.getItems().addAll("Salary", "Freelance", "Investment", "Allowance", "Other");
        sourceDropdown.setPromptText("Select Source");
        GridPane.setConstraints(sourceDropdown, 1, 1);

        // Date Label
        Label dateLabel = new Label("Income date:");
        GridPane.setConstraints(dateLabel, 0, 2);

        // Date Picker
        datePicker = new DatePicker();
        GridPane.setConstraints(datePicker, 1, 2);

        // Save Button
        Button saveButton = new Button("Save");
        GridPane.setConstraints(saveButton, 1, 3);
        saveButton.setOnAction(e -> {
            String amountText = amountField.getText();
            String source = sourceDropdown.getValue();
            LocalDate date = datePicker.getValue();

            if (validateIncomeData(amountText, source, date)) {
                double amount = Double.parseDouble(amountText);
                Income income = new Income(userId, amount, source, date);
                boolean success = app.getUserService().addIncome(income);

                if (success) {
                    clearForm();
                    showAlert(Alert.AlertType.INFORMATION, "Income Added", "Income has been successfully added.", true);
                } else {
                    showAlert(Alert.AlertType.ERROR, "Save Failed", "Failed to add income. Please try again.", false);
                }
            } else {
                showAlert(Alert.AlertType.ERROR, "Invalid Input", "Please check your input and try again.", false);
            }
        });

        Button backButton = new Button("Back");
        backButton.setOnAction(e -> app.showDashboard());
        GridPane.setConstraints(backButton, 1, 4);

        view.getChildren().addAll(amountLabel, amountField, sourceLabel, sourceDropdown, dateLabel, datePicker, saveButton, backButton);
    }

    private void clearForm() {
        amountField.clear();
        sourceDropdown.getSelectionModel().clearSelection();
        datePicker.setValue(null);
    }

    private void showAlert(Alert.AlertType alertType, String title, String message, boolean navigateBack) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        if (navigateBack) {
            alert.setOnHidden(evt -> app.showMainScreen());
        }

        alert.show();
    }

    private boolean validateIncomeData(String amountText, String source, LocalDate date) {
        try {
            double amount = Double.parseDouble(amountText);
            return amount > 0 && source != null && date != null;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public GridPane getView() {
        return view;
    }
}
