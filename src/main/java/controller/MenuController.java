package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
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
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("No User");
            alert.setHeaderText(null);
            alert.setContentText("Current user was lost.");
            alert.showAndWait();
            return;
        }

        if (!currentUser.getRole().equalsIgnoreCase("TEACHER")) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Access Denied.");
            alert.setHeaderText(null);
            alert.setContentText("Only teachers can create courses.");
            alert.showAndWait();
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/views/courses-view.fxml")
            );

            Parent root = loader.load();

            CoursesController controller = loader.getController();

            controller.setCurrentUser(currentUser);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            stage.getScene().setRoot(root);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleEnrollment(ActionEvent event) {
            if (currentUser == null) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("No User");
                alert.setHeaderText(null);
                alert.setContentText("Current user was lost.");
                alert.showAndWait();
                return;
            }

            if (!currentUser.getRole().equalsIgnoreCase("STUDENT")) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Access Denied.");
                alert.setHeaderText(null);
                alert.setContentText("Only students can enroll in classes.");
                alert.showAndWait();
                return;
            }

            try {
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/views/enrollment-view.fxml")
                );
                Parent root = loader.load();

                EnrollmentController controller = loader.getController();

                controller.setCurrentUser(currentUser);

                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

                stage.getScene().setRoot(root);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    @FXML
    private void handleAssignments(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/views/assignments-view.fxml")
            );

            Parent root = loader.load();

            AssignmentsController controller = loader.getController();
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
    private void handleGrades(ActionEvent event) {
        // Logic to navigate to the course picker, which lets the user choose
        // which course's grades to view before landing on the grades screen.
        if (currentUser == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("No User");
            alert.setHeaderText(null);
            alert.setContentText("Current user was lost.");
            alert.showAndWait();
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/views/course-picker-view.fxml")
            );

            Parent root = loader.load();

            CoursePickerController controller = loader.getController();
            controller.setCurrentUser(currentUser);

            Stage stage = (Stage) ((Node) event.getSource())
                    .getScene()
                    .getWindow();

            stage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
        }
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
