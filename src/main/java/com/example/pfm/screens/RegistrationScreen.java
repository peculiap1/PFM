package com.example.pfm.screens;

import com.example.pfm.PFMApp;
import com.example.pfm.model.User;
import com.example.pfm.service.UserService;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

/**
 * The RegistrationScreen class provides a user interface for new users to register for the Personal Finance
 * Manager (PFM) application. It includes fields for entering a username and password and buttons for
 * submitting the registration or switching to the login screen.
 */
public class RegistrationScreen {
    private PFMApp app;
    private GridPane view;
    private static HBox hBox;
    private UserService userService;

    /**
     * Constructor for RegistrationScreen. Initializes the application context, user service, and creates the view.
     *
     * @param app The main application class, providing access to application-wide functionalities.
     * @param userService The service class for user-related operations such as registration.
     */
    public RegistrationScreen(PFMApp app, UserService userService) {
        this.app = app;
        this.userService = userService;
        createView();

        view.getStylesheets().add(getClass().getResource("/com/example/pfm/stylesheets/register.css").toExternalForm());
    }

    /**
     * Creates the user interface elements for the registration screen, including input fields for username
     * and password, a register button, and a link to switch back to the login screen.
     */
    private void createView() {
        hBox = new HBox(20);
        view = new GridPane();
        view.setAlignment(Pos.CENTER);
        view.setVgap(10);
        view.setHgap(10);
        HBox.setMargin(view, new Insets(0, 20, 20, 20));

        // Creating and placing form elements in the GridPane.
        Label usernameLabel = new Label("Username:");
        TextField usernameField = new TextField();
        Label passwordLabel = new Label("Password:");
        PasswordField passwordField = new PasswordField();
        Button registerButton = new Button("Register");
        Text alreadyHaveAccountText = new Text("Already have an account?");
        GridPane.setHalignment(alreadyHaveAccountText, HPos.CENTER);
        Button loginButton = new Button("Login");
        GridPane.setHalignment(loginButton, HPos.LEFT);

        registerButton.setOnAction(e -> handleRegister(usernameField.getText(), passwordField.getText()));
        loginButton.setOnAction(e -> app.showLoginScreen());

        // Adding elements to the GridPane.
        view.add(usernameLabel, 0, 0);
        view.add(usernameField, 1, 0);
        view.add(passwordLabel, 0, 1);
        view.add(passwordField, 1, 1);
        view.add(registerButton, 1, 2);
        view.add(alreadyHaveAccountText, 0, 3, 2, 1);
        view.add(loginButton, 1, 4);

        // Adding PFM logo to the registration screen.
        Image pfmLogo = new Image(getClass().getResourceAsStream("/images/logo.jpeg"));
        ImageView pfmLogoView = new ImageView(pfmLogo);
        pfmLogoView.setPreserveRatio(true);
        pfmLogoView.setFitWidth(530);

        hBox.getChildren().addAll(view, pfmLogoView);
    }

    /**
     * Handles the registration process when the "Register" button is clicked. Validates the input and calls
     * the UserService to register the user. Displays a dialog based on the registration outcome.
     *
     * @param username The entered username.
     * @param password The entered password.
     */
    private void handleRegister(String username, String password) {
        String result = userService.registerUser(username, password);
        if (result == null) {
            // Registration successful
            showSuccessDialog("Registration successful!");
        } else {
            // Registration failed, show an error message
            showErrorDialog(result);
        }
    }

    /**
     * Shows an error dialog with a custom message.
     *
     * @param message The message to display in the error dialog.
     */
    private void showErrorDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);

        alert.getDialogPane().getStylesheets().add(getClass().getResource("/com/example/pfm/stylesheets/register.css").toExternalForm());

        alert.showAndWait();
    }

    /**
     * Shows a success dialog with a custom message. Automatically navigates to the login screen when the dialog is closed.
     *
     * @param message The message to display in the success dialog.
     */
    private void showSuccessDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);

        alert.getDialogPane().getStylesheets().add(getClass().getResource("/com/example/pfm/stylesheets/register.css").toExternalForm());

        alert.setOnHidden(evt -> app.showLoginScreen());

        alert.show();
    }

    /**
     * Returns the HBox containing the registration form and logo as the root element of this screen.
     *
     * @return The root Parent element of the registration screen.
     */
    public Parent getView() {
        return hBox;
    }
}


