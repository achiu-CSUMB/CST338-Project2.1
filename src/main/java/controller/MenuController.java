package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
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

            Stage stage = (Stage) ((Node) event.getSource())
                    .getScene()
                    .getWindow();

            stage.getScene().setRoot(root);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCourses() {
        // Logic to navigate to the Courses scene
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
    private void handleLogout() {
        // Logic to handle logout
    }

}
