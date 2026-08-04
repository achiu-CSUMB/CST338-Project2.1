package controller;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;



/**
 * Author: John Ly
 * Date: 8/3/2026
 * Description:
 */
public class RegisterController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private Label errorLabel;

    @FXML
    private void handleCreateAccount() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (username.isBlank() || password.isBlank() || confirmPassword.isBlank()) {
            errorLabel.setText("All fields are required.");
        } else if (!password.equals(confirmPassword)) {
            errorLabel.setText("Passwords do not match.");
        } else {
            errorLabel.setText("Account info is valid");
            // Proceed with account creation logic
        }
    }

    @FXML
    private void handleBackToLogin(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(
                    getClass().getResource("/views/login-view.fxml")
            );

            Parent root = fxmlLoader.load();

            Stage stage = (Stage) ((Node) event.getSource())
                    .getScene()
                    .getWindow();

            stage.setScene(new Scene(root, 600, 500));
        } catch (IOException e) {
            errorLabel.setText("Error loading login screen.");
            e.printStackTrace();
        }
    }
}
