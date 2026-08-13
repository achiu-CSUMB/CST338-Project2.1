package controller;

import java.io.IOException;

import dao.UserDao;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.User;


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
    private ComboBox<String> roleComboBox;

    @FXML
    private Label errorLabel;

    @FXML
    private ComboBox<String> prefixComboBox;

    @FXML
    private TextField teacherNameField;

    private UserDao userDao = new UserDao();

    public void  setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    @FXML
    private void handleCreateAccount() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        String role = roleComboBox.getValue();
        String prefix = prefixComboBox.getValue();
        String teacherName = teacherNameField.getText().trim();

        if (username.isBlank() || password.isBlank() || confirmPassword.isBlank() || role == null) {
            errorLabel.setText("All fields are required.");
            return;
        }

        if (role.equals("Teacher")) {
            if (prefix == null || teacherName.isBlank()) {
                errorLabel.setText("Teacher prefix and name are required.");
                return;
            }
        }

        if (!password.equals(confirmPassword)) {
            errorLabel.setText("Passwords do not match.");
            return;
        }

        User existingUser = userDao.findByUsername(username);

        if (existingUser != null) {
            errorLabel.setText("Username already exists.");
            return;
        }

        User user = new User(username, password, role, prefix, teacherName);

        boolean inserted = userDao.insert(user);

        if(inserted){
            errorLabel.setText("Account Created Successfully! Please log in.");

        } else {
            errorLabel.setText("Cannot create account.");
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
