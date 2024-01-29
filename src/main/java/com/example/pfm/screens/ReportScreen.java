package com.example.pfm.screens;

import com.example.pfm.PFMApp;
import com.example.pfm.dao.ExpenseDAO;
import com.example.pfm.dao.IncomeDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.print.*;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * ReportScreen class is responsible for generating and displaying financial reports within the Personal
 * Finance Manager (PFM) application. It includes a summary of total income, expenses, net savings, and
 * a detailed breakdown of spending by category.
 */
public class ReportScreen implements DataRefresh {
    private PFMApp app;
    private VBox view;
    private Label totalIncomeLabel;
    private Label totalExpenseLabel;
    private Label netSavingsLabel;
    private Label financialAdviceText;
    private ObservableList<String> spendingBreakdown;
    private ListView<String> listView;

    private IncomeDAO incomeDAO;
    private ExpenseDAO expenseDAO;
    private Stage primaryStage;

    /**
     * Constructs a ReportScreen with necessary dependencies for generating and displaying reports.
     *
     * @param app Reference to the main application instance.
     * @param incomeDAO Data access object for income-related operations.
     * @param expenseDAO Data access object for expense-related operations.
     * @param primaryStage The primary stage of the application.
     */
    public ReportScreen(PFMApp app, IncomeDAO incomeDAO, ExpenseDAO expenseDAO, Stage primaryStage) {
        this.app = app;
        app.registerListener(this); // Registering this screen to listen for data changes
        this.incomeDAO = incomeDAO;
        this.expenseDAO = expenseDAO;
        this.primaryStage = primaryStage;
        createView();  // Initializes the UI components for the report screen
        setupSummarySection();  // Sets up the summary section displaying income, expenses, and net savings
        setupCategorySection(); // Sets up the section displaying spending breakdown by category
        addPrintButton(); // Adds a print button to enable printing the report

        view.getStylesheets().add(getClass().getResource("/com/example/pfm/stylesheets/report.css").toExternalForm());
    }

    @Override
    public void refreshData() {
        updateSummaryValues(); // Updates the summary values whenever data changes
        updateCategorySpending(); // Updates the category-wise spending breakdown
    }

    private void createView() {
        view = new VBox();
        view.setAlignment(Pos.TOP_CENTER);
        view.getStyleClass().add("view");
    }

    private void setupSummarySection() {
        // Report Title Label
        LocalDate currentDate = LocalDate.now();
        String currentMonth = currentDate.getMonth().getDisplayName(TextStyle.FULL, Locale.getDefault()).toUpperCase();
        int currentYear = currentDate.getYear();

        Label reportTitle = new Label("MONTHLY REPORT " + currentMonth + " " + currentYear);
        reportTitle.setId("report-title");

        // Income and expense label
        int userId = app.getUserService().getCurrentUserId();
        double totalIncome = incomeDAO.getTotalIncomeForCurrentMonth(userId);
        double totalExpense = expenseDAO.getTotalExpenseForCurrentMonth(userId);

        Image incomeIcon = new Image(getClass().getResourceAsStream("/images/icons/income-euro.png"));
        ImageView incomeIconView = new ImageView(incomeIcon);
        incomeIconView.setFitWidth(22);
        incomeIconView.setPreserveRatio(true);

        totalIncomeLabel = new Label();
        totalIncomeLabel.setText("Total Income For This Month: €" + totalIncome);
        totalIncomeLabel.setGraphic(incomeIconView);
        totalIncomeLabel.setContentDisplay(ContentDisplay.LEFT);

        Image expenseIcon = new Image(getClass().getResourceAsStream("/images/icons/expense-euro.png"));
        ImageView expenseIconView = new ImageView(expenseIcon);
        expenseIconView.setFitWidth(22);
        expenseIconView.setPreserveRatio(true);

        totalExpenseLabel = new Label();
        totalExpenseLabel.setText("Total Expenses For This Month: €" + totalExpense);
        totalExpenseLabel.setGraphic(expenseIconView);
        totalExpenseLabel.setContentDisplay(ContentDisplay.LEFT);

        //Net Savings Label
        double netSavings = totalIncome - totalExpense;

        Image netSavingsIcon = new Image(getClass().getResourceAsStream("/images/icons/netsavings.png"));
        ImageView netSavingsIconView = new ImageView(netSavingsIcon);
        netSavingsIconView.setFitWidth(22);
        netSavingsIconView.setPreserveRatio(true);

        netSavingsLabel = new Label();
        netSavingsLabel.setText("Total Net Savings For This Month: €" + netSavings);
        netSavingsLabel.setGraphic(netSavingsIconView);

        financialAdviceText = new Label();
        financialAdviceText.setText(getFinancialAdvice(netSavings, totalIncome, totalExpense));
        financialAdviceText.setId("financial-advice-text");
        VBox.setMargin(financialAdviceText, new Insets(20, 0, 20, 0));


        view.getChildren().addAll(reportTitle, totalIncomeLabel, totalExpenseLabel, netSavingsLabel, financialAdviceText);
    }

