package com.example.pfm.screens;

import com.example.pfm.PFMApp;
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
 * The LoginScreen class provides the UI for user login, including fields for username and password,
 * and buttons for login and registration navigation.
 */
public class LoginScreen {
    private UserService userService;
    private PFMApp app;
    private static GridPane view;
    private static HBox hBox;

    /**
     * Constructs a LoginScreen with references to the main application and user service.
     *
     * @param app The main application instance for navigation.
     * @param userService The service for user authentication and management.
     */
    public LoginScreen(PFMApp app, UserService userService) {
        this.app = app;
        this.userService = userService;
        createView();

        view.getStylesheets().add(getClass().getResource("/com/example/pfm/stylesheets/login.css").toExternalForm());
    }

    /**
     * Initializes the UI components for the login screen including input fields, buttons, and layout setup.
     */
    private void createView() {
        hBox = new HBox(20);
        view = new GridPane();
        view.setAlignment(Pos.CENTER);
        view.setHgap(10);
        view.setVgap(10);
        HBox.setMargin(view, new Insets(0, 20, 20, 20));

        Label usernameLabel = new Label("Username:");
        TextField usernameField = new TextField();
        Label passwordLabel = new Label("Password:");
        PasswordField passwordField = new PasswordField();
        Button loginButton = new Button("Login");
        Text noAccountYetText = new Text("No account yet?");
        GridPane.setHalignment(noAccountYetText, HPos.CENTER);
        Button registerButton = new Button("Register");
        GridPane.setHalignment(registerButton, HPos.LEFT);

        loginButton.setOnAction(e -> handleLogin(usernameField.getText(), passwordField.getText()));
        registerButton.setOnAction(e -> app.showRegistrationScreen());

        view.add(usernameLabel, 0,0);
        view.add(usernameField, 1, 0);
        view.add(passwordLabel, 0, 1);
        view.add(passwordField, 1,1);
        view.add(loginButton, 1, 2);
        view.add(noAccountYetText, 0, 3, 2, 1);
        view.add(registerButton, 1, 4);

        Image pfmLogo = new Image(getClass().getResourceAsStream("/images/logo.jpeg"));
        ImageView pfmLogoView = new ImageView(pfmLogo);
        pfmLogoView.setPreserveRatio(true);
        pfmLogoView.setFitWidth(530);

        hBox.getChildren().addAll(view, pfmLogoView);
    }

    /**
     * Handles the login action when the login button is clicked. Authenticates the user and navigates to the main screen
     * or shows an error dialog if authentication fails.
     *
     * @param username The entered username.
     * @param password The entered password.
     */
    private void handleLogin(String username, String password) {
        String result = userService.authenticateUser(username, password);
        if (result == null) {
           app.showMainScreen(); // Navigation to main screen on successful login
        } else {
            showErrorDialog(result); // Display error dialog on failed login
        }
    }

    /**
     * Displays an error dialog with a specified message.
     *
     * @param message The error message to display.
     */

    private void showErrorDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);

        alert.getDialogPane().getStylesheets().add(getClass().getResource("/com/example/pfm/stylesheets/login.css").toExternalForm());

        alert.showAndWait();
    }


    /**
     * Returns the view component of the login screen.
     *
     * @return The Parent view component containing the login form and logo.
     */
    public static Parent getView() {
        return hBox;
    }
}
