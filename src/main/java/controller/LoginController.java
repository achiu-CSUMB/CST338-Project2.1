package controller;

import dao.UserDao;
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
import model.User;

import java.io.IOException;

/**
 * Author: John Ly
 * Date: 8/3/2026
 * Description: For users interaction on the login scene
 */
public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorLabel;

    @FXML
    private void handleLogin()  {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isBlank() || password.isBlank()) {
            errorLabel.setText("Username and password required.");
            return;
        }

        User user = userDao.findByUsername(username);

        if (user == null) {
            errorLabel.setText("User not found");
            return;
        }

        if (!password.equals(user.getPassword())) {
            errorLabel.setText("Incorrect password.");
            return;
        }

        errorLabel.setText("Login successful.");
    }

    @FXML
    private void handleCreateAccount(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(
                    getClass().getResource("/views/register-view.fxml")
            );

            Parent root = fxmlLoader.load();

            Stage stage = (Stage) ((Node) event.getSource())
                    .getScene()
                    .getWindow();

            stage.setScene(new Scene(root, 600, 500));
        } catch (IOException e) {
            errorLabel.setText("Error loading registration screen.");
            e.printStackTrace();
        }
    }

    private final UserDao userDao = new UserDao();

}
