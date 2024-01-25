package com.example.pfm.screens;

import com.example.pfm.PFMApp;
import com.example.pfm.dao.IncomeDAO;
import com.example.pfm.model.Income;
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

public class IncomeScreen implements DataRefresh{
    private VBox view;
    private PFMApp app;
    private IncomeDAO incomeDAO;
    private int userId;

    private BarChart<String, Number> incomeChart;

    public IncomeScreen(PFMApp app, IncomeDAO incomeDAO, int userId) {
        this.app = app;
        app.registerListener(this);
        this.incomeDAO = incomeDAO;
        this.userId = userId;
        createView();
        setupIncomeChart();

        view.getStylesheets().add(getClass().getResource("/com/example/pfm/stylesheets/income.css").toExternalForm());

    }

    private void createView() {
        view = new VBox();
        TableView<Income> incomeTableView = new TableView<>();
        TableColumn<Income, Double> amountColumn = new TableColumn<>("Amount");
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));

        TableColumn<Income, String> sourceColumn = new TableColumn<>("Source");
        sourceColumn.setCellValueFactory(new PropertyValueFactory<>("source"));

        TableColumn<Income, LocalDate> dateColumn = new TableColumn<>("Date");
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));

        incomeTableView.getColumns().addAll(amountColumn, sourceColumn, dateColumn);

        List<Income> incomes = incomeDAO.getAllIncomesByUserId(userId);
        incomeTableView.setItems(FXCollections.observableArrayList(incomes));


        TableColumn<Income, Void> actionsColumn = new TableColumn<>("Actions");
        actionsColumn.setCellFactory(col -> new TableCell<Income, Void>() {
            private final Button editButton = new Button("Edit");
            private final Button deleteButton = new Button("Delete");

            {
                editButton.setOnAction(event -> {
                    Income selectedIncome = getTableView().getItems().get(getIndex());
                    openIncomeEditForm(selectedIncome);

                });
                deleteButton.setOnAction(event -> {
                    Income selectedIncome = getTableView().getItems().get(getIndex());
                    deleteIncome(selectedIncome);
                });
            }

            private void openIncomeEditForm(Income selectedIncome) {
                Dialog<Income> dialog = new Dialog<>();
                dialog.setTitle("Edit Income");

                ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
                dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

                TextField amountField = new TextField(String.valueOf(selectedIncome.getAmount()));
                ComboBox<String> sourceField = new ComboBox<>();
                sourceField.getItems().addAll("Salary", "Freelance", "Investment", "Allowance", "Other");
                sourceField.setValue(selectedIncome.getSource());
                DatePicker datePicker = new DatePicker(selectedIncome.getDate());

                GridPane grid = new GridPane();
                grid.add(new Label("Amount:"), 0, 0);
                grid.add(amountField, 1, 0);
                grid.add(new Label("Source:"), 0, 1);
                grid.add(sourceField, 1, 1);
                grid.add(new Label("Date:"), 0, 2);
                grid.add(datePicker, 1, 2);

                dialog.getDialogPane().setContent(grid);

                dialog.setResultConverter(dialogButton -> {
                    if (dialogButton == saveButtonType) {
                        try {
                            selectedIncome.setAmount(Double.parseDouble((amountField.getText())));
                            selectedIncome.setSource(sourceField.getValue());
                            selectedIncome.setDate(datePicker.getValue());
                            return selectedIncome;
                        } catch (NumberFormatException e) {
                            showAlert("Invalid Input", "Please enter a valid amount.");
                            return null;
                        }
                    }
                    return null;
                });

                Optional<Income> result = dialog.showAndWait();
                result.ifPresent(newIncome -> {
                    boolean updateSuccess = incomeDAO.updateIncome(newIncome);
                    if (updateSuccess) {
                        refreshIncomeTable();
                        app.onDataChanged();
                    } else {
                        showAlert("Update Error", "Could not update the income information.");
                    }
                });
            }

            private void refreshIncomeTable() {
                incomeTableView.setItems(FXCollections.observableArrayList(incomeDAO.getAllIncomesByUserId(userId)));
                updateIncomeChart();
                app.onDataChanged();
            }

            private void showAlert(String title, String message) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle(title);
                alert.setHeaderText(null);
                alert.setContentText(message);
                alert.showAndWait();
            }

            private void deleteIncome(Income selectedIncome) {
                Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete this income?");
                confirmationAlert.setTitle("Confirm Delete");
                confirmationAlert.setHeaderText(null);
                confirmationAlert.setContentText("This action cannot be undone.");

                Optional<ButtonType> result = confirmationAlert.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    // When the user confirms the income deletion
                    boolean deleteSuccess = incomeDAO.deleteIncome(selectedIncome.getId(), selectedIncome.getUserId());
                    if (deleteSuccess) {
                        // When the deletion was successful, the table updates
                        refreshIncomeTable();
                        app.onDataChanged();
                    } else {
                        // When the deletion fails, it shows an error message
                        showAlert("Deletion Error", "Could not delete the income record.");
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
        incomeTableView.getColumns().add(actionsColumn);

        view.getChildren().add(incomeTableView);
    }

    //Monthly incomes total chart
    private void setupIncomeChart() {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        incomeChart = new BarChart<>(xAxis, yAxis);

        xAxis.setLabel("Month");
        yAxis.setLabel("Total Income");

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Monthly Income");

        for (int month = 1; month <= 12; month++) {
            double total = incomeDAO.getTotalIncomeForMonth(userId, month, LocalDate.now().getYear());
            String monthName = Month.of(month).getDisplayName(TextStyle.FULL, Locale.getDefault());
            series.getData().add(new XYChart.Data<>(monthName, total));
        }

        incomeChart.getData().add(series);
        view.getChildren().add(incomeChart);
    }

    private void updateIncomeChart() {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Monthly Income");

        for (int month = 1; month <= 12; month++) {
            double total = incomeDAO.getTotalIncomeForMonth(userId, month, LocalDate.now().getYear());
            String monthName = Month.of(month).getDisplayName(TextStyle.FULL, Locale.getDefault());
            series.getData().add((new XYChart.Data<>(monthName, total)));
        }

        incomeChart.getData().clear();
        incomeChart.getData().add(series);
    }

    @Override
    public void refreshData() {
        updateIncomeChart();
    }


    public VBox getView() {
        return view;
    }
}
