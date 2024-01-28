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

public class RegistrationScreen {
        private PFMApp app;
        private GridPane view;
        private static HBox hBox;
        private UserService userService;

        public RegistrationScreen(PFMApp app, UserService userService) {
            this.app = app;
            this.userService = userService;
            createView();

            view.getStylesheets().add(getClass().getResource("/com/example/pfm/stylesheets/register.css").toExternalForm());
        }

        private void createView() {
            hBox = new HBox(20);
            view = new GridPane();
            view.setAlignment(Pos.CENTER);
            view.setVgap(10);
            view.setHgap(10);
            HBox.setMargin(view, new Insets(0, 20, 20, 20));

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

            view.add(usernameLabel, 0, 0);
            view.add(usernameField, 1, 0);
            view.add(passwordLabel, 0, 1);
            view.add(passwordField, 1, 1);
            view.add(registerButton, 1, 2);
            view.add(alreadyHaveAccountText, 0, 3, 2, 1);
            view.add(loginButton, 1, 4);

            Image pfmLogo = new Image(getClass().getResourceAsStream("/images/logo.jpeg"));
            ImageView pfmLogoView = new ImageView(pfmLogo);
            pfmLogoView.setPreserveRatio(true);
            pfmLogoView.setFitWidth(530);

            hBox.getChildren().addAll(view, pfmLogoView);
        }

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

        private void showErrorDialog(String message) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText(message);

            alert.getDialogPane().getStylesheets().add(getClass().getResource("/com/example/pfm/stylesheets/register.css").toExternalForm());

            alert.showAndWait();
        }

        private void showSuccessDialog(String message) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setHeaderText(null);
            alert.setContentText(message);

            alert.getDialogPane().getStylesheets().add(getClass().getResource("/com/example/pfm/stylesheets/register.css").toExternalForm());

            alert.setOnHidden(evt -> app.showLoginScreen());

            alert.show();
        }
        public Parent getView() {
            return hBox;
        }
    }