    /**
     * Generates a simple personalized financial advice based on the user's net savings, total income, and total expenses
     * for the current month. It provides positive feedback for a positive net savings and suggests improvements
     * for a negative net savings.
     *
     * @param netSavings The net savings calculated as the difference between total income and total expenses.
     * @param totalIncome The total income for the current month.
     * @param totalExpense The total expenses for the current month.
     * @return A string containing tailored financial advice.
     */
    private String getFinancialAdvice(double netSavings, double totalIncome, double totalExpense) {
        if (totalIncome == 0 && totalExpense == 0) {
            // No data available yet
            return ""; // Return an empty string or any message indicating no data is available
        }
        if (netSavings > 0) {
            return "Great Job! Your savings are on track for this month. Keep up the good work!";
        } else {
            return "Looks like you've spent more than your income this month. Try to save more next month.";
        }
    }

    /**
     * Updates the summary section with the latest financial data including total income, total expenses,
     * and net savings. It also refreshes the financial advice based on the updated figures.
     */
    private void updateSummaryValues() {
        // Update values
        int userId = app.getUserService().getCurrentUserId();
        double totalIncome = incomeDAO.getTotalIncomeForCurrentMonth(userId);
        double totalExpense = expenseDAO.getTotalExpenseForCurrentMonth(userId);
        double netSavings = totalIncome - totalExpense;

        totalIncomeLabel.setText("Total Income For This Month: €" + totalIncome);
        totalExpenseLabel.setText("Total Expenses For This Month: €" + totalExpense);
        netSavingsLabel.setText("Total Net Savings For This Month: €" + netSavings);
        financialAdviceText.setText(getFinancialAdvice(netSavings, totalIncome, totalExpense));
    }

    /**
     * Sets up the section of the report that displays a breakdown of expenses by category. This section includes
     * a label and a ListView that visually represents each category and its corresponding spending amount.
     */
    private void setupCategorySection() {
        Label categorySpendLabel = new Label();
        categorySpendLabel.setText("Summary Of Category-Wise Spending:");
        categorySpendLabel.setId("category-label");
        VBox.setMargin(categorySpendLabel, new Insets(20, 0, 20,0));

        spendingBreakdown = FXCollections.observableArrayList(getCategorySpending());
        listView = new ListView<>(spendingBreakdown);

        // Custom cell factory to display each spending category with an associated icon.
        listView.setCellFactory(lv -> new ListCell<String>() {
            private final ImageView imageView = new ImageView();

            {
                imageView.setFitWidth(28);
                imageView.setPreserveRatio(true);
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(item);
                    String categoryName = item.split(":")[0].trim(); // This is to extract the category name from the amount
                    Image icon = getCategoryIcon(categoryName);
                    imageView.setImage(icon);
                    setGraphic(imageView);
                }
            }
        });

        view.getChildren().addAll(categorySpendLabel, listView);
    }

    /**
     * Refreshes the category-wise spending breakdown in the ListView to reflect the most current data.
     */
    private void updateCategorySpending() {
        spendingBreakdown.setAll(getCategorySpending());
    }

    /**
     * Retrieves an icon corresponding to a given spending category.
     *
     * @param category The name of the spending category.
     * @return An Image object representing the icon associated with the specified category.
     */
    private Image getCategoryIcon(String category) {
        String iconPath = "";
        switch (category) {
            case "Groceries":
                iconPath = "/images/icons/groceries.png";
                break;
            case "Shopping":
                iconPath = "/images/icons/shopping.png";
                break;
            case "Utilities":
                iconPath = "/images/icons/utilities.png";
                break;
            case "Entertainment":
                iconPath = "/images/icons/entertainment.png";
                break;
            case "Insurance":
                iconPath = "/images/icons/insurance.png";
                break;
            case "Hobbies":
                iconPath = "/images/icons/hobbies.png";
                break;
            case "Travel":
                iconPath = "/images/icons/travel.png";
                break;
            default:
                iconPath = "/images/icons/other.png";
                break;
        }
        return new Image(getClass().getResourceAsStream(iconPath));
    }

    /**
     * Generates a list of strings representing the spending breakdown by category. Each string includes the category
     * name and the total amount spent in that category for the current month.
     *
     * @return A list of strings each representing a category and its total spending.
     */
    private List<String> getCategorySpending() {
        Map<String, Double> spendingByCategory = expenseDAO.getTotalSpentPerCategory(app.getUserService().getCurrentUserId());

        return spendingByCategory.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .map(entry -> String.format("%s: €%.2f", entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    private void addPrintButton() {
        Button printButton = new Button("Print");
        printButton.setOnAction(e -> {
            PrinterJob job = PrinterJob.createPrinterJob();
            if (job != null && job.showPrintDialog(primaryStage)) {
                printButton.setVisible(false); // This is false to prevent the print button showing on the printed content.
                PageLayout pageLayout = job.getPrinter().createPageLayout(Paper.A4, PageOrientation.PORTRAIT, Printer.MarginType.DEFAULT);

                // To Scale the printable area to fit the page
                double scaleX = pageLayout.getPrintableWidth() / view.getBoundsInParent().getWidth();
                double scaleY = pageLayout.getPrintableHeight() / view.getBoundsInParent().getHeight();
                double minimumScale = Math.min(scaleX, scaleY);

                Scale scale = new Scale(minimumScale, minimumScale);
                view.getTransforms().add(scale);

                boolean success = job.printPage(pageLayout, view);
                if (success) {
                    job.endJob();
                }

                // This removes the scale transform after printing
                view.getTransforms().remove(scale);
                printButton.setVisible(true); // Show the button again after printing.
            }
        });
        view.getChildren().add(printButton);
    }


    public VBox getView() {
        return view;
    }
}
