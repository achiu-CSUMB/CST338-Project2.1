package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.User;

/**
 * Author: John Ly
 * Date: 8/13/2026
 * Description: Controller for the main menu scene, handling user interactions and navigation to other scenes.
 */
public class MenuController {

    private User currentUser;

    public void setCurrentUser(User user) {
        currentUser = user;
    }

    @FXML
    private void handleAccounts(ActionEvent event) {
        // Logic to navigate to the Accounts scene
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/views/accounts-view.fxml")
            );

            Parent root = loader.load();

            AccountsController controller = loader.getController();
            controller.setCurrentUser(currentUser);

            Stage stage = (Stage) ((Node) event.getSource())
                    .getScene()
                    .getWindow();

            stage.getScene().setRoot(root);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCourses(ActionEvent event) {
        if (currentUser == null) {
            return;
        }
        if (!currentUser.getRole().equals("TEACHER")) {
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/views/courses-view.fxml")
            );

            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            stage.getScene().setRoot(root);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleEnrollment(ActionEvent event) {
            try {
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/views/enrollment-view.fxml")
                );

                Parent root = loader.load();

                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

                stage.getScene().setRoot(root);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    @FXML
    private void handleAssignments() {
        // Logic to navigate to the Assignments scene
    }

    @FXML
    private void handleGrades() {
        // Logic to navigate to the Grades scene
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        // Logic to handle logout
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/views/login-view.fxml")
            );

            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource())
                    .getScene()
                    .getWindow();

            stage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
