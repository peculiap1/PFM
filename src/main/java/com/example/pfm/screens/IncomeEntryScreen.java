package com.example.pfm.screens;

import com.example.pfm.PFMApp;
import com.example.pfm.model.Income;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.time.LocalDate;

/**
 * The IncomeEntryScreen class provides a user interface for entering new income records.
 * It includes form fields for income amount, source, and date, along with save and back buttons.
 */
public class IncomeEntryScreen {
    private GridPane view;
    private PFMApp app;
    private int userId;
    private TextField amountField;
    private ComboBox<String> sourceDropdown;
    private DatePicker datePicker;

    /**
     * Constructs an IncomeEntryScreen with necessary dependencies and initializes the UI components.
     *
     * @param app Reference to the main application object.
     * @param userId ID of the currently logged-in user.
     */

    public IncomeEntryScreen(PFMApp app, int userId) {
        this.app = app;
        this.userId = userId;
        createView();
        view.getStylesheets().add(getClass().getResource("/com/example/pfm/stylesheets/income-entry.css").toExternalForm());
    }

    /**
     * Initializes the view components, including form fields for income details and action buttons.
     */
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
        backButton.setOnAction(e -> app.showMainScreen());
        GridPane.setConstraints(backButton, 1, 4);

        view.getChildren().addAll(amountLabel, amountField, sourceLabel, sourceDropdown, dateLabel, datePicker, saveButton, backButton);
    }

    /**
     * Validates the input data from the form fields before submitting the income.
     *
     * @param amountText The amount of income entered as text.
     * @param source The selected source of income.
     * @param date The selected date of income.
     * @return true if the input data is valid. false if not.
     */
    private boolean validateIncomeData(String amountText, String source, LocalDate date) {
        try {
            double amount = Double.parseDouble(amountText);
            return amount > 0 && source != null && date != null;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message, boolean navigateBack) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        alert.getDialogPane().getStylesheets().add(getClass().getResource("/com/example/pfm/stylesheets/income-entry.css").toExternalForm());


        if (navigateBack) {
            alert.setOnHidden(evt -> app.showMainScreen());
        }

        alert.show();
    }

    /**
     * Clears all form fields after successfully saving an income record or when initiating a new entry.
     */
    private void clearForm() {
        amountField.clear();
        sourceDropdown.getSelectionModel().clearSelection();
        datePicker.setValue(null);
    }

    /**
     * Returns the main view component of the IncomeEntryScreen.
     *
     * @return GridPane containing all UI elements of the IncomeEntryScreen.
     */
    public GridPane getView() {
        return view;
    }
}
