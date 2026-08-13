package controller;

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
 * Date: 8/10/2026
 * Description: Handles account management actions
 */
public class AccountsController {

    private UserDao userDao = new UserDao();
    private User loadedUser;
    private User currentUser;

    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    @FXML
    private TextField usernameField;

    @FXML
    private ComboBox<String> roleComboBox;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label statusLabel;

    @FXML
    private TextField newUsernameField;

    @FXML
    private void handleLoadUser() {
        String username = usernameField.getText().trim();

        if (username.isBlank()) {
            statusLabel.setText("Enter a username");
            return;
        }

        User user = userDao.findByUsername(username);

        if (user == null) {
            statusLabel.setText("User not found");
            loadedUser = null;
            return;
        }

        loadedUser = user;
        roleComboBox.setValue(user.getRole());
        passwordField.clear();
        statusLabel.setText("User loaded");
    }

    @FXML
    private void handleUpdateUser() {
        if (loadedUser == null) {
            statusLabel.setText("Load a user first.");
            return;
        }

        if (currentUser != null && !currentUser.getRole().equals("Admin")) {
            statusLabel.setText("Only admins can change roles.");
            return;
        }

        String role = roleComboBox.getValue();

        if (role == null) {
            statusLabel.setText("Select a role.");
            return;
        }

        loadedUser.setRole(role);

        boolean updated = userDao.update(loadedUser);

        if (updated) {
            statusLabel.setText("User updated successfully.");
        } else {
            statusLabel.setText("Could not update user.");
        }
    }

    @FXML
    private void handleDeleteUser() {
        if (loadedUser == null) {
            statusLabel.setText("Load a user first.");
            return;
        }

        boolean deleted = userDao.delete(loadedUser.getUserId());

        if (deleted) {
            statusLabel.setText("User deleted successfully.");

            usernameField.clear();
            roleComboBox.setValue(null);
            passwordField.clear();
            loadedUser = null;
        } else {
            statusLabel.setText("Could not delete user.");
        }
    }

    @FXML
    private void handleResetPassword() {
        if (loadedUser == null) {
            statusLabel.setText("Load a user first.");
            return;
        }

        String newPassword = passwordField.getText();

        if (newPassword.isBlank()) {
            statusLabel.setText("Enter a new password.");
            return;
        }

        loadedUser.setPassword(newPassword);

        boolean updated = userDao.update(loadedUser);

        if (updated) {
            passwordField.clear();
            statusLabel.setText("Password reset successfully.");
        } else {
            statusLabel.setText("Could not reset password.");
        }
    }

    @FXML
    private void handleChangeUsername() {
        if (loadedUser == null) {
            statusLabel.setText("Load a user first.");
            return;
        }

        String newUsername = newUsernameField.getText().trim();

        if (newUsername.isBlank()) {
        statusLabel.setText("Enter a username.");
        return;
        }

        User existingUser = userDao.findByUsername(newUsername);

        if (existingUser != null) {
            statusLabel.setText("Username already exists.");
            return;
        }

        loadedUser.setUsername(newUsername);
        boolean updated = userDao.update(loadedUser);

        if (updated) {
            usernameField.setText(newUsername);
            newUsernameField.clear();
            statusLabel.setText("Username changed successfully.");
        } else  {
            statusLabel.setText("Could not change username.");
        }
    }

    public void setCurrentUser(User user) {
        currentUser = user;
        loadedUser = user;

        usernameField.setText(user.getUsername());
        roleComboBox.setValue(user.getRole());

        if (!currentUser.getRole().equals("Admin")) {
            roleComboBox.setDisable(true);
        }
    }

    @FXML
    private void handleBack(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/views/menu-view.fxml")
            );

            Parent root = loader.load();

            MenuController controller = loader.getController();
            controller.setCurrentUser(currentUser);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            stage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
